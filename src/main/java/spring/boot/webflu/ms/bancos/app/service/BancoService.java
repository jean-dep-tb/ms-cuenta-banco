package spring.boot.webflu.ms.bancos.app.service;

import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import spring.boot.webflu.ms.bancos.app.documents.Banco;

public interface BancoService {

	Flux<Banco> findAllBanco();
	Mono<Banco> findByIdBanco(String id);
	Mono<Banco> saveBanco(Banco clientePersonal);
	Mono<Void> deleteBanco(Banco cliente);
	Mono<Banco> viewRucBanco(String dni);
	
}
