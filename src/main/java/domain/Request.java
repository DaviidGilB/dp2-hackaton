
package domain;

import javax.persistence.Access;
import javax.persistence.AccessType;
import javax.persistence.Entity;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import javax.persistence.Index;
import javax.persistence.ManyToOne;
import javax.persistence.Table;
import javax.validation.Valid;
import javax.validation.constraints.NotNull;

import org.hibernate.validator.constraints.NotBlank;

@Entity
@Access(AccessType.PROPERTY)
@Table(indexes = {
	@Index(columnList = "activity, status"), @Index(columnList = "prisoner, status"),
	@Index(columnList = "status"), @Index(columnList = "prisoner"), @Index(columnList = "activity")
})
public class Request extends DomainEntity {

	private ActivityStatus	status;
	private String			motivation;
	private String			rejectReason;
	private Activity		activity;
	private Prisoner		prisoner;


	@ManyToOne
	public Prisoner getPrisoner() {
		return this.prisoner;
	}

	public void setPrisoner(Prisoner prisoner) {
		this.prisoner = prisoner;
	}

	@NotNull
	@Enumerated(EnumType.STRING)
	public ActivityStatus getStatus() {
		return this.status;
	}

	public void setStatus(ActivityStatus status) {
		this.status = status;
	}

	@NotBlank
	public String getMotivation() {
		return this.motivation;
	}

	public void setMotivation(String motivation) {
		this.motivation = motivation;
	}

	@Valid
	public String getRejectReason() {
		return this.rejectReason;
	}

	public void setRejectReason(String rejectReason) {
		this.rejectReason = rejectReason;
	}

	@ManyToOne
	public Activity getActivity() {
		return this.activity;
	}

	public void setActivity(Activity activity) {
		this.activity = activity;
	}

}
