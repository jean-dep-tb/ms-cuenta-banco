package spring.boot.webflu.ms.cuenta.banco.app;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.netflix.eureka.EnableEurekaClient;
import org.springframework.data.mongodb.core.ReactiveMongoTemplate;

import reactor.core.publisher.Flux;
import spring.boot.webflu.ms.cuenta.banco.app.documents.CuentaBanco;
import spring.boot.webflu.ms.cuenta.banco.app.documents.TipoBancoCuenta;
import spring.boot.webflu.ms.cuenta.banco.app.service.ProductoBancoService;
import spring.boot.webflu.ms.cuenta.banco.app.service.TipoBancoProductoService;

@EnableEurekaClient
@SpringBootApplication
public class SpringBootWebfluMsCuentaBancoApplication implements CommandLineRunner{

	@Autowired
	private ProductoBancoService serviceProducto;
	
	@Autowired
	private TipoBancoProductoService serviceTipoProducto;
	
	@Autowired
	private ReactiveMongoTemplate mongoTemplate;
	
	private static final Logger log = LoggerFactory.getLogger(SpringBootWebfluMsCuentaBancoApplication.class);
	
	public static void main(String[] args) {
		SpringApplication.run(SpringBootWebfluMsCuentaBancoApplication.class, args);
	}

	@Override
	public void run(String... args) throws Exception {
		
		mongoTemplate.dropCollection("ProductosBancarios").subscribe();
		mongoTemplate.dropCollection("TipoProducto").subscribe();
		
		TipoBancoCuenta ahorro = new TipoBancoCuenta("1","ahorro");
		TipoBancoCuenta ahorroVip = new TipoBancoCuenta("2","ahorroVip");
		TipoBancoCuenta corriente = new TipoBancoCuenta("3","corriente");
		TipoBancoCuenta corrienteVip = new TipoBancoCuenta("4","corrienteVip");
		TipoBancoCuenta plazoFijo = new TipoBancoCuenta("5","plazoFijo");
		TipoBancoCuenta plazoFijoVip = new TipoBancoCuenta("6","plazoFijoVip");
		TipoBancoCuenta pyme = new TipoBancoCuenta("7","pyme");
		TipoBancoCuenta corporativo = new TipoBancoCuenta("8","corporativo");
		//
		Flux.just(ahorro,ahorroVip,corriente,corrienteVip,plazoFijo,plazoFijoVip,pyme,corporativo)
		.flatMap(serviceTipoProducto::saveTipoProducto)
		.doOnNext(c -> {
			log.info("Tipo cliente creado: " +  c.getDescripcion() + ", Id: " + c.getIdTipo());
		}).thenMany(					
				Flux.just(
						
						new CuentaBanco("47305710","900001",ahorro,10000.0,"bcp"),
						new CuentaBanco("47305710","900002",ahorroVip,20000.0,"bbva"),
						new CuentaBanco("47305711","900003",corriente,30000.0,"bcp"),
						new CuentaBanco("47305711","900004",corrienteVip,40000.0,"bbva"),
						new CuentaBanco("47305712","900005",plazoFijo,50000.0,"yyy"),
						new CuentaBanco("47305712","900006",plazoFijoVip,60000.0,"xxx"),
						new CuentaBanco("47305713","900007",pyme,70000.0,"xxx"),
						new CuentaBanco("47305713","900008",corporativo,80000.0,"yyy")

						)					
					.flatMap(producto -> {
						return serviceProducto.saveProductoBanco(producto);
					})					
				).subscribe(producto -> log.info("Insert: " + producto.getId() + " " + producto.getNumero_cuenta()));
		
		
	}

}
