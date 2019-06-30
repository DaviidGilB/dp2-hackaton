package domain;

import javax.persistence.Access;
import javax.persistence.AccessType;
import javax.persistence.Entity;

import org.hibernate.validator.constraints.NotBlank;

@Entity
@Access(AccessType.PROPERTY)
public class TypeProduct extends DomainEntity {

	private String typeProductEN;
	private String typeProductES;

	@NotBlank
	public String getTypeProductEN() {
		return this.typeProductEN;
	}

	public void setTypeProductEN(String typeProductEN) {
		this.typeProductEN = typeProductEN;
	}

	@NotBlank
	public String getTypeProductES() {
		return this.typeProductES;
	}

	public void setTypeProductES(String typeProductES) {
		this.typeProductES = typeProductES;
	}

}
