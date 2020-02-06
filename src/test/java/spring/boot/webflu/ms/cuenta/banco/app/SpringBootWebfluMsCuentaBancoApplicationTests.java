package spring.boot.webflu.ms.cuenta.banco.app;

import java.util.List;

import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.test.web.reactive.server.WebTestClient;

import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import spring.boot.webflu.ms.cuenta.banco.app.documents.CuentaBanco;
import spring.boot.webflu.ms.cuenta.banco.app.documents.TipoBancoCuenta;

@RunWith(SpringRunner.class)
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
class SpringBootWebfluMsCuentaBancoApplicationTests {

	@Autowired
	private WebTestClient client;
	
	@Test
	void contextLoads() {
	}

	@Test
	public void listarCuentaBanco() {
		client.get().uri("/api/ProductoBancario")
		.accept(MediaType.APPLICATION_JSON)
		.exchange()
		.expectStatus().isOk() 
		.expectHeader().contentType(MediaType.APPLICATION_JSON) //.hasSize(2);
		.expectBodyList(CuentaBanco.class).consumeWith(response -> {
			
			List<CuentaBanco> cuentaBanco = response.getResponseBody();
			
			cuentaBanco.forEach(p -> {
				System.out.println(p.getNumeroCuenta());
			});
			
			Assertions.assertThat(cuentaBanco.size() > 0).isTrue();
		});
	}
	
	@Test
	void crearCuentaBanco() {
		
		TipoBancoCuenta tict = new TipoBancoCuenta();
		tict.setIdTipo("1");
		tict.setDescripcion("ahorro");
		
		CuentaBanco ctBanco = new CuentaBanco();
		ctBanco.setDni("47305710");
		ctBanco.setNumeroCuenta("900001");
		ctBanco.setTipoProducto(tict);
		ctBanco.setFecha_afiliacion("2020-02-03");
		ctBanco.setFecha_caducidad("2020-02-03");
		ctBanco.setSaldo(9.0);
		ctBanco.setUsuario("jean");
		ctBanco.setClave("123");
		ctBanco.setCodigoBanco("bcp");
		
		client.post()
		.uri("/api/ProductoBancario/guardarProductoBanco")
		.contentType(MediaType.APPLICATION_JSON)
		.accept(MediaType.APPLICATION_JSON)
		.body(Mono.just(ctBanco), CuentaBanco.class)
		.exchange()
		.expectStatus().isOk()
		.expectHeader().contentType(MediaType.APPLICATION_JSON)
		.expectBody(CuentaBanco.class)
		.consumeWith(response -> {
			CuentaBanco b = response.getResponseBody();
			Assertions.assertThat(b.getDni()).isNotEmpty().isEqualTo("47305710");
			Assertions.assertThat(b.getNumeroCuenta()).isNotEmpty().isEqualTo("900001");
			Assertions.assertThat(b.getTipoProducto().getDescripcion()).isNotEmpty().isEqualTo("ahorro");
			Assertions.assertThat(b.getFecha_afiliacion()).isNotEmpty().isEqualTo("2020-02-03");
			Assertions.assertThat(b.getFecha_caducidad()).isNotEmpty().isEqualTo("2020-02-03");
			Assertions.assertThat(b.getSaldo()).isEqualTo(9.0);
			Assertions.assertThat(b.getUsuario()).isNotEmpty().isEqualTo("jean");
			Assertions.assertThat(b.getClave()).isNotEmpty().isEqualTo("123");
			Assertions.assertThat(b.getCodigoBanco()).isEqualTo("bcp");
		});
	}
	
	
}
