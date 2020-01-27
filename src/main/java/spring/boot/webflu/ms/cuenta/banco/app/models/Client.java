package spring.boot.webflu.ms.cuenta.banco.app.models;


import lombok.Data;


@Data
public class Client {

	private String dni;
	private String nombres;
	private String apellidos;
	private String sexo;
	private String telefono;
	private String edad;
	private String correo;
	private TypeClient tipoCliente;
	
	
	@Override
	public String toString() {
		return "Client [dni=" + dni + ", nombres=" + nombres + ", apellidos=" + apellidos + ", sexo=" + sexo
				+ ", telefono=" + telefono + ", edad=" + edad + ", correo=" + correo + ", tipoCliente=" + tipoCliente
				+ "]";
	}
}










