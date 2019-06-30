
package domain;

import java.util.Date;

import javax.persistence.Access;
import javax.persistence.AccessType;
import javax.persistence.Entity;
import javax.validation.Valid;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Past;

import org.hibernate.validator.constraints.NotBlank;
import org.hibernate.validator.constraints.URL;
import org.springframework.format.annotation.DateTimeFormat;

@Entity
@Access(AccessType.PROPERTY)
public class EducationRecord extends DomainEntity {

	private String	title;
	private Date	startDateStudy;
	private Date	endDateStudy;
	private String	institution;
	private String	link;


	public EducationRecord() {		//Created for Json Purposes
		super();
	}

	@NotBlank
	public String getTitle() {
		return this.title;
	}

	public void setTitle(String title) {
		this.title = title;
	}

	@DateTimeFormat(pattern = "dd/MM/yyyy")
	@Past
	@NotNull
	public Date getStartDateStudy() {
		return this.startDateStudy;
	}

	public void setStartDateStudy(Date startDateStudy) {
		this.startDateStudy = startDateStudy;
	}

	@Valid
	@DateTimeFormat(pattern = "dd/MM/yyyy")
	@Past
	public Date getEndDateStudy() {
		return this.endDateStudy;
	}

	public void setEndDateStudy(Date endDateStudy) {
		this.endDateStudy = endDateStudy;
	}

	@NotBlank
	public String getInstitution() {
		return this.institution;
	}

	public void setInstitution(String institution) {
		this.institution = institution;
	}

	@URL
	public String getLink() {
		return this.link;
	}

	public void setLink(String link) {
		this.link = link;
	}

}
