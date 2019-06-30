
package domain;

import java.util.Date;
import java.util.List;

import javax.persistence.Access;
import javax.persistence.AccessType;
import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Index;
import javax.persistence.ManyToMany;
import javax.persistence.OneToMany;
import javax.persistence.OneToOne;
import javax.persistence.Table;
import javax.validation.Valid;
import javax.validation.constraints.Max;
import javax.validation.constraints.Min;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Past;
import javax.validation.constraints.Pattern;

import org.hibernate.validator.constraints.NotBlank;
import org.springframework.format.annotation.DateTimeFormat;

@Entity
@Access(AccessType.PROPERTY)
@Table(indexes = {
	@Index(columnList = "crimeRate"), @Index(columnList = "freedom"), @Index(columnList = "isIsolated, freedom")
})
public class Prisoner extends Actor {

	private Double				crimeRate;
	private String				ticker;
	private Date				incomeDate;
	private Date				exitDate;
	private Boolean				isIsolated;
	private Boolean				isSuspect;
	private Integer				points;
	private Boolean				freedom;

	private List<Charge>		charges;
	private List<Request>		requests;
	private List<Product>		products;
	private FinderActivities	finderActivities;
	private List<Visit>			visits;


	@NotNull
	@Min(-1)
	@Max(1)
	public Double getCrimeRate() {
		return this.crimeRate;
	}

	public void setCrimeRate(Double crimeRate) {
		this.crimeRate = crimeRate;
	}

	@Pattern(regexp = "[0-9]{2}[0-1]{1}[0-9]{3}-([A-Za-z0-9]{6})")
	@NotBlank
	@Column(unique = true)
	public String getTicker() {
		return this.ticker;
	}

	public void setTicker(String ticker) {
		this.ticker = ticker;
	}

	@Past
	@DateTimeFormat(pattern = "dd/MM/yyyy HH:mm")
	public Date getIncomeDate() {
		return this.incomeDate;
	}

	public void setIncomeDate(Date incomeDate) {
		this.incomeDate = incomeDate;
	}

	@DateTimeFormat(pattern = "dd/MM/yyyy HH:mm")
	public Date getExitDate() {
		return this.exitDate;
	}

	public void setExitDate(Date exitDate) {
		this.exitDate = exitDate;
	}

	@NotNull
	public Boolean getIsIsolated() {
		return this.isIsolated;
	}

	public void setIsIsolated(Boolean isIsolated) {
		this.isIsolated = isIsolated;
	}

	@NotNull
	public Boolean getIsSuspect() {
		return this.isSuspect;
	}

	public void setIsSuspect(Boolean isSuspect) {
		this.isSuspect = isSuspect;
	}

	@NotNull
	@Min(0)
	public Integer getPoints() {
		return this.points;
	}

	public void setPoints(Integer points) {
		this.points = points;
	}

	@NotNull
	public Boolean getFreedom() {
		return this.freedom;
	}

	public void setFreedom(Boolean freedom) {
		this.freedom = freedom;
	}

	@ManyToMany
	public List<Charge> getCharges() {
		return this.charges;
	}

	public void setCharges(List<Charge> charges) {
		this.charges = charges;
	}

	@OneToMany(mappedBy = "prisoner")
	public List<Request> getRequests() {
		return this.requests;
	}

	public void setRequests(List<Request> requests) {
		this.requests = requests;
	}

	@ManyToMany
	public List<Product> getProducts() {
		return this.products;
	}

	public void setProducts(List<Product> products) {
		this.products = products;
	}

	@Valid
	@OneToOne(optional = true, cascade = CascadeType.ALL)
	public FinderActivities getFinderActivities() {
		return this.finderActivities;
	}

	public void setFinderActivities(FinderActivities finderActivities) {
		this.finderActivities = finderActivities;
	}

	@OneToMany(mappedBy = "prisoner")
	public List<Visit> getVisits() {
		return this.visits;
	}

	public void setVisits(List<Visit> visits) {
		this.visits = visits;
	}
}
