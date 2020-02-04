package spring.boot.webflu.ms.cuenta.banco.app.service;

import java.util.List;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import spring.boot.webflu.ms.cuenta.banco.app.documents.CurrentAccount;

public interface ProductoService {

	Flux<CurrentAccount> findAllProductoBanco();
	
	Mono<CurrentAccount> findByIdProductoBanco(String id); //findByIdProducto
	
	Mono<CurrentAccount> saveProductoBanco(CurrentAccount producto); //saveProducto

	Flux<CurrentAccount> findAllProductoByDniCliente(String dniCliente);

	//Flux<CurrentAccount> saveProductoList(List<CurrentAccount> producto);
	Flux<CurrentAccount> saveProductoBancoCliente(CurrentAccount producto); //saveProductoList
	
	Mono<CurrentAccount> listProdNumTarj(String num, String codigo_bancario);
	
	Mono<CurrentAccount> retiro(Double monto, String numTarjeta, Double comision, String codigo_bancario);
	
	Mono<CurrentAccount> depositos(Double monto, String numTarjeta, Double comision, String codigo_bancario);

}
