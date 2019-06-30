package services;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

import javax.transaction.Transactional;

import org.joda.time.LocalDate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.i18n.LocaleContextHolder;
import org.springframework.security.authentication.encoding.Md5PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.util.Assert;
import org.springframework.validation.BindingResult;
import org.springframework.validation.FieldError;
import org.springframework.validation.Validator;

import domain.Box;
import domain.Charge;
import domain.FinderActivities;
import domain.Prisoner;
import domain.Product;
import domain.Request;
import domain.Visit;
import domain.Visitor;
import forms.FormObjectPrisoner;
import repositories.PrisonerRepository;
import security.Authority;
import security.LoginService;
import security.UserAccount;
import utilities.RandomString;

@Service
@Transactional
public class PrisonerService {

	@Autowired
	private PrisonerRepository prisonerRepository;

	@Autowired
	private WardenService wardenService;

	@Autowired
	private BoxService boxService;

	@Autowired
	private FinderActivitiesService finderActivitiesService;

	@Autowired
	private Validator validator;

	// -----------------------------------------SECURITY-----------------------------
	// ------------------------------------------------------------------------------

	public Prisoner save(Prisoner prisoner) {
		return this.prisonerRepository.save(prisoner);
	}

	public Prisoner findOne(int prisonerId) {
		return this.prisonerRepository.findOne(prisonerId);
	}

	public Prisoner loggedPrisoner() {
		UserAccount userAccount;
		userAccount = LoginService.getPrincipal();
		return this.prisonerRepository.getPrisonerByUsername(userAccount.getUsername());
	}

	public void loggedAsPrisoner() {
		UserAccount userAccount;
		userAccount = LoginService.getPrincipal();
		List<Authority> authorities = (List<Authority>) userAccount.getAuthorities();
		Assert.isTrue(authorities.get(0).toString().equals("PRISONER"));
	}

	public List<Prisoner> findAll() {
		return this.prisonerRepository.findAll();
	}

	public List<Visitor> getVisitorsToCreateVisit(Prisoner prisoner) {
		int prisonerId = prisoner.getId();
		return this.prisonerRepository.getVisitorsToCreateVisit(prisonerId);
	}

	public List<Prisoner> getFreePrisoners() {
		return this.prisonerRepository.getFreePrisoners();
	}

	public Prisoner securityAndPrisoner() {
		UserAccount userAccount;
		userAccount = LoginService.getPrincipal();
		List<Authority> authorities = (List<Authority>) userAccount.getAuthorities();
		Assert.isTrue(authorities.get(0).toString().equals("PRISONER"));
		return this.prisonerRepository.getPrisonerByUsername(userAccount.getUsername());
	}

	public Prisoner create() {

		// SE DECLARA EL WARDEN
		Prisoner s = new Prisoner();

		// SE CREAN LAS LISTAS VACIAS
		List<Box> boxes = new ArrayList<Box>();
		List<Request> requests = new ArrayList<Request>();
		List<Product> products = new ArrayList<Product>();
		List<Visit> visits = new ArrayList<Visit>();
		List<Charge> charges = new ArrayList<Charge>();

		// SE AÃ‘ADE EL USERNAME Y EL PASSWORD
		UserAccount userAccountActor = new UserAccount();
		userAccountActor.setUsername("");
		userAccountActor.setPassword("");

		// SE AÑADEN TODOS LOS ATRIBUTOS
		s.setName("");
		s.setMiddleName("");
		s.setSurname("");
		s.setPhoto("");
		s.setBoxes(boxes);

		s.setCrimeRate(-1.);
		s.setIsIsolated(false);
		s.setIsSuspect(false);
		s.setPoints(0);
		s.setFreedom(false);
		s.setCharges(charges);
		s.setRequests(requests);
		s.setProducts(products);
		s.setVisits(visits);

		List<Authority> authorities = new ArrayList<Authority>();

		Authority authority = new Authority();
		authority.setAuthority(Authority.PRISONER);
		authorities.add(authority);

		userAccountActor.setAuthorities(authorities);
		// NOTLOCKED A TRUE EN LA INICIALIZACION, O SE CREARA UNA CUENTA BANEADA
		userAccountActor.setIsNotLocked(true);

		s.setUserAccount(userAccountActor);
		return s;
	}

	// Método auxiliar para generar el ticker-------------------------------
	private String generateTicker() {
		String res = "";
		Date date = null;
		String date1;
		String date2 = LocalDate.now().toString();
		String gen = new RandomString(6).nextString();
		List<Prisoner> lc = this.prisonerRepository.findAll();
		SimpleDateFormat df_in = new SimpleDateFormat("yyMMdd");
		SimpleDateFormat df_output = new SimpleDateFormat("yyyy-MM-dd");
		try {
			date = df_output.parse(date2);
		} catch (ParseException e) {
			e.printStackTrace();
		}
		date1 = df_in.format(date);
		res = res + date1 + "-" + gen;
		for (Prisoner c : lc)
			if (c.getTicker() == res)
				return this.generateTicker();
		return res;
	}

	public void savePrisoner(Prisoner prisoner) {
		this.wardenService.loggedAsWarden();

		if (prisoner.getId() == 0) {
			prisoner.setTicker(this.generateTicker());

			prisoner.setIncomeDate(new Date());

			Calendar exitCalendar = Calendar.getInstance();
			exitCalendar.setTime(prisoner.getIncomeDate());

			for (Charge c : prisoner.getCharges()) {
				exitCalendar.add(Calendar.YEAR, c.getYear());
				exitCalendar.add(Calendar.MONTH, c.getMonth());
			}

			prisoner.setExitDate(exitCalendar.getTime());

			FinderActivities finder = this.finderActivitiesService.create();
			prisoner.setFinderActivities(finder);

			List<Box> boxes = new ArrayList<>();

			// Boxes
			Box box1 = this.boxService.createSystem();
			box1.setName("SUSPICIOUSBOX");
//			Box saved1 = this.boxService.saveSystem(box1);
			boxes.add(box1);

			Box box2 = this.boxService.createSystem();
			box2.setName("TRASHBOX");
//			Box saved2 = this.boxService.saveSystem(box2);
			boxes.add(box2);

			Box box3 = this.boxService.createSystem();
			box3.setName("OUTBOX");
//			Box saved3 = this.boxService.saveSystem(box3);
			boxes.add(box3);

			Box box4 = this.boxService.createSystem();
			box4.setName("INBOX");
//			Box saved4 = this.boxService.saveSystem(box4);
			boxes.add(box4);

			prisoner.setBoxes(boxes);
		} else
			Assert.isTrue(prisoner.getFreedom() == false);

		this.prisonerRepository.save(prisoner);
		this.prisonerRepository.flush();
	}

	public Prisoner reconstruct(FormObjectPrisoner formPrisoner, BindingResult binding) {
		Prisoner result = this.create();

		result.setName(formPrisoner.getName());
		result.setMiddleName(formPrisoner.getMiddleName());
		result.setSurname(formPrisoner.getSurname());
		result.setPhoto(formPrisoner.getPhoto());
		result.setCharges(formPrisoner.getCharges());

		// USER ACCOUNT
		UserAccount userAccount = new UserAccount();

		// Authorities
		List<Authority> authorities = new ArrayList<Authority>();
		Authority authority = new Authority();
		authority.setAuthority(Authority.PRISONER);
		authorities.add(authority);
		userAccount.setAuthorities(authorities);

		// locked
		userAccount.setIsNotLocked(true);

		// Username
		userAccount.setUsername(formPrisoner.getUsername());

		// Password
		Md5PasswordEncoder encoder;
		encoder = new Md5PasswordEncoder();
		userAccount.setPassword(encoder.encodePassword(formPrisoner.getPassword(), null));

		result.setUserAccount(userAccount);

		String locale = LocaleContextHolder.getLocale().getLanguage().toUpperCase();

		// Confirmacion contrasena
		if (!formPrisoner.getPassword().equals(formPrisoner.getConfirmPassword()))
			if (locale.contains("ES"))
				binding.addError(new FieldError("formPrisoner", "password", formPrisoner.getPassword(), false, null,
						null, "Las contrasenas no coinciden"));
			else
				binding.addError(new FieldError("formPrisoner", "password", formPrisoner.getPassword(), false, null,
						null, "Passwords don't match"));

		// Confirmacion terminos y condiciones
		if (!formPrisoner.getTermsAndConditions())
			if (locale.contains("ES"))
				binding.addError(
						new FieldError("formPrisoner", "termsAndConditions", formPrisoner.getTermsAndConditions(),
								false, null, null, "Debe aceptar los terminos y condiciones"));
			else
				binding.addError(
						new FieldError("formPrisoner", "termsAndConditions", formPrisoner.getTermsAndConditions(),
								false, null, null, "You must accept the terms and conditions"));

		// Confirmacion seleccion de minimo un cargo
		if (formPrisoner.getCharges() == null || formPrisoner.getCharges().isEmpty())
			if (locale.contains("ES"))
				binding.addError(new FieldError("formPrisoner", "charges", formPrisoner.getCharges(), false, null, null,
						"Debe seleccionar al menos un cargo"));
			else
				binding.addError(new FieldError("formPrisoner", "charges", formPrisoner.getCharges(), false, null, null,
						"You must select at least one charge"));

		return result;
	}

	public List<Prisoner> getSuspectPrisoners() {
		this.wardenService.loggedAsWarden();
		return this.prisonerRepository.getSuspectPrisoners();
	}

	public List<Prisoner> getIncarceratedPrisoners() {
		return this.prisonerRepository.getIncarceratedPrisoners();
	}

	public Prisoner getPrisonerByUsername(String username) {
		return this.prisonerRepository.getPrisonerByUsername(username);
	}

	public Boolean booleanLogedAsPrisoner() {
		Boolean prisoner = false;
		UserAccount userAccount;
		userAccount = LoginService.getPrincipal();
		List<Authority> authorities = (List<Authority>) userAccount.getAuthorities();
		if (authorities.get(0).toString().equals("PRISONER"))
			prisoner = true;
		return prisoner;
	}

	public Prisoner reconstruct(Prisoner prisoner, BindingResult binding) {
		Prisoner result;
		Prisoner founded = this.findOne(prisoner.getId());

		result = prisoner;

		result.setVersion(founded.getVersion());
		result.setBoxes(founded.getBoxes());
		result.setCharges(founded.getCharges());
		result.setCrimeRate(founded.getCrimeRate());
		result.setExitDate(founded.getExitDate());
		result.setFinderActivities(founded.getFinderActivities());
		result.setFreedom(founded.getFreedom());
		result.setIncomeDate(founded.getIncomeDate());
		result.setIsIsolated(founded.getIsIsolated());
		result.setIsSuspect(founded.getIsSuspect());
		result.setPoints(founded.getPoints());
		result.setProducts(founded.getProducts());
		result.setRequests(founded.getRequests());
		result.setTicker(founded.getTicker());
		result.setUserAccount(founded.getUserAccount());
		result.setVisits(founded.getVisits());

		this.validator.validate(result, binding);

		return result;
	}

	public Prisoner getPrisonerAsWarden(int prisonerId) {
		this.wardenService.loggedAsWarden();
		return this.findOne(prisonerId);
	}

	public List<Prisoner> getPrisonersWithProductsOfASalesMan(int salesmanId) {
		return this.prisonerRepository.getPrisonersWithProductsOfASalesMan(salesmanId);
	}

	public List<Product> getProductsOfLoggedPrisoner() {
		return this.securityAndPrisoner().getProducts();
	}

	public Date dateToGetFree(Prisoner prisoner) throws ParseException {

		Integer months = this.prisonerRepository.totalMonthsOfCharges(prisoner);
		Integer years = this.prisonerRepository.totalYearsOfCharges(prisoner);

		Calendar exitCalendar = Calendar.getInstance();
		exitCalendar.setTime(prisoner.getIncomeDate());

		exitCalendar.add(Calendar.YEAR, years);
		exitCalendar.add(Calendar.MONTH, months);

		return exitCalendar.getTime();
	}

	public void calculateExitDateForProsioner(Prisoner p) throws ParseException {

		Date date = this.dateToGetFree(p);

		p.setExitDate(date);
		this.save(p);
	}

	public List<Prisoner> getPrisonersToBeFree() {
		return this.prisonerRepository.getPrisonersToBeFree();
	}

	public void flush() {
		this.prisonerRepository.flush();

	}
}
