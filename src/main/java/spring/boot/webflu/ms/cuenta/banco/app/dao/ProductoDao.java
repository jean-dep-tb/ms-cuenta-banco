package spring.boot.webflu.ms.cuenta.banco.app.dao;

import org.springframework.data.mongodb.repository.Query;
import org.springframework.data.mongodb.repository.ReactiveMongoRepository;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import spring.boot.webflu.ms.cuenta.banco.app.models.CurrentAccount;

public interface ProductoDao extends ReactiveMongoRepository<CurrentAccount, String> {

	//busca por numero de documento y tipo de producto si ya esta registrado
	@Query("{ 'dni' : ?0 , 'tipoProducto.idTipo' : ?1}")
	Flux<CurrentAccount> viewDniCliente2(String dni, String idTipo);
	
	
	@Query("{ 'dni' : ?0 }")
	Flux<CurrentAccount> viewDniCliente(String dni);

	@Query("{ 'numero_cuenta' : ?0 }")
	Mono<CurrentAccount> viewNumTarjeta(String numTarjeta);
	

	/*@Query("{ 'numeroTarjeta' : ?0 }")
	Mono<Producto> viewNumTarjeta2(String numTarjeta);*/
	
}
