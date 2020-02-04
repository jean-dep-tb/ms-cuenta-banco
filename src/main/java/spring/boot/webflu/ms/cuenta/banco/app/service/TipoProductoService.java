package spring.boot.webflu.ms.cuenta.banco.app.service;

import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import spring.boot.webflu.ms.cuenta.banco.app.documents.TypeCurrentAccount;

public interface TipoProductoService {
	
	Flux<TypeCurrentAccount> findAllTipoproducto();
	Mono<TypeCurrentAccount> findByIdTipoProducto(String id);
	Mono<TypeCurrentAccount> saveTipoProducto(TypeCurrentAccount tipoProducto);
	Mono<Void> deleteTipo(TypeCurrentAccount tipoProducto);
	
}
