
package services;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import javax.transaction.Transactional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.i18n.LocaleContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.util.Assert;
import org.springframework.validation.BindingResult;
import org.springframework.validation.FieldError;
import org.springframework.validation.Validator;

import domain.Configuration;
import domain.TypeProduct;
import repositories.ConfigurationRepository;
import security.Authority;
import security.LoginService;
import security.UserAccount;

@Service
@Transactional
public class ConfigurationService {

	@Autowired
	private ConfigurationRepository configurationRepository;

	@Autowired
	private WardenService wardenService;

	@Autowired
	private TypeProductService typeProductService;

	@Autowired
	private Validator validator;

	public Configuration getConfiguration() {
		return this.configurationRepository.findAll().get(0);
	}

	public Configuration save(Configuration configuration) {
		return this.configurationRepository.save(configuration);
	}

	public void saveConfiguration(Configuration configuration) {
		this.wardenService.loggedAsWarden();
		this.save(configuration);
	}

	public Boolean isStringSpam(String s, List<String> spamWords) {
		Boolean result = false;

		List<String> trimmedString = new ArrayList<String>();
		trimmedString = Arrays.asList(s.split("\\+|(?=[,.¿?;!¡])"));

		// ("\\s*(=>|,|\\s)\\s*"));
		for (String g : spamWords)
			for (String c : trimmedString)
				if (g.equals(c) || g.equalsIgnoreCase(c)) {
					result = true;
					break;
				}

		return result;
	}

	public void loggedAsWarden() {
		UserAccount userAccount;
		userAccount = LoginService.getPrincipal();
		List<Authority> authorities = (List<Authority>) userAccount.getAuthorities();
		Assert.isTrue(authorities.get(0).toString().equals("Warden"));
	}

	public Configuration reconstruct(Configuration configuration, BindingResult binding) {
		this.wardenService.loggedAsWarden();

		Configuration result = new Configuration();

		Configuration configurationBefore = this.configurationRepository.findOne(configuration.getId());

		result.setId(configurationBefore.getId());
		result.setVersion(configurationBefore.getVersion());

		result.setFinderResult(configuration.getFinderResult());
		result.setSpainTelephoneCode(configuration.getSpainTelephoneCode());
		result.setWelcomeMessageEnglish(configuration.getWelcomeMessageEnglish());
		result.setWelcomeMessageSpanish(configuration.getWelcomeMessageSpanish());
		result.setSystemName(configuration.getSystemName());
		result.setImageURL(configuration.getImageURL());
		result.setTypeProducts(configuration.getTypeProducts());
		result.setMaxFinderResults(configuration.getMaxFinderResults());
		result.setMinFinderResults(configuration.getMinFinderResults());
		result.setTimeFinderPrisoners(configuration.getTimeFinderPrisoners());
		result.setFinderResult(configuration.getFinderResult());
		result.setTimeFinderActivities(configuration.getTimeFinderActivities());

		this.validator.validate(result, binding);

		return result;

	}

	public List<String> showSpamWordsList() {
		this.wardenService.loggedAsWarden();

		List<String> spamWordsList = this.configurationRepository.spamWords();

		return spamWordsList;
	}

	public void addSpamWords(String word, BindingResult binding) {
		this.wardenService.loggedAsWarden();
		String locale = LocaleContextHolder.getLocale().getLanguage().toUpperCase();
		Configuration configuration = this.configurationRepository.configuration();
		List<String> spamWords = configuration.getSpamWords();
		if (spamWords.contains(word)) {
			if (locale.contains("ES"))
				binding.addError(new FieldError("spamWords", "word", word, false, null, null,
						"La palabra ya esta contenida en la lista de palabras spam."));
			else
				binding.addError(new FieldError("spamWords", "word", word, false, null, null,
						"The word is already contained in the list of spam words."));
		} else {
			spamWords.add(word);
			configuration.setSpamWords(spamWords);
			this.configurationRepository.save(configuration);
		}
	}

	public void deleteSpamWords(String word) {
		this.wardenService.loggedAsWarden();
		Configuration configuration = this.configurationRepository.configuration();
		List<String> spamWords = configuration.getSpamWords();
		spamWords.remove(word);
		configuration.setSpamWords(spamWords);
		this.configurationRepository.save(configuration);
	}

	public void addTypeProducts(String typeEN, String typeES, BindingResult binding) {
		this.wardenService.loggedAsWarden();
		String locale = LocaleContextHolder.getLocale().getLanguage().toUpperCase();
		Configuration configuration = this.configurationRepository.configuration();
		List<String> typeProductsEN = new ArrayList<String>();
		for (TypeProduct p : configuration.getTypeProducts())
			typeProductsEN.add(p.getTypeProductEN());
		List<String> typeProductsES = new ArrayList<String>();
		for (TypeProduct p : configuration.getTypeProducts())
			typeProductsES.add(p.getTypeProductES());
		if (typeProductsEN.contains(typeEN) || typeProductsES.contains(typeES)) {
			if (locale.contains("ES")) {
				binding.addError(new FieldError("typeProductsEN", "typeEN", typeEN, false, null, null,
						"La palabra ya esta contenida en la lista de tipos de productos."));
				binding.addError(new FieldError("typeProductsES", "typeES", typeES, false, null, null,
						"La palabra ya esta contenida en la lista de tipos de productos."));
			} else {
				binding.addError(new FieldError("typeProductsEN", "typeEN", typeEN, false, null, null,
						"The word is already contained in the list of type products."));
				binding.addError(new FieldError("typeProductsES", "typeES", typeES, false, null, null,
						"The word is already contained in the list of type products."));
			}
		} else {

			TypeProduct p = new TypeProduct();
			p.setTypeProductEN(typeEN);
			p.setTypeProductES(typeES);
			TypeProduct saved = this.typeProductService.save(p);
			List<TypeProduct> lp = configuration.getTypeProducts();
			lp.add(saved);
			configuration.setTypeProducts(lp);
			this.configurationRepository.save(configuration);
		}
	}

	public void deleteTypeProducts(int id) {
		this.wardenService.loggedAsWarden();

		TypeProduct tp = this.typeProductService.findOne(id);
		List<TypeProduct> lt = this.wardenService.getProductTypesAssigned();

		Assert.isTrue(!lt.contains(tp));

		Configuration configuration = this.configurationRepository.configuration();

		List<TypeProduct> lp = this.typeProductService.findAll();
		Assert.isTrue(lp.contains(tp));
		lp.remove(tp);
		this.typeProductService.delete(tp);
		configuration.setTypeProducts(lp);
		this.configurationRepository.save(configuration);
	}

}
