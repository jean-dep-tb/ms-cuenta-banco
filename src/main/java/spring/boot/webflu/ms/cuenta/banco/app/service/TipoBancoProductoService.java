package spring.boot.webflu.ms.cuenta.banco.app.service;

import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import spring.boot.webflu.ms.cuenta.banco.app.documents.TipoBancoCuenta;

public interface TipoBancoProductoService {
	
	Flux<TipoBancoCuenta> findAllTipoproducto();
	Mono<TipoBancoCuenta> findByIdTipoProducto(String id);
	Mono<TipoBancoCuenta> saveTipoProducto(TipoBancoCuenta tipoProducto);
	Mono<Void> deleteTipo(TipoBancoCuenta tipoProducto);
	
}
