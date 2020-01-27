package spring.boot.webflu.ms.cuenta.banco.app.controllers;

import javax.annotation.PostConstruct;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.reactive.function.client.WebClient;

import reactor.core.publisher.Flux;
import spring.boot.webflu.ms.cuenta.banco.app.models.Client;

@RestController
@RequestMapping("/api/ProductoBancario")
public class WebClientController {
	
	WebClient webClient;
	@PostConstruct
	 public void init() { 
			 webClient = WebClient 
			.builder()
			.baseUrl("http://gateway:8099/clientes/api/ClientePersonal")
			.defaultHeader(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_JSON_UTF8_VALUE)
			.build(); 		 
	}
	
   @GetMapping("/ClientePersonal/{dniCliente}")
   public Flux<Client> getCliente(@PathVariable String dniCliente) 
   { 
		return webClient.get().uri("/dni/"+dniCliente).retrieve().bodyToFlux(Client.class); 
   }
  

}
