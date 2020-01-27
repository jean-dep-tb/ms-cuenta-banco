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
import spring.boot.webflu.ms.cuenta.banco.app.models.CurrentAccount;
import spring.boot.webflu.ms.cuenta.banco.app.models.TypeCurrentAccount;
import spring.boot.webflu.ms.cuenta.banco.app.service.ProductoService;
import spring.boot.webflu.ms.cuenta.banco.app.service.TipoProductoService;

@EnableEurekaClient
@SpringBootApplication
public class SpringBootWebfluMsCuentaBancoApplication implements CommandLineRunner{

	@Autowired
	private ProductoService serviceProducto;
	
	@Autowired
	private TipoProductoService serviceTipoProducto;
	
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
		
		TypeCurrentAccount ahorro = new TypeCurrentAccount("1","ahorro");
		TypeCurrentAccount ahorroVip = new TypeCurrentAccount("2","ahorroVip");
		TypeCurrentAccount corriente = new TypeCurrentAccount("3","corriente");
		TypeCurrentAccount corrienteVip = new TypeCurrentAccount("4","corrienteVip");
		TypeCurrentAccount plazoFijo = new TypeCurrentAccount("5","plazoFijo");
		TypeCurrentAccount plazoFijoVip = new TypeCurrentAccount("6","plazoFijoVip");
		TypeCurrentAccount pyme = new TypeCurrentAccount("7","pyme");
		TypeCurrentAccount corporativo = new TypeCurrentAccount("8","corporativo");
		//
		Flux.just(ahorro,ahorroVip,corriente,corrienteVip,plazoFijo,plazoFijoVip,pyme,corporativo)
		.flatMap(serviceTipoProducto::saveTipoProducto)
		.doOnNext(c -> {
			log.info("Tipo cliente creado: " +  c.getDescripcion() + ", Id: " + c.getIdTipo());
		}).thenMany(					
				Flux.just(
						new CurrentAccount("07091424","0001",ahorro,1000.0),
						new CurrentAccount("07091425","0002",ahorroVip,2000.0),
						new CurrentAccount("07091426","0003",corriente,3000.0),
						new CurrentAccount("07091427","0004",corrienteVip,4000.0),
						new CurrentAccount("07091428","0005",plazoFijo,5000.0),
						new CurrentAccount("07091429","0006",plazoFijoVip,6000.0),
						new CurrentAccount("07091430","0007",pyme,7000.0),
						new CurrentAccount("07091431","0008",corporativo,8000.0)

						)					
					.flatMap(producto -> {
						return serviceProducto.saveProducto(producto);
					})					
				).subscribe(producto -> log.info("Insert: " + producto.getId() + " " + producto.getNumero_cuenta()));
		
		
	}

}
