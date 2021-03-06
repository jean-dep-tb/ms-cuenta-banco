package spring.boot.webflu.ms.cuenta.banco.app.dao;

import java.util.Date;

import org.springframework.data.mongodb.repository.Query;
import org.springframework.data.mongodb.repository.ReactiveMongoRepository;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import spring.boot.webflu.ms.cuenta.banco.app.documents.CurrentAccount;

public interface ProductoDao extends ReactiveMongoRepository<CurrentAccount, String> {

	//busca por numero de documento y tipo de producto si ya esta registrado
	@Query("{ 'dni' : ?0 , 'tipoProducto.idTipo' : ?1, 'codigoBanco': ?2 }")
	Flux<CurrentAccount> viewDniCliente2(String dni, String idTipo, String codigo_bancario);
	
	
	@Query("{ 'dni' : ?0 }")
	Flux<CurrentAccount> viewDniCliente(String dni);

	//BUSCA EL NUMERO DE CUENTA - TARGETA CON SU BANCO
	@Query("{ 'numero_cuenta' : ?0, 'codigoBanco': ?1}")
	Mono<CurrentAccount> viewNumTarjeta(String numTarjeta,String codigo_bancario);
	

	/*@Query("{ 'numeroTarjeta' : ?0 }")
	Mono<Producto> viewNumTarjeta2(String numTarjeta);*/
	
//	@Query("{'fecha_afiliacion' : {'$gt' : ?0, '$lt' : ?1}, 'codigo_bancario' : ?2}")
//	Flux<CurrentAccount> consultaProductoBanco(Date from, Date to, String codigo_bancario);
//	
}
