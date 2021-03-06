package spring.boot.webflu.ms.cuenta.banco.app.controllers;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import spring.boot.webflu.ms.cuenta.banco.app.documents.CurrentAccount;
import spring.boot.webflu.ms.cuenta.banco.app.documents.TypeCurrentAccount;
import spring.boot.webflu.ms.cuenta.banco.app.dto.dtoCurrentAccount;
import spring.boot.webflu.ms.cuenta.banco.app.service.ProductoService;

@RequestMapping("/api/ProductoBancario")
@RestController
public class ProductoControllers {

	private static final Logger log = LoggerFactory.getLogger(ProductoControllers.class);
	
	@Autowired
	private ProductoService productoService;

	//LISTA LAS CUENTAS DE BANCO EXISTENTES
	@GetMapping
	public Mono<ResponseEntity<Flux<CurrentAccount>>> findAll() {
		return Mono.just(
				ResponseEntity.ok().contentType(MediaType.APPLICATION_JSON_UTF8).body(productoService.findAllProductoBanco())

		);
	}

	//TODAS CUENTAS BANCARIAS POR ID
	@GetMapping("/{id}")
	public Mono<ResponseEntity<CurrentAccount>> viewId(@PathVariable String id) {
		return productoService.findByIdProductoBanco(id)
				.map(p -> ResponseEntity.ok().contentType(MediaType.APPLICATION_JSON_UTF8).body(p))
				.defaultIfEmpty(ResponseEntity.notFound().build());
	}

	// ACTUALIZAR LA CUENTA
	@PutMapping
	public Mono<CurrentAccount> updateProducto(@RequestBody CurrentAccount producto) {

		return productoService.saveProductoBanco(producto);
	}
	
	//REALIZAR RETIRO DE LA CUENTA BANCARIA
	//SERVICIO CONSUMIDO DESDE MS-OP-BANCOS
	@PutMapping("/retiro/{numero_cuenta}/{monto}/{comision}/{codigo_bancario}")
	public Mono<CurrentAccount> retiroBancario(@PathVariable String numero_cuenta,@PathVariable Double monto,
			@PathVariable Double comision, @PathVariable String codigo_bancario) {

		System.out.println("LLEGO DESDE MS-OP-BANCOS --->>>");
		return productoService.retiro(monto, numero_cuenta, comision,codigo_bancario);
	}
	
	//REALIZA UN DEPOSITO A LA CUENTA
	//SERVICIO CONSUMIDO DESDE MS-OP-BANCOS
	@PutMapping("/deposito/{numero_Cuenta}/{monto}/{comision}/{codigo_bancario}")
	public Mono<CurrentAccount> despositoBancario(@PathVariable Double monto, @PathVariable String numero_Cuenta,
			@PathVariable Double comision,@PathVariable String codigo_bancario) {

		return productoService.depositos(monto, numero_Cuenta, comision,codigo_bancario);
	}
	
	//GUARDAR UN PRODUCTO BANCARIO - TIPO AHORRO, PLAZO FIJO,CORRIENTE,AHORRO VIP,CORRIENTE VIP,EMPRESARIAL PYME,CORPORATIVO
	//PLAZO FIJO VIP
	@PostMapping
	public Flux<CurrentAccount> guardarProductoBanco(@RequestBody CurrentAccount pro) {
		return productoService.saveProductoBancoCliente(pro);
	}
	
	//MUESTRA LA CUENTA BANCARIA POR EL NUMERO DE TARJETA Y EL CODIGO DE BANCO
	@GetMapping("/numero_cuenta/{num}/{codigo_bancario}")
	public Mono<CurrentAccount> listarProductoBanco(@PathVariable String num, @PathVariable String codigo_bancario) {
		Mono<CurrentAccount> producto = productoService.listProdNumTarj(num, codigo_bancario);
		return producto;
	}

	//MUESTRA LAS CUENTAS DE LOS CLIENTES -{TRANSACCIONES REALIZADAS Y TODAS LAS CUENTAS ASOCIADAS CON ESE CLIENTE}
	@GetMapping("/dni/{dni}")
	public Flux<CurrentAccount> listarProductoBancoCliente(@PathVariable String dni) {
		Flux<CurrentAccount> producto = productoService.findAllProductoByDniCliente(dni);
		return producto;
	} 
	
	//MUESTRA LOS SALDOS DE LA CUENTAS DE BANCO
	//CON EL NUMERO DE CUENTA Y EL CODIGO DE BANCO
	@GetMapping("/SaldosBancarios/{numero_cuenta}/{codigo_bancario}")
	public Mono<dtoCurrentAccount> saldosClienteBancos(@PathVariable String numero_cuenta,@PathVariable String codigo_bancario) {
		
		System.out.println("Saldos Bancarios : --->> " + numero_cuenta + " cod banco -->> " + codigo_bancario);
		
		//BUSCAR LA TARGETA-CUENTA
		Mono<CurrentAccount> oper = productoService.listProdNumTarj(numero_cuenta, codigo_bancario);
		
		oper.map(o->o).subscribe(u -> log.info(u.toString()));
		
		return oper.flatMap(c -> {

			dtoCurrentAccount pp = new dtoCurrentAccount();
			TypeCurrentAccount tp = new TypeCurrentAccount();
			
			tp.setIdTipo(c.getTipoProducto().getIdTipo());
			tp.setDescripcion(c.getTipoProducto().getDescripcion());
			
			
			pp.setDni(c.getDni());
			pp.setNumero_cuenta(c.getNumero_cuenta());
			pp.setSaldo(c.getSaldo());
			pp.setTipoProducto(tp);

			return Mono.just(pp);
		});

	}
}
