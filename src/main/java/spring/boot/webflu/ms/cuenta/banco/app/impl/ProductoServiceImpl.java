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
import spring.boot.webflu.ms.cuenta.banco.app.client.CreditoClient;
import spring.boot.webflu.ms.cuenta.banco.app.dao.ProductoDao;
import spring.boot.webflu.ms.cuenta.banco.app.dao.TipoProductoDao;
import spring.boot.webflu.ms.cuenta.banco.app.documents.CurrentAccount;
import spring.boot.webflu.ms.cuenta.banco.app.documents.TypeCurrentAccount;
import spring.boot.webflu.ms.cuenta.banco.app.dto.Client;
import spring.boot.webflu.ms.cuenta.banco.app.dto.CreditAccount;
import spring.boot.webflu.ms.cuenta.banco.app.exception.RequestException;
import spring.boot.webflu.ms.cuenta.banco.app.service.ProductoService;
import spring.boot.webflu.ms.cuenta.banco.app.service.TipoProductoService;


@Service
public class ProductoServiceImpl implements ProductoService {

//	@Value("{${com.bootcamp.gateway.url}}")
	//String valor;
	
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
	
	@Autowired
	private CreditoClient creditoClient;

	@Override
	public Flux<CurrentAccount> findAllProductoBanco() {
		return productoDao.findAll();

	}

	@Override
	public Mono<CurrentAccount> findByIdProductoBanco(String id) {
		return productoDao.findById(id);

	}

	@Override
	public Flux<CurrentAccount> findAllProductoByDniCliente(String dniCliente) {

		return productoDao.viewDniCliente(dniCliente);
	}

	@Override
	public Mono<CurrentAccount> retiro(Double monto, String numTarjeta, Double comision, String codigo_bancario) {
		//BUSCA EL NUMERO DE LA CUENTA-TARJETA CON SU BANCO CORRESPONDIENTE
		//PARA OBTERNER TODOS LOS DATOS PARA QUITAR EL MONTO
		
		System.out.println("llego desde el controlador");
		
		return productoDao.viewNumTarjeta(numTarjeta,codigo_bancario).flatMap(c -> {

			System.out.println(c.toString());
			
			if (monto < c.getSaldo()) {
				c.setSaldo((c.getSaldo() - monto) - comision);

				return productoDao.save(c);
			}
			return Mono.error(new InterruptedException("SALDO INSUFICIENTE"));
		});
	}

	@Override
	public Mono<CurrentAccount> depositos(Double monto, String numTarjeta, Double comision, String codigo_bancario) {
		return productoDao.viewNumTarjeta(numTarjeta,codigo_bancario).flatMap(c -> {
			c.setSaldo((c.getSaldo() + monto) - comision);
			return productoDao.save(c);
		});
	}

	@Override
	public Flux<CurrentAccount> saveProductoBancoCliente(CurrentAccount producto) {

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
			
			//consumir el servicio credito para saver si tiene una 
			//deuda o  no
			
			Flux<CreditAccount> cred = creditoClient.findByNumDoc(f.getDni());
				
			return cred.flatMap(ba -> {
			
				if(ba.getConsumo() > 0) {
					throw new RequestException("TIENES UNA DEUDA - NO PUEDES ADQUIRIR UN PRODUCTO");
				}
				
				//buscar el numero de documento en ms-cliente
				
				System.out.println("El DNI es : --->" + f.getDni());
				
				Mono<Client> cli = clientClient.findByNumDoc(f.getDni());
				
				log.info("datos cliente --->> "+cli.map(c-> "DNI : " + c.getDni()));

				return cli.flatMap(p -> {
					
					//compara el codigo del cliente consultardo 
					//con el codigo que se esta mandando en cuenta banco
					if(!p.getCodigoBanco().equalsIgnoreCase(f.getCodigoBanco())) {
						
						throw new RequestException("LA CUENTA-PRODUCTO DEL CLIENTE NO PERTENECE AL BANCO");
					
					}else{
					
					/*
					  
					tipo cliente
					personal = 1
					empresarial= 2
					personal vip = 3
					empresarial pyme = 4
					empresarial corporativo = 5 
					
					*/		
						
					if (p.getTipoCliente().getIdTipo().equalsIgnoreCase("1")) { //personal = 1

						//BUSCA SI EL CLIENTE PERSONAL TIENE UN PRODUCTO YA CREADO
						Mono<Long> valor = productoDao
								.viewDniCliente2(f.getDni(), f.getTipoProducto().getIdTipo(),f.getCodigoBanco()).count();

						System.out.println("clientes ---> " + valor);
					
						return valor.flatMap(f2 -> {
							//TIENE ALMNOS UNA CUENTA CREADA
							if (f2 >= 1) {
								
								//CLIENTE PERSONAL SOLO PUEDE TENER UN PRODUCTO
								//VERIFICA QUE NO TENGA CREADO UNA DE CUENTA : AHORRO, CORRIENTE, PLAZO FIJO
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
									f1.setCodigoBanco(f.getCodigoBanco());

									TypeCurrentAccount t = new TypeCurrentAccount();
									t.setIdTipo(f.getTipoProducto().getIdTipo());
									t.setDescripcion(f.getTipoProducto().getDescripcion());

									f1.setTipoProducto(t);

									return productoDao.save(f1);
								} else {
									throw new RequestException("PERSONAL TIENE UNA CUENTA BANCARIA DE ESTE TIPO");
								}
							
							//SINO TIENE NINGUNA CUENTA CREADA
							} else {

								CurrentAccount f1 = new CurrentAccount();

								f1.setDni(f.getDni());
								f1.setNumero_cuenta(f.getNumero_cuenta());
								f1.setFecha_afiliacion(f.getFecha_afiliacion());
								f1.setFecha_caducidad(f.getFecha_caducidad());
								f1.setSaldo(f.getSaldo());
								f1.setUsuario(f.getClave());
								f1.setClave(f.getClave());
								f1.setCodigoBanco(f.getCodigoBanco());

								TypeCurrentAccount t = new TypeCurrentAccount();
								t.setIdTipo(f.getTipoProducto().getIdTipo());
								t.setDescripcion(f.getTipoProducto().getDescripcion());

								f1.setTipoProducto(t);

								return productoDao.save(f1);

							}
						});
					
			
					} else if (p.getTipoCliente().getIdTipo().equalsIgnoreCase("2")) { //empresarial= 2
						
						//CLIENTE EMPRESARIA SOLO PUEDE TENER CUENTAS DE TIPO CORRIENTE
						if (!f.getTipoProducto().getIdTipo().equalsIgnoreCase("2")) {

							throw new RequestException("CLIENTE EMPRESARIAL : NO PUEDE TENER CUENTA DE ESTE TIPO");
						}

						CurrentAccount f1 = new CurrentAccount();

						f1.setDni(f.getDni());
						f1.setNumero_cuenta(f.getNumero_cuenta());
						f1.setFecha_afiliacion(f.getFecha_afiliacion());
						f1.setFecha_caducidad(f.getFecha_caducidad());
						f1.setSaldo(f.getSaldo());
						f1.setUsuario(f.getClave());
						f1.setClave(f.getClave());
						f1.setCodigoBanco(f.getCodigoBanco());

						TypeCurrentAccount t = new TypeCurrentAccount();
						t.setIdTipo(f.getTipoProducto().getIdTipo());
						t.setDescripcion(f.getTipoProducto().getDescripcion());
						f1.setTipoProducto(t);

						return productoDao.save(f1);

					} else if (p.getTipoCliente().getIdTipo().equalsIgnoreCase("3")) { //personal vip = 3
						
						//VALIDAR QUE SOLO PUEDE TENER LA CUENTA DE TIPO : AHORRO VIP , CORRIENTE VIP Y PZF VIP
						if (!f.getTipoProducto().getIdTipo().equalsIgnoreCase("4")
								&& !f.getTipoProducto().getIdTipo().equalsIgnoreCase("5")
								&& !f.getTipoProducto().getIdTipo().equalsIgnoreCase("8")) {
						
							throw new RequestException("PERSONAL VIP : NO PUEDE TENER CUENTA DE ESTE TIPO");

						}else if(!(f.getSaldo() >= 10)) { 							
							throw new RequestException("DEBE TENER SALDO MINIMO S/.10.00");
						}else {
													
							CurrentAccount f1 = new CurrentAccount();
							f1.setDni(f.getDni());
							f1.setNumero_cuenta(f.getNumero_cuenta());
							f1.setFecha_afiliacion(f.getFecha_afiliacion());
							f1.setFecha_caducidad(f.getFecha_caducidad());
							f1.setSaldo(f.getSaldo());
							f1.setUsuario(f.getClave());
							f1.setClave(f.getClave());
							f1.setCodigoBanco(f.getCodigoBanco());

							TypeCurrentAccount t = new TypeCurrentAccount();
							t.setIdTipo(f.getTipoProducto().getIdTipo());
							t.setDescripcion(f.getTipoProducto().getDescripcion());
							f1.setTipoProducto(t);
							return productoDao.save(f1);

						}
					}else if (p.getTipoCliente().getIdTipo().equalsIgnoreCase("4")) { //empresarial pyme = 4
						
						//VALIDAR QUE SOLO PUEDE TENER LA CUENTA DE TIPO EMPRESARIAL PYME
						if (!f.getTipoProducto().getIdTipo().equalsIgnoreCase("6")) {
							
							throw new RequestException("EMPRESARIAL PYME : NO PUEDE TENER CUENTA DE ESTE TIPO");

						}else if(!(f.getSaldo() >= 50)) { 
						
							throw new RequestException("DEBE TENER SALDO MINIMO S/.50.00");
					
						}else {
							CurrentAccount f1 = new CurrentAccount();

							f1.setDni(f.getDni());
							f1.setNumero_cuenta(f.getNumero_cuenta());
							f1.setFecha_afiliacion(f.getFecha_afiliacion());
							f1.setFecha_caducidad(f.getFecha_caducidad());
							f1.setSaldo(f.getSaldo());
							f1.setUsuario(f.getClave());
							f1.setClave(f.getClave());
							f1.setCodigoBanco(f.getCodigoBanco());

							TypeCurrentAccount t = new TypeCurrentAccount();
							t.setIdTipo(f.getTipoProducto().getIdTipo());
							t.setDescripcion(f.getTipoProducto().getDescripcion());
							f1.setTipoProducto(t);
							return productoDao.save(f1);

						}
					}else if (p.getTipoCliente().getIdTipo().equalsIgnoreCase("5")) { //empresarial corporativo = 5 
						
						//VALIDAR QUE SOLO PUEDE TENER LA CUENTA DE TIPO EMPRESARIAL CORPORATIVO
						if (!f.getTipoProducto().getIdTipo().equalsIgnoreCase("7")) {
							throw new RequestException("EMPRESARIAL CORPORATIVO : NO PUEDE TENER CUENTA DE ESTE TIPO");

						}else if(!(f.getSaldo() >= 100)) { 
							
								throw new RequestException("DEBE TENER SALDO MINIMO S/.100.00");
						
						} else {
							CurrentAccount f1 = new CurrentAccount();

							f1.setDni(f.getDni());
							f1.setNumero_cuenta(f.getNumero_cuenta());
							f1.setFecha_afiliacion(f.getFecha_afiliacion());
							f1.setFecha_caducidad(f.getFecha_caducidad());
							f1.setSaldo(f.getSaldo());
							f1.setUsuario(f.getClave());
							f1.setClave(f.getClave());
							f1.setCodigoBanco(f.getCodigoBanco());

							TypeCurrentAccount t = new TypeCurrentAccount();
							t.setIdTipo(f.getTipoProducto().getIdTipo());
							t.setDescripcion(f.getTipoProducto().getDescripcion());
							f1.setTipoProducto(t);
							return productoDao.save(f1);

						}
					}
					}
					return Mono.empty();

				});
				
				
			});
			
		});

	}

	@Override
	public Mono<CurrentAccount> listProdNumTarj(String num, String codigo_bancario) {
		System.out.println("lista productos por numero de targeta");
		return productoDao.viewNumTarjeta(num, codigo_bancario);
	}
	
	@Override
	public Mono<CurrentAccount> saveProductoBanco(CurrentAccount producto) {
		// TODO Auto-generated method stub
		return productoDao.save(producto);
	}

}
