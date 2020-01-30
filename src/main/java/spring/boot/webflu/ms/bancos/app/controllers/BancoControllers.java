package spring.boot.webflu.ms.bancos.app.controllers;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import spring.boot.webflu.ms.bancos.app.documents.Banco;
import spring.boot.webflu.ms.bancos.app.service.BancoService;

@RequestMapping("/api/Bancos")
@RestController
public class BancoControllers {

	@Autowired
	private BancoService bankService;

	// LISTA TODOS LOS BANCOS
	@GetMapping
	public Mono<ResponseEntity<Flux<Banco>>> findAll() {
		return Mono
				.just(ResponseEntity.ok().contentType(MediaType.APPLICATION_JSON_UTF8).body(bankService.findAllBanco())

				);
	}

	// LISTA BANCO POR ID
	@GetMapping("/{id}")
	public Mono<ResponseEntity<Banco>> viewId(@PathVariable String id) {
		return bankService.findByIdBanco(id)
				.map(p -> ResponseEntity.ok().contentType(MediaType.APPLICATION_JSON_UTF8).body(p))
				.defaultIfEmpty(ResponseEntity.notFound().build());
	}

	// LISTA BANCO POR RUC
	@GetMapping("/ruc/{ruc}")
	public Mono<ResponseEntity<Banco>> viewId2(@PathVariable String ruc) {
		return bankService.viewRucBanco(ruc)
				.map(p -> ResponseEntity.ok().contentType(MediaType.APPLICATION_JSON_UTF8).body(p))
				.defaultIfEmpty(ResponseEntity.notFound().build());
	}

	// ACTUALIZA BANCO POR ID
	@PutMapping
	public Mono<Banco> updateClientePersonal(@RequestBody Banco cliente) {
		System.out.println(cliente.toString());
		return bankService.saveBanco(cliente);
	}

	// GUARDA BANCO
	@PostMapping
	public Mono<Banco> guardarCliente(@RequestBody Banco bank) {
		return bankService.saveBanco(bank);

	}

	// ELIMINA BANCO POR ID
	@DeleteMapping("/{id}")
	public Mono<ResponseEntity<Void>> deleteBanco(@PathVariable String id) {
		return bankService.findByIdBanco(id).flatMap(s -> {
			return bankService.deleteBanco(s).then(Mono.just(new ResponseEntity<Void>(HttpStatus.NO_CONTENT)));
		}).defaultIfEmpty(new ResponseEntity<Void>(HttpStatus.NO_CONTENT));
	}

}
