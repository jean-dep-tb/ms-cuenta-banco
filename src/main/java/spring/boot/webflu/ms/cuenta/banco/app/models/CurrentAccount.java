package spring.boot.webflu.ms.cuenta.banco.app.models;

import javax.validation.Valid;
import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.NotNull;

import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;



import lombok.Getter;
import lombok.Setter;
import lombok.ToString;
import reactor.core.publisher.Mono;

@Getter
@Setter
@ToString
@Document(collection ="ProductosBancarios")
public class CurrentAccount {
	
	@Id
	@NotEmpty
	private String id;
	@NotEmpty
	private String dni;
	@NotEmpty
	private String numero_cuenta;
	@NotEmpty
	
	private TypeCurrentAccount tipoProducto;
	@NotEmpty
	private String fecha_afiliacion;
	@NotEmpty
	private String fecha_caducidad;
	@NotEmpty
	private double saldo;
	@NotEmpty
	private String usuario;
	@NotEmpty
	private String clave;
	
	//private tipoProducto tipoCliente;
	
	public CurrentAccount() {

	}

	public CurrentAccount(String dni,String numero_cuenta,
			TypeCurrentAccount tipoProducto,double saldo) {
		this.dni = dni;
		this.numero_cuenta = numero_cuenta;
		this.tipoProducto = tipoProducto;
		this.saldo = saldo;
	}

	
	
	
	
	
}










