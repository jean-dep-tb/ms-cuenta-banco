package spring.boot.webflu.ms.bancos.app;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.netflix.eureka.EnableEurekaClient;

@EnableEurekaClient
@SpringBootApplication
public class SpringBootWebfluMsBancosApplication {

	public static void main(String[] args) {
		SpringApplication.run(SpringBootWebfluMsBancosApplication.class, args);
	}

}
