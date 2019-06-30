
package services;

import java.util.ArrayList;
import java.util.List;

import javax.transaction.Transactional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.i18n.LocaleContextHolder;
import org.springframework.security.authentication.encoding.Md5PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.util.Assert;
import org.springframework.validation.BindingResult;
import org.springframework.validation.FieldError;
import org.springframework.validation.Validator;

import repositories.VisitorRepository;
import security.Authority;
import security.LoginService;
import security.UserAccount;
import domain.Box;
import domain.Finder;
import domain.Visit;
import domain.Visitor;
import forms.FormObjectVisitor;

@Service
@Transactional
public class VisitorService {

	@Autowired
	private VisitorRepository	visitorRepository;

	@Autowired
	private BoxService			boxService;

	@Autowired
	private Validator			validator;


	// -----------------------------------------SECURITY-----------------------------
	// ------------------------------------------------------------------------------

	public Visitor save(Visitor visitor) {

		if (visitor.getId() == 0) {

			List<Box> boxes = new ArrayList<>();

			// Boxes
			Box box1 = this.boxService.createSystem();
			box1.setName("SUSPICIOUSBOX");
			Box saved1 = this.boxService.saveSystem(box1);
			boxes.add(saved1);

			Box box2 = this.boxService.createSystem();
			box2.setName("TRASHBOX");
			Box saved2 = this.boxService.saveSystem(box2);
			boxes.add(saved2);

			Box box3 = this.boxService.createSystem();
			box3.setName("OUTBOX");
			Box saved3 = this.boxService.saveSystem(box3);
			boxes.add(saved3);

			Box box4 = this.boxService.createSystem();
			box4.setName("INBOX");
			Box saved4 = this.boxService.saveSystem(box4);
			boxes.add(saved4);

			visitor.setBoxes(boxes);
		}

		return this.visitorRepository.save(visitor);
	}

	public Visitor loggedVisitor() {
		UserAccount userAccount;
		userAccount = LoginService.getPrincipal();
		return this.visitorRepository.getVisitorByUsername(userAccount.getUsername());
	}

	public void loggedAsVisitor() {
		UserAccount userAccount;
		userAccount = LoginService.getPrincipal();
		List<Authority> authorities = (List<Authority>) userAccount.getAuthorities();
		Assert.isTrue(authorities.get(0).toString().equals("VISITOR"));
	}

	public Visitor findOne(int visitorId) {
		return this.visitorRepository.findOne(visitorId);
	}

	public Visitor create() {

		// SE DECLARA EL GUARD
		Visitor s = new Visitor();

		// SE CREAN LAS LISTAS VACIAS
		List<Box> boxes = new ArrayList<Box>();

		// SE AÑADE EL USERNAME Y EL PASSWORD
		UserAccount userAccountActor = new UserAccount();
		userAccountActor.setUsername("");
		userAccountActor.setPassword("");

		// SE AÑADEN TODOS LOS ATRIBUTOS
		s.setName("");
		s.setMiddleName("");
		s.setSurname("");
		s.setPhoto("");
		s.setEmail("");
		s.setPhoneNumber("");
		s.setEmergencyEmail("");
		s.setAddress("");

		Finder finder = new Finder();
		s.setFinder(finder);

		List<Visit> visits = new ArrayList<Visit>();
		s.setVisits(visits);

		List<Authority> authorities = new ArrayList<Authority>();

		Authority authority = new Authority();
		authority.setAuthority(Authority.VISITOR);
		authorities.add(authority);

		userAccountActor.setAuthorities(authorities);
		// NOTLOCKED A TRUE EN LA INICIALIZACION, O SE CREARA UNA CUENTA BANEADA
		userAccountActor.setIsNotLocked(true);

		s.setUserAccount(userAccountActor);
		return s;
	}

	public Visitor reconstruct(FormObjectVisitor formVisitor, BindingResult binding) {
		Visitor result = this.create();

		result.setName(formVisitor.getName());
		result.setMiddleName(formVisitor.getMiddleName());
		result.setSurname(formVisitor.getSurname());
		result.setPhoto(formVisitor.getPhoto());
		result.setPhoneNumber(formVisitor.getPhoneNumber());
		result.setEmail(formVisitor.getEmail());
		result.setEmergencyEmail(formVisitor.getEmailEmergency());
		result.setAddress(formVisitor.getAddress());

		// USER ACCOUNT
		UserAccount userAccount = new UserAccount();

		// Authorities
		List<Authority> authorities = new ArrayList<Authority>();
		Authority authority = new Authority();
		authority.setAuthority(Authority.VISITOR);
		authorities.add(authority);
		userAccount.setAuthorities(authorities);

		// locked
		userAccount.setIsNotLocked(true);

		// Username
		userAccount.setUsername(formVisitor.getUsername());

		// Password
		Md5PasswordEncoder encoder;
		encoder = new Md5PasswordEncoder();
		userAccount.setPassword(encoder.encodePassword(formVisitor.getPassword(), null));

		result.setUserAccount(userAccount);

		String locale = LocaleContextHolder.getLocale().getLanguage().toUpperCase();

		// Confirmacion contrasena
		if (!formVisitor.getPassword().equals(formVisitor.getConfirmPassword()))
			if (locale.contains("ES"))
				binding.addError(new FieldError("formVisitor", "password", formVisitor.getPassword(), false, null, null, "Las contrasenas no coinciden"));
			else
				binding.addError(new FieldError("formVisitor", "password", formVisitor.getPassword(), false, null, null, "Passwords don't match"));

		// Confirmacion terminos y condiciones
		if (!formVisitor.getTermsAndConditions())
			if (locale.contains("ES"))
				binding.addError(new FieldError("formVisitor", "termsAndConditions", formVisitor.getTermsAndConditions(), false, null, null, "Debe aceptar los terminos y condiciones"));
			else
				binding.addError(new FieldError("formVisitor", "termsAndConditions", formVisitor.getTermsAndConditions(), false, null, null, "You must accept the terms and conditions"));

		return result;
	}

	public Visitor reconstruct(Visitor salesman, BindingResult binding) {
		Visitor result = this.create();
		Visitor founded = this.findOne(salesman.getId());

		result = salesman;

		result.setVersion(founded.getVersion());
		result.setBoxes(founded.getBoxes());
		result.setVisits(founded.getVisits());
		result.setFinder(founded.getFinder());
		result.setUserAccount(founded.getUserAccount());

		this.validator.validate(result, binding);

		return result;
	}

	public void deleteLoggedVisitor() {
		this.loggedAsVisitor();
		Visitor visitor = this.loggedVisitor();

		visitor.getFinder().getPrisoners().clear();

		visitor.getVisits().clear();

		this.visitorRepository.delete(visitor);
	}

	public void flush() {
		this.visitorRepository.flush();
	}
}
