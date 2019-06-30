
package domain;

import java.util.Date;
import java.util.List;

import javax.persistence.Access;
import javax.persistence.AccessType;
import javax.persistence.Entity;
import javax.persistence.Index;
import javax.persistence.OneToMany;
import javax.persistence.Table;
import javax.validation.constraints.Future;
import javax.validation.constraints.Min;
import javax.validation.constraints.NotNull;

import org.hibernate.validator.constraints.NotBlank;
import org.springframework.format.annotation.DateTimeFormat;

@Entity
@Access(AccessType.PROPERTY)
@Table(indexes = {
	@Index(columnList = "realizationDate"), @Index(columnList = "title, description"), @Index(columnList = "maxAssistant"),
})
public class Activity extends DomainEntity {

	private String			title;
	private String			description;
	private Date			realizationDate;
	private int				maxAssistant;
	private int				rewardPoints;
	private Boolean			isFinalMode;

	private List<Request>	requests;


	@NotBlank
	public String getTitle() {
		return this.title;
	}

	public void setTitle(String title) {
		this.title = title;
	}

	@NotBlank
	public String getDescription() {
		return this.description;
	}

	public void setDescription(String description) {
		this.description = description;
	}

	@NotNull
	@Future
	@DateTimeFormat(pattern = "dd/MM/yyyy HH:mm")
	public Date getRealizationDate() {
		return this.realizationDate;
	}

	public void setRealizationDate(Date realizationDate) {
		this.realizationDate = realizationDate;
	}

	@Min(1)
	public int getMaxAssistant() {
		return this.maxAssistant;
	}

	public void setMaxAssistant(int maxAssistant) {
		this.maxAssistant = maxAssistant;
	}

	@Min(1)
	public int getRewardPoints() {
		return this.rewardPoints;
	}

	public void setRewardPoints(int rewardPoints) {
		this.rewardPoints = rewardPoints;
	}

	@NotNull
	public Boolean getIsFinalMode() {
		return this.isFinalMode;
	}

	public void setIsFinalMode(Boolean isFinalMode) {
		this.isFinalMode = isFinalMode;
	}

	@OneToMany(mappedBy = "activity")
	public List<Request> getRequests() {
		return this.requests;
	}

	public void setRequests(List<Request> requests) {
		this.requests = requests;
	}

}
