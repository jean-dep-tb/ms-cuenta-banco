package spring.boot.webflu.ms.cuenta.banco.app.service;

import java.util.List;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import spring.boot.webflu.ms.cuenta.banco.app.models.CurrentAccount;

public interface ProductoService {

	Flux<CurrentAccount> findAllProducto();
	
	Mono<CurrentAccount> findByIdProducto(String id);
	
	Mono<CurrentAccount> saveProducto(CurrentAccount producto);

	Flux<CurrentAccount> findAllProductoByDniCliente(String dniCliente);

	//Flux<CurrentAccount> saveProductoList(List<CurrentAccount> producto);
	Flux<CurrentAccount> saveProductoList(CurrentAccount producto);
	
	Mono<CurrentAccount> listProdNumTarj(String num);
	
	Mono<CurrentAccount> retiro(Double monto, String numTarjeta, Double comision);
	
	Mono<CurrentAccount> depositos(Double monto, String numTarjeta, Double comision);

}
