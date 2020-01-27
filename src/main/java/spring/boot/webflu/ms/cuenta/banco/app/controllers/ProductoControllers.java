package spring.boot.webflu.ms.cuenta.banco.app.controllers;

import java.net.URI;
import java.util.List;

import javax.validation.Valid;

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
import spring.boot.webflu.ms.cuenta.banco.app.models.CurrentAccount;
import spring.boot.webflu.ms.cuenta.banco.app.models.TypeCurrentAccount;
import spring.boot.webflu.ms.cuenta.banco.app.models.dtoCurrentAccount;
import spring.boot.webflu.ms.cuenta.banco.app.service.ProductoService;
import spring.boot.webflu.ms.cuenta.banco.app.service.TipoProductoService;

@RequestMapping("/api/ProductoBancario")
@RestController
public class ProductoControllers {

	@Autowired
	private ProductoService productoService;

	@Autowired
	private TipoProductoService tipoProductoService;

	// Muestra todos las cuentas bancarias existentes
	@GetMapping
	public Mono<ResponseEntity<Flux<CurrentAccount>>> findAll() {
		return Mono.just(
				ResponseEntity.ok().contentType(MediaType.APPLICATION_JSON_UTF8).body(productoService.findAllProducto())

		);
	}

	// Filtra todas cuentas bancarias por id
	@GetMapping("/{id}")
	public Mono<ResponseEntity<CurrentAccount>> viewId(@PathVariable String id) {
		return productoService.findByIdProducto(id)
				.map(p -> ResponseEntity.ok().contentType(MediaType.APPLICATION_JSON_UTF8).body(p))
				.defaultIfEmpty(ResponseEntity.notFound().build());
	}

	// actualiza cuenta bancaria
	@PutMapping
	public Mono<CurrentAccount> updateProducto(@RequestBody CurrentAccount producto) {

		return productoService.saveProducto(producto);
	}

	// actualiza al momento de hacer la transaccion desde servicio
	// operaciones(movimientos)
	@PutMapping("/retiro/{numero_cuenta}/{monto}/{comision}")
	public Mono<CurrentAccount> retiroBancario(@PathVariable Double monto, @PathVariable String numero_cuenta,
			@PathVariable Double comision) {

		return productoService.retiro(monto, numero_cuenta, comision);
	}

	// actualiza al momento de hacer la transaccion desde servicio
	// operaciones(movimientos)
	@PutMapping("/deposito/{numero_Cuenta}/{monto}/{comision}")
	public Mono<CurrentAccount> despositoBancario(@PathVariable Double monto, @PathVariable String numero_Cuenta,
			@PathVariable Double comision) {

		return productoService.depositos(monto, numero_Cuenta, comision);
	}

	/*
	 * Guarda o Crea una tarjeta bancaria(tipo: ahorro, plazo fijo ....) - si el
	 * cliente ya tiene un tipo de cuenta bancaria no debe de registrarlo
	 */
	//public Flux<CurrentAccount> saveProducto(@RequestBody List<CurrentAccount> pro) {
	
	@PostMapping
	public Flux<CurrentAccount> saveProducto(@RequestBody CurrentAccount pro) {
		return productoService.saveProductoList(pro);
	}
	
	// Muestra la cuenta bancaria por el numero de tarjeta
	@GetMapping("/numero_cuenta/{num}")
	public Mono<CurrentAccount> listProdNumTarj(@PathVariable String num) {
		Mono<CurrentAccount> producto = productoService.listProdNumTarj(num);
		return producto;
	}

	// Muestra todos las Movimientos que realizo de sus trajetas de credito 
	// todos los registro de que se han realizado
	@GetMapping("/dni/{dni}")
	public Flux<CurrentAccount> listProductoByDicliente(@PathVariable String dni) {
		Flux<CurrentAccount> producto = productoService.findAllProductoByDniCliente(dni);
		return producto;
	}

	// Muestra los saldos de las cuentas de banco de un cliente
	// se consulta por el numero de cuenta
	@GetMapping("/SaldosBancarios/{numero_cuenta}")
	public Mono<dtoCurrentAccount> SaldosBancarios(@PathVariable String numero_cuenta) {
		
		//Busca el numero de tarjeta
		Mono<CurrentAccount> oper = productoService.listProdNumTarj(numero_cuenta);

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
