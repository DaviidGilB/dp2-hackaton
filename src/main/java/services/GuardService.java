
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

import repositories.GuardRepository;
import security.Authority;
import security.LoginService;
import security.UserAccount;
import domain.Box;
import domain.Guard;
import domain.Product;
import domain.Report;
import domain.Visit;
import forms.FormObjectGuard;

@Service
@Transactional
public class GuardService {

	@Autowired
	private GuardRepository	guardRepository;

	@Autowired
	private WardenService	wardenService;

	@Autowired
	private BoxService		boxService;

	@Autowired
	private VisitService	visitService;

	@Autowired
	private ReportService	reportService;

	@Autowired
	private Validator		validator;


	// -----------------------------------------SECURITY-----------------------------
	// ------------------------------------------------------------------------------

	public Guard save(Guard guard) {
		return this.guardRepository.save(guard);
	}

	public Guard loggedGuard() {
		UserAccount userAccount;
		userAccount = LoginService.getPrincipal();
		return this.guardRepository.getGuardByUsername(userAccount.getUsername());
	}

	public void loggedAsGuard() {
		UserAccount userAccount;
		userAccount = LoginService.getPrincipal();
		List<Authority> authorities = (List<Authority>) userAccount.getAuthorities();
		Assert.isTrue(authorities.get(0).toString().equals("GUARD"));
	}

	public Guard findOne(int guardId) {
		return this.guardRepository.findOne(guardId);
	}

	public List<Visit> getFutureAcceptedVisits() {
		return this.guardRepository.getFutureAcceptedVisits();
	}

	public List<Guard> findAll() {
		return this.guardRepository.findAll();
	}

	//CREATE
	public Guard create() {

		// SE DECLARA EL GUARD
		Guard s = new Guard();

		// SE CREAN LAS LISTAS VACIAS
		List<Box> boxes = new ArrayList<Box>();
		List<Product> products = new ArrayList<Product>();

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
		s.setPhone("");

		List<Visit> visits = new ArrayList<Visit>();
		s.setVisits(visits);

		List<Authority> authorities = new ArrayList<Authority>();

		Authority authority = new Authority();
		authority.setAuthority(Authority.GUARD);
		authorities.add(authority);

		userAccountActor.setAuthorities(authorities);
		// NOTLOCKED A TRUE EN LA INICIALIZACION, O SE CREARA UNA CUENTA BANEADA
		userAccountActor.setIsNotLocked(true);

		s.setUserAccount(userAccountActor);
		return s;
	}
	//RECONSTRUCT
	public Guard reconstruct(FormObjectGuard formGuard, BindingResult binding) {
		Guard result = this.create();

		result.setName(formGuard.getName());
		result.setMiddleName(formGuard.getMiddleName());
		result.setSurname(formGuard.getSurname());
		result.setPhoto(formGuard.getPhoto());
		result.setPhone(formGuard.getPhone());
		result.setEmail(formGuard.getEmail());

		// USER ACCOUNT
		UserAccount userAccount = new UserAccount();

		// Authorities
		List<Authority> authorities = new ArrayList<Authority>();
		Authority authority = new Authority();
		authority.setAuthority(Authority.GUARD);
		authorities.add(authority);
		userAccount.setAuthorities(authorities);

		// locked
		userAccount.setIsNotLocked(true);

		// Username
		userAccount.setUsername(formGuard.getUsername());

		// Password
		Md5PasswordEncoder encoder;
		encoder = new Md5PasswordEncoder();
		userAccount.setPassword(encoder.encodePassword(formGuard.getPassword(), null));

		result.setUserAccount(userAccount);

		String locale = LocaleContextHolder.getLocale().getLanguage().toUpperCase();

		// Confirmacion contrasena
		if (!formGuard.getPassword().equals(formGuard.getConfirmPassword()))
			if (locale.contains("ES"))
				binding.addError(new FieldError("formGuard", "password", formGuard.getPassword(), false, null, null, "Las contrasenas no coinciden"));
			else
				binding.addError(new FieldError("formGuard", "password", formGuard.getPassword(), false, null, null, "Passwords don't match"));

		// Confirmacion terminos y condiciones
		if (!formGuard.getTermsAndConditions())
			if (locale.contains("ES"))
				binding.addError(new FieldError("formGuard", "termsAndConditions", formGuard.getTermsAndConditions(), false, null, null, "Debe aceptar los terminos y condiciones"));
			else
				binding.addError(new FieldError("formGuard", "termsAndConditions", formGuard.getTermsAndConditions(), false, null, null, "You must accept the terms and conditions"));

		return result;
	}

	public void saveGuard(Guard guard) {
		this.wardenService.loggedAsWarden();

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

		guard.setBoxes(boxes);

		List<Visit> visits = new ArrayList<>();
		guard.setVisits(visits);

		this.guardRepository.save(guard);
	}

	public Guard securityAndGuard() {
		UserAccount userAccount;
		userAccount = LoginService.getPrincipal();
		List<Authority> authorities = (List<Authority>) userAccount.getAuthorities();
		Assert.isTrue(authorities.get(0).toString().equals("GUARD"));
		return this.guardRepository.getGuardByUsername(userAccount.getUsername());
	}

	public void deleteLoggedGuard() {
		Guard guard = this.securityAndGuard();

		for (Visit visit : guard.getVisits())
			if (visit.getReport() != null) {
				Report report = visit.getReport();
				this.reportService.delete(report.getId());
				visit.setReport(null);
				this.visitService.save(visit.getId());
			}

		this.guardRepository.delete(guard.getId());
	}

	public Guard reconstruct(Guard guard, BindingResult binding) {
		Guard result = new Guard();
		Guard guardFounded = this.guardRepository.findOne(guard.getId());

		result = guard;

		result.setVersion(guardFounded.getVersion());
		result.setBoxes(guardFounded.getBoxes());
		result.setUserAccount(guardFounded.getUserAccount());
		result.setVisits(guardFounded.getVisits());

		this.validator.validate(result, binding);

		return result;
	}

	public void flush() {
		this.guardRepository.flush();
	}

	public Guard saveEdit(Guard guard) {
		this.loggedAsGuard();
		return this.guardRepository.save(guard);
	}
}
