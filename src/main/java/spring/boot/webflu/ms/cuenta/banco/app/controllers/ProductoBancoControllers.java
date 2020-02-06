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

import io.swagger.annotations.ApiOperation;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import spring.boot.webflu.ms.cuenta.banco.app.documents.CuentaBanco;
import spring.boot.webflu.ms.cuenta.banco.app.documents.TipoBancoCuenta;
import spring.boot.webflu.ms.cuenta.banco.app.dto.CuentaBancoDto;
import spring.boot.webflu.ms.cuenta.banco.app.service.ProductoBancoService;

@RequestMapping("/api/ProductoBancario")
@RestController
public class ProductoBancoControllers {

	private static final Logger log = LoggerFactory.getLogger(ProductoBancoControllers.class);
	
	@Autowired
	private ProductoBancoService productoService;

	@ApiOperation(value = "LISTA LAS CUENTAS DE BANCO EXISTENTES", notes="")
	@GetMapping
	public Mono<ResponseEntity<Flux<CuentaBanco>>> findAll() {
		return Mono.just(
				ResponseEntity.ok().contentType(MediaType.APPLICATION_JSON).body(productoService.findAllProductoBanco())

		);
	}

	@ApiOperation(value = "TODAS CUENTAS BANCARIAS POR ID", notes="")
	@GetMapping("/{id}")
	public Mono<ResponseEntity<CuentaBanco>> viewId(@PathVariable String id) {
		return productoService.findByIdProductoBanco(id)
				.map(p -> ResponseEntity.ok().contentType(MediaType.APPLICATION_JSON).body(p))
				.defaultIfEmpty(ResponseEntity.notFound().build());
	}

	@ApiOperation(value = "ACTUALIZAR LA CUENTA", notes="")
	@PutMapping
	public Mono<CuentaBanco> updateProducto(@RequestBody CuentaBanco producto) {

		return productoService.saveProductoBanco(producto);
	}

	@ApiOperation(value = "REALIZAR RETIRO DE LA CUENTA BANCARIA - SERVICIO CONSUMIDO DESDE MS-OP-BANCOS", notes="")
	@PutMapping("/retiro/{numero_cuenta}/{monto}/{comision}/{codigo_bancario}")
	public Mono<CuentaBanco> retiroBancario(@PathVariable String numero_cuenta,@PathVariable Double monto,
			@PathVariable Double comision, @PathVariable String codigo_bancario) {

		System.out.println("LLEGO DESDE MS-OP-BANCOS --->>>");
		return productoService.retiro(monto, numero_cuenta, comision,codigo_bancario);
	}
	
	@ApiOperation(value = "REALIZA UN DEPOSITO A LA CUENTA - SERVICIO CONSUMIDO DESDE MS-OP-BANCOS", notes="")
	@PutMapping("/deposito/{numero_Cuenta}/{monto}/{comision}/{codigo_bancario}")
	public Mono<CuentaBanco> despositoBancario(@PathVariable Double monto, @PathVariable String numero_Cuenta,
			@PathVariable Double comision,@PathVariable String codigo_bancario) {
		
		System.out.println("Comision :" + comision);

		return productoService.depositos(monto, numero_Cuenta, comision,codigo_bancario);
	}
	
	
	//transferencia sin comision
//	@ApiOperation(value = "Actualiza al momento de hacer la transaccion [DEPOSITO] desde servicio"
//			+ " operaciones(movimientos)", notes="")
//	@PutMapping("/depositoTransf/{numero_Cuenta}/{monto}/{codigo_bancario}")
//	public Mono<CurrentAccount> despositoTransf(@PathVariable Double monto, @PathVariable String numero_Cuenta,
//			@PathVariable Double comision,  @PathVariable String codigo_bancario) {
//
//		return productoService.depositos(monto, numero_Cuenta, codigo_bancario);
//	}

	@ApiOperation(value = "GUARDAR UN PRODUCTO BANCARIO - TIPO AHORRO, PLAZO FIJO,CORRIENTE,AHORRO VIP,CORRIENTE VIP,EMPRESARIAL PYME,CORPORATIVO,PLAZO FIJO VIP", notes="")
	@PostMapping
	public Flux<CuentaBanco> guardarProductoBanco(@RequestBody CuentaBanco pro) {
		
		//EL DNI DEBE DE EXISTIR EN EL MS-CREDITO PARA QUE PUEDA VERIFICAR
		return productoService.saveProductoBancoCliente(pro);
	}
	
	@ApiOperation(value = "GUARDA CUENTA PRODUCTO BANCO - SIN VALIDAR", notes="")
	@PostMapping("/guardarProductoBanco")
	public Mono<CuentaBanco> guardarProBanco(@RequestBody CuentaBanco cuentaBanco) {
		return productoService.saveProductoBanco(cuentaBanco);
	}

	@ApiOperation(value = "MUESTRA LA CUENTA BANCARIA POR EL NUMERO DE TARJETA Y EL CODIGO DE BANCO", notes="")
	@GetMapping("/numero_cuenta/{num}/{codigo_bancario}")
	public Mono<CuentaBanco> productosBancoPorBancos(@PathVariable String num, @PathVariable String codigo_bancario) {
		Mono<CuentaBanco> producto = productoService.listProdNumTarj(num, codigo_bancario);
		return producto;
	}

	//
	@ApiOperation(value = "MUESTRA LAS CUENTAS DE LOS CLIENTES -{TRANSACCIONES REALIZADAS Y TODAS LAS CUENTAS ASOCIADAS CON ESE CLIENTE}3", notes="")
	@GetMapping("/dni/{dni}")
	public Flux<CuentaBanco> mostrarProductoBancoCliente(@PathVariable String dni) {
		Flux<CuentaBanco> producto = productoService.findAllProductoByDniCliente(dni);
		return producto;
	} 

	@ApiOperation(value = "MUESTRA LOS SALDOS DE LA CUENTAS DE BANCO - CON EL NUMERO DE CUENTA Y EL CODIGO DE BANCO", notes="")
	@GetMapping("/SaldosBancarios/{numero_cuenta}/{codigo_bancario}")
	public Mono<CuentaBancoDto> saldosClienteBancos(@PathVariable String numero_cuenta,@PathVariable String codigo_bancario) {
		
		System.out.println("Saldos Bancarios : --->> " + numero_cuenta + " cod banco -->> " + codigo_bancario);
		
		//BUSCAR LA TARGETA-CUENTA
		Mono<CuentaBanco> oper = productoService.listProdNumTarj(numero_cuenta, codigo_bancario);
		
		oper.map(o->o).subscribe(u -> log.info(u.toString()));
		
		return oper.flatMap(c -> {

			CuentaBancoDto pp = new CuentaBancoDto();
			TipoBancoCuenta tp = new TipoBancoCuenta();
			
			tp.setIdTipo(c.getTipoProducto().getIdTipo());
			tp.setDescripcion(c.getTipoProducto().getDescripcion());
			
			
			pp.setDni(c.getDni());
			pp.setNumero_cuenta(c.getNumeroCuenta());
			pp.setSaldo(c.getSaldo());
			pp.setTipoProducto(tp);

			return Mono.just(pp);
		});

	}
}
