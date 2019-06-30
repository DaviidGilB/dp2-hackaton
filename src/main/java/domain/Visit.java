
package domain;

import java.util.Date;

import javax.persistence.Access;
import javax.persistence.AccessType;
import javax.persistence.CascadeType;
import javax.persistence.Entity;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import javax.persistence.Index;
import javax.persistence.ManyToOne;
import javax.persistence.OneToOne;
import javax.persistence.Table;
import javax.validation.Valid;
import javax.validation.constraints.NotNull;

import org.hibernate.validator.constraints.NotBlank;
import org.springframework.format.annotation.DateTimeFormat;

@Entity
@Access(AccessType.PROPERTY)
@Table(indexes = {
	@Index(columnList = "visitor, prisoner, visitStatus, date"), @Index(columnList = "visitStatus, date"), 
	@Index(columnList = "prisoner, visitStatus, date"), @Index(columnList = "visitor, visitStatus, date"), @Index(columnList = "report")
})
public class Visit extends DomainEntity {

	private VisitStatus	visitStatus;
	private String		description;		//Formulario
	private Date		date;				//Formulario	Futura (controlar a mano)
	private Reason		reason;			//Formulario

	private Visitor		visitor;			//Formulario
	private Report		report;
	private Prisoner	prisoner;			//Formulario
	private boolean		createdByPrisoner;


	@Valid
	public boolean isCreatedByPrisoner() {
		return this.createdByPrisoner;
	}

	public void setCreatedByPrisoner(boolean createdByPrisoner) {
		this.createdByPrisoner = createdByPrisoner;
	}

	@NotNull
	@Enumerated(EnumType.STRING)
	public VisitStatus getVisitStatus() {
		return this.visitStatus;
	}

	public void setVisitStatus(VisitStatus visitStatus) {
		this.visitStatus = visitStatus;
	}

	@NotBlank
	public String getDescription() {
		return this.description;
	}

	public void setDescription(String description) {
		this.description = description;
	}

	@NotNull
	@DateTimeFormat(pattern = "dd/MM/yyyy HH:mm")
	public Date getDate() {
		return this.date;
	}

	public void setDate(Date date) {
		this.date = date;
	}

	@NotNull
	public Reason getReason() {
		return this.reason;
	}

	public void setReason(Reason reason) {
		this.reason = reason;
	}

	@ManyToOne
	public Visitor getVisitor() {
		return this.visitor;
	}

	public void setVisitor(Visitor visitor) {
		this.visitor = visitor;
	}

	@Valid
	@OneToOne(optional = true, cascade = CascadeType.ALL)
	public Report getReport() {
		return this.report;
	}

	public void setReport(Report report) {
		this.report = report;
	}

	@ManyToOne
	public Prisoner getPrisoner() {
		return this.prisoner;
	}

	public void setPrisoner(Prisoner prisoner) {
		this.prisoner = prisoner;
	}
}
