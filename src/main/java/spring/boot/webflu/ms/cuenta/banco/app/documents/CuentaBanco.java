package spring.boot.webflu.ms.cuenta.banco.app.documents;

import java.util.Date;

import javax.validation.constraints.NotEmpty;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

import com.fasterxml.jackson.annotation.JsonFormat;

import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

@Getter
@Setter
@ToString
@Document(collection ="ProductosBancarios")
public class CuentaBanco {
	
	@Id
	@NotEmpty
	private String id;
	@NotEmpty
	private String dni;
	@NotEmpty
	private String numeroCuenta; //DEBE DE SER UNICO - numero_cuenta
	@NotEmpty
	
	private TipoBancoCuenta tipoProducto;
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
	@NotEmpty
	private String codigoBanco;
	
	//private tipoProducto tipoCliente;
	
//	@JsonFormat(shape=JsonFormat.Shape.STRING, pattern="dd/MM/yyyy")
//	public Date fecha_afiliacion() {
//		return fecha_afiliacion;
//	}
//	@JsonFormat(shape=JsonFormat.Shape.STRING, pattern="dd/MM/yyyy")
//	public Date fecha_caducidad() {
//		return fecha_caducidad;
//	}
	
	public CuentaBanco() {

	}

	public CuentaBanco(String dni,String numeroCuenta,
			TipoBancoCuenta tipoProducto,double saldo,String codigoBanco) {
		this.dni = dni;
		this.numeroCuenta = numeroCuenta;
		this.tipoProducto = tipoProducto;
		this.saldo = saldo;
		this.codigoBanco = codigoBanco;
	}

	
	
	
	
	
}










