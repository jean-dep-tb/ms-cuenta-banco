package spring.boot.webflu.ms.cuenta.banco.app.service;

import java.util.List;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import spring.boot.webflu.ms.cuenta.banco.app.documents.CuentaBanco;

public interface ProductoBancoService {

	Flux<CuentaBanco> findAllProductoBanco();
	
	Mono<CuentaBanco> findByIdProductoBanco(String id); //findByIdProducto
	
	Mono<CuentaBanco> saveProductoBanco(CuentaBanco producto); //saveProducto

	Flux<CuentaBanco> findAllProductoByDniCliente(String dniCliente);

	//Flux<CurrentAccount> saveProductoList(List<CurrentAccount> producto);
	Flux<CuentaBanco> saveProductoBancoCliente(CuentaBanco producto); //saveProductoList
	
	Mono<CuentaBanco> listProdNumTarj(String num, String codigo_bancario);
	
	Mono<CuentaBanco> retiro(Double monto, String numTarjeta, Double comision, String codigo_bancario);
	
	Mono<CuentaBanco> depositos(Double monto, String numTarjeta, Double comision, String codigo_bancario);

}
