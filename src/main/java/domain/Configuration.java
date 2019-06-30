
package domain;

import java.util.List;

import javax.persistence.Access;
import javax.persistence.AccessType;
import javax.persistence.ElementCollection;
import javax.persistence.Entity;
import javax.persistence.OneToMany;
import javax.validation.Valid;
import javax.validation.constraints.NotNull;

import org.hibernate.validator.constraints.NotBlank;
import org.hibernate.validator.constraints.URL;

@Entity
@Access(AccessType.PROPERTY)
public class Configuration extends DomainEntity {

	private List<String> spamWords;
	private List<TypeProduct> typeProducts;
	private String spainTelephoneCode;
	private Integer minFinderResults;
	private Integer maxFinderResults;
	private Integer timeFinderPrisoners;
	private Integer finderResult;
	private String welcomeMessageEnglish;
	private String welcomeMessageSpanish;
	private String systemName;
	private String imageURL;
	private Integer timeFinderActivities;

	@NotNull
	@Valid
	public Integer getFinderResult() {
		return this.finderResult;
	}

	public void setFinderResult(Integer finderResult) {
		this.finderResult = finderResult;
	}

	@ElementCollection(targetClass = String.class)
	public List<String> getSpamWords() {
		return this.spamWords;
	}

	public void setSpamWords(List<String> spamWords) {
		this.spamWords = spamWords;
	}

	@OneToMany
	public List<TypeProduct> getTypeProducts() {
		return this.typeProducts;
	}

	public void setTypeProducts(List<TypeProduct> typeProducts) {
		this.typeProducts = typeProducts;
	}

	@NotNull
	@NotBlank
	public String getSpainTelephoneCode() {
		return this.spainTelephoneCode;
	}

	public void setSpainTelephoneCode(String spainTelephoneCode) {
		this.spainTelephoneCode = spainTelephoneCode;
	}

	@NotBlank
	public String getWelcomeMessageEnglish() {
		return this.welcomeMessageEnglish;
	}

	public void setWelcomeMessageEnglish(String welcomeMessageEnglish) {
		this.welcomeMessageEnglish = welcomeMessageEnglish;
	}

	@NotBlank
	public String getWelcomeMessageSpanish() {
		return this.welcomeMessageSpanish;
	}

	public void setWelcomeMessageSpanish(String welcomeMessageSpanish) {
		this.welcomeMessageSpanish = welcomeMessageSpanish;
	}

	@NotBlank
	public String getSystemName() {
		return this.systemName;
	}

	public void setSystemName(String systemName) {
		this.systemName = systemName;
	}

	@NotBlank
	@URL
	public String getImageURL() {
		return this.imageURL;
	}

	public void setImageURL(String imageURL) {
		this.imageURL = imageURL;
	}

	@NotNull
	@Valid
	public Integer getMinFinderResults() {
		return this.minFinderResults;
	}

	public void setMinFinderResults(Integer minFinderResults) {
		this.minFinderResults = minFinderResults;
	}

	@NotNull
	@Valid
	public Integer getMaxFinderResults() {
		return this.maxFinderResults;
	}

	public void setMaxFinderResults(Integer maxFinderResults) {
		this.maxFinderResults = maxFinderResults;
	}

	@NotNull
	@Valid
	public Integer getTimeFinderPrisoners() {
		return this.timeFinderPrisoners;
	}

	public void setTimeFinderPrisoners(Integer timeFinderPrisoners) {
		this.timeFinderPrisoners = timeFinderPrisoners;
	}

	@NotNull
	@Valid
	public Integer getTimeFinderActivities() {
		return this.timeFinderActivities;
	}

	public void setTimeFinderActivities(Integer timeFinderActivities) {
		this.timeFinderActivities = timeFinderActivities;
	}

}
