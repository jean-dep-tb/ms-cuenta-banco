package spring.boot.webflu.ms.cuenta.banco.app.documents;

import javax.validation.constraints.NotEmpty;

import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@Document(collection ="TipoProducto")

public class TipoBancoCuenta {

	
	@NotEmpty
	private String idTipo;
	@NotEmpty
	private String descripcion;
		
	public TipoBancoCuenta() {
		
	}

	public TipoBancoCuenta(@NotEmpty String idTipo, @NotEmpty String descripcion) {		
		
		this.idTipo = idTipo;
		this.descripcion = descripcion;
		
	}
	
	
	
}
