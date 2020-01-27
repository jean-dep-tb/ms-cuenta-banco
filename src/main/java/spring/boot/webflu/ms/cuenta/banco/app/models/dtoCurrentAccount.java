package spring.boot.webflu.ms.cuenta.banco.app.models;

import lombok.Getter;
import lombok.Setter;
import lombok.ToString;
import spring.boot.webflu.ms.cuenta.banco.app.models.TypeCurrentAccount;

@Getter
@Setter
public class dtoCurrentAccount {

	private String dni;
	private String numero_cuenta;
	private TypeCurrentAccount tipoProducto;
	private double saldo;
}
