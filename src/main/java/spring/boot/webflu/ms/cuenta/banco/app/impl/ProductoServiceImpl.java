package spring.boot.webflu.ms.cuenta.banco.app.impl;

import java.util.ArrayList;
import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import spring.boot.webflu.ms.cuenta.banco.app.client.ClientClient;
import spring.boot.webflu.ms.cuenta.banco.app.dao.ProductoDao;
import spring.boot.webflu.ms.cuenta.banco.app.dao.TipoProductoDao;
import spring.boot.webflu.ms.cuenta.banco.app.exception.RequestException;
import spring.boot.webflu.ms.cuenta.banco.app.models.CurrentAccount;
import spring.boot.webflu.ms.cuenta.banco.app.models.TypeCurrentAccount;
import spring.boot.webflu.ms.cuenta.banco.app.models.Client;
import spring.boot.webflu.ms.cuenta.banco.app.service.ProductoService;
import spring.boot.webflu.ms.cuenta.banco.app.service.TipoProductoService;


@Service
public class ProductoServiceImpl implements ProductoService {

//	@Value("{${com.bootcamp.gateway.url}}")
	String valor;
	
	private static final Logger log = LoggerFactory.getLogger(ProductoServiceImpl.class);
	
	@Autowired
	public ProductoDao productoDao;

	@Autowired
	public TipoProductoDao tipoProductoDao;

	@Autowired
	private TipoProductoService tipoProductoService;
	
	//consultar otros ms-cliente
	@Autowired
	private ClientClient clientClient;

	@Override
	public Flux<CurrentAccount> findAllProducto() {
		return productoDao.findAll();

	}

	@Override
	public Mono<CurrentAccount> findByIdProducto(String id) {
		return productoDao.findById(id);

	}

	@Override
	public Flux<CurrentAccount> findAllProductoByDniCliente(String dniCliente) {

		return productoDao.viewDniCliente(dniCliente);
	}

	@Override
	public Mono<CurrentAccount> retiro(Double monto, String numTarjeta, Double comision) {

		return productoDao.viewNumTarjeta(numTarjeta).flatMap(c -> {

			if (monto < c.getSaldo()) {
				c.setSaldo((c.getSaldo() - monto) - comision);

				return productoDao.save(c);
			}
			return Mono.error(new InterruptedException("No tiene el saldo suficiente para retirar"));
		});
	}

	@Override
	public Mono<CurrentAccount> depositos(Double monto, String numTarjeta, Double comision) {
		return productoDao.viewNumTarjeta(numTarjeta).flatMap(c -> {
			c.setSaldo((c.getSaldo() + monto) - comision);
			return productoDao.save(c);
		});
	}

	@Override
	public Flux<CurrentAccount> saveProductoList(CurrentAccount producto) {

		List<CurrentAccount> listProducto = new ArrayList<CurrentAccount>();
		listProducto.add(producto);
		
		Flux<CurrentAccount> fMono = Flux.fromIterable(listProducto);
		
		/*
		tipo producto
		Ahorro = 1
		Cuentas corrientes  = 2
		Cuentas a plazo fijo = 3
		cuenta ahorro personal VIP 0 = 4
		cuenta corriente personal VIP = 5
		empresarial PYME  = 6 
		empresarial Corporative = 7
		cuenta plazo fijo VIP = 8

		 */
		return fMono.filter(ff -> {
			if (ff.getTipoProducto().getIdTipo().equalsIgnoreCase("1")
					|| ff.getTipoProducto().getIdTipo().equalsIgnoreCase("2")
					|| ff.getTipoProducto().getIdTipo().equalsIgnoreCase("3")
					|| ff.getTipoProducto().getIdTipo().equalsIgnoreCase("4")
					|| ff.getTipoProducto().getIdTipo().equalsIgnoreCase("5")
					|| ff.getTipoProducto().getIdTipo().equalsIgnoreCase("6")
					|| ff.getTipoProducto().getIdTipo().equalsIgnoreCase("7")
					|| ff.getTipoProducto().getIdTipo().equalsIgnoreCase("8")) {
				return true;
			}
			return false;
		}).flatMap(f -> {
			//buscar el numero de documento en ms-cliente
			Mono<Client> cli = clientClient.findByNumDoc(f.getDni());
			
			log.info("datos cliente --->> "+cli.toString());
			
			//la consulta hacia el ms-clientes
//			Mono<Client> cli = WebClient.builder().baseUrl(valor+"/clientes/api/Clientes/").build().get()
//					.uri("/dni/" + f.getDni()).retrieve().bodyToMono(Client.class).log();

			return cli.flatMap(p -> {

				/*
				  
				tipo cliente
				personal = 1
				empresarial= 2
				personal vip = 3
				empresarial pyme = 4
				empresarial corporativo = 5 
				
				*/				
				if (p.getTipoCliente().getIdTipo().equalsIgnoreCase("1")) { //personal = 1

					//busca si el cliente personal ya tiene un producto creado
					Mono<Long> valor = productoDao.viewDniCliente2(f.getDni(), f.getTipoProducto().getIdTipo()).count();

					System.out.println("clientes ---> " + valor);
					
					//
					return valor.flatMap(f2 -> {
						//si tiene almenos una cuenta creada de uno de los productos
						if (f2 >= 1) {
							//verifica si ya se tiene ese tipo de producto para ese 
							//cliente , si ya la tiene no la crea
							if (!f.getTipoProducto().getIdTipo().equalsIgnoreCase("1")
									&& !f.getTipoProducto().getIdTipo().equalsIgnoreCase("2")
									&& !f.getTipoProducto().getIdTipo().equalsIgnoreCase("3")) {

								CurrentAccount f1 = new CurrentAccount();

								f1.setDni(f.getDni());
								f1.setNumero_cuenta(f.getNumero_cuenta());
								f1.setFecha_afiliacion(f.getFecha_afiliacion());
								f1.setFecha_caducidad(f.getFecha_caducidad());
								f1.setSaldo(f.getSaldo());
								f1.setUsuario(f.getClave());
								f1.setClave(f.getClave());

								TypeCurrentAccount t = new TypeCurrentAccount();
								t.setIdTipo(f.getTipoProducto().getIdTipo());
								t.setDescripcion(f.getTipoProducto().getDescripcion());

								f1.setTipoProducto(t);

								return productoDao.save(f1);
							} else {
								throw new RequestException("El Cliente ya tiene una cuenta bancaria de ese tipo");
							}
						
						//cuando no tien una cuenta creada de ninguno de los tres productos
						} else {

							CurrentAccount f1 = new CurrentAccount();

							f1.setDni(f.getDni());
							f1.setNumero_cuenta(f.getNumero_cuenta());
							f1.setFecha_afiliacion(f.getFecha_afiliacion());
							f1.setFecha_caducidad(f.getFecha_caducidad());
							f1.setSaldo(f.getSaldo());
							f1.setUsuario(f.getClave());
							f1.setClave(f.getClave());

							TypeCurrentAccount t = new TypeCurrentAccount();
							t.setIdTipo(f.getTipoProducto().getIdTipo());
							t.setDescripcion(f.getTipoProducto().getDescripcion());

							f1.setTipoProducto(t);

							return productoDao.save(f1);

						}
					});
				
				
				} else if (p.getTipoCliente().getIdTipo().equalsIgnoreCase("2")) { //empresarial= 2
					
					//solamente puede tener tipo de cuenta 2 = cuenta corriente
					if (!f.getTipoProducto().getIdTipo().equalsIgnoreCase("2")) {

						throw new RequestException("Cliente Empresarial no puede tener cuenta de ese tipo");
					}

					CurrentAccount f1 = new CurrentAccount();

					f1.setDni(f.getDni());
					f1.setNumero_cuenta(f.getNumero_cuenta());
					f1.setFecha_afiliacion(f.getFecha_afiliacion());
					f1.setFecha_caducidad(f.getFecha_caducidad());
					f1.setSaldo(f.getSaldo());
					f1.setUsuario(f.getClave());
					f1.setClave(f.getClave());

					TypeCurrentAccount t = new TypeCurrentAccount();
					t.setIdTipo(f.getTipoProducto().getIdTipo());
					t.setDescripcion(f.getTipoProducto().getDescripcion());
					f1.setTipoProducto(t);

					return productoDao.save(f1);

				} else if (p.getTipoCliente().getIdTipo().equalsIgnoreCase("3")) { //personal vip = 3
					
					//no pude crear ninguno de tipo 4,5 y 8
					//-->>>>>>>>>>>>>>>>>>>>>>>>>VERIFICAR ESTA RESTRICCION
					if (!f.getTipoProducto().getIdTipo().equalsIgnoreCase("4")
							&& !f.getTipoProducto().getIdTipo().equalsIgnoreCase("5")
							&& !f.getTipoProducto().getIdTipo().equalsIgnoreCase("8")) {

						throw new RequestException("Un Cliente Personal VIP" + 
						" no puede tener este tipo de cuenta");

					}else if(!(f.getSaldo() >= 20)) { 
					
						throw new RequestException("La cuenta se apertura con un saldo mayor a S/.20");
					}else {
						
					
						CurrentAccount f1 = new CurrentAccount();
						f1.setDni(f.getDni());
						f1.setNumero_cuenta(f.getNumero_cuenta());
						f1.setFecha_afiliacion(f.getFecha_afiliacion());
						f1.setFecha_caducidad(f.getFecha_caducidad());
						f1.setSaldo(f.getSaldo());
						f1.setUsuario(f.getClave());
						f1.setClave(f.getClave());

						TypeCurrentAccount t = new TypeCurrentAccount();
						t.setIdTipo(f.getTipoProducto().getIdTipo());
						t.setDescripcion(f.getTipoProducto().getDescripcion());
						f1.setTipoProducto(t);
						return productoDao.save(f1);

					}
				}else if (p.getTipoCliente().getIdTipo().equalsIgnoreCase("4")) { //empresarial pyme = 4
					
					//-->>>>>>>>>>>>>>>>>>>>>>>>>VERIFICAR ESTA RESTRICCION
					if (!f.getTipoProducto().getIdTipo().equalsIgnoreCase("6")) {

						throw new RequestException("Un Cliente Empresarial PYME" + 
						" no puede tener este tipo de cuenta");

					}else if(!(f.getSaldo() >= 50)) { 
					
						throw new RequestException("La cuenta se apertura con un saldo mayor a S/.50");
				
					}else {
						CurrentAccount f1 = new CurrentAccount();

						f1.setDni(f.getDni());
						f1.setNumero_cuenta(f.getNumero_cuenta());
						f1.setFecha_afiliacion(f.getFecha_afiliacion());
						f1.setFecha_caducidad(f.getFecha_caducidad());
						f1.setSaldo(f.getSaldo());
						f1.setUsuario(f.getClave());
						f1.setClave(f.getClave());

						TypeCurrentAccount t = new TypeCurrentAccount();
						t.setIdTipo(f.getTipoProducto().getIdTipo());
						t.setDescripcion(f.getTipoProducto().getDescripcion());
						f1.setTipoProducto(t);
						return productoDao.save(f1);

					}
				}else if (p.getTipoCliente().getIdTipo().equalsIgnoreCase("5")) { //empresarial corporativo = 5 
					//-->>>>>>>>>>>>>>>>>>>>>>>>>VERIFICAR ESTA RESTRICCION
					if (!f.getTipoProducto().getIdTipo().equalsIgnoreCase("7")) {

						throw new RequestException("Un Cliente Empresarial Corporativo" + 
						" no puede tener este tipo de cuenta");

					}else if(!(f.getSaldo() >= 100)) { 
							
							throw new RequestException("La cuenta se apertura con un saldo mayor a S/.100");
					
					} else {
						CurrentAccount f1 = new CurrentAccount();

						f1.setDni(f.getDni());
						f1.setNumero_cuenta(f.getNumero_cuenta());
						f1.setFecha_afiliacion(f.getFecha_afiliacion());
						f1.setFecha_caducidad(f.getFecha_caducidad());
						f1.setSaldo(f.getSaldo());
						f1.setUsuario(f.getClave());
						f1.setClave(f.getClave());

						TypeCurrentAccount t = new TypeCurrentAccount();
						t.setIdTipo(f.getTipoProducto().getIdTipo());
						t.setDescripcion(f.getTipoProducto().getDescripcion());
						f1.setTipoProducto(t);
						return productoDao.save(f1);

					}
				}
				return Mono.empty();

			});
		});

	}

	@Override
	public Mono<CurrentAccount> listProdNumTarj(String num) {

		return productoDao.viewNumTarjeta(num);
	}
	
	@Override
	public Mono<CurrentAccount> saveProducto(CurrentAccount producto) {
		// TODO Auto-generated method stub
		return productoDao.save(producto);
	}

}
