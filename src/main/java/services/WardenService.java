
package services;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.transaction.Transactional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.i18n.LocaleContextHolder;
import org.springframework.security.authentication.encoding.Md5PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.util.Assert;
import org.springframework.validation.BindingResult;
import org.springframework.validation.FieldError;
import org.springframework.validation.Validator;

import domain.ActivityStatus;
import domain.Actor;
import domain.Box;
import domain.Charge;
import domain.Guard;
import domain.Message;
import domain.Prisoner;
import domain.Request;
import domain.TypeProduct;
import domain.Visit;
import domain.VisitStatus;
import domain.Visitor;
import domain.Warden;
import forms.FormObjectWarden;
import repositories.WardenRepository;
import security.Authority;
import security.LoginService;
import security.UserAccount;

@Service
@Transactional
public class WardenService {

	@Autowired
	private WardenRepository wardenRepository;

	@Autowired
	private BoxService boxService;

	@Autowired
	private ActorService actorService;

	@Autowired
	private MessageService messageService;

	@Autowired
	private Validator validator;

	@Autowired
	private GuardService guardService;

	@Autowired
	private PrisonerService prisonerService;

	@Autowired
	private VisitService visitService;

	@Autowired
	private RequestService requestService;

	// ----------------------------------------CRUD
	// METHODS--------------------------
	// ------------------------------------------------------------------------------

	public Warden save(Warden Warden) {
		return this.wardenRepository.save(Warden);
	}

	public Warden create() {

		// SE DECLARA EL WARDEN
		Warden s = new Warden();

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
		s.setBoxes(boxes);
		s.setEmail("");

		List<Authority> authorities = new ArrayList<Authority>();

		Authority authority = new Authority();
		authority.setAuthority(Authority.WARDEN);
		authorities.add(authority);

		userAccountActor.setAuthorities(authorities);
		// NOTLOCKED A TRUE EN LA INICIALIZACION, O SE CREARA UNA CUENTA BANEADA
		userAccountActor.setIsNotLocked(true);

		s.setUserAccount(userAccountActor);
		return s;
	}

	// -----------------------------------------SECURITY-----------------------------
	// ------------------------------------------------------------------------------

	/**
	 * LoggedWarden now contains the security of loggedAsWarden
	 *
	 * @return
	 */
	public Warden loggedWarden() {
		UserAccount userAccount;
		userAccount = LoginService.getPrincipal();
		List<Authority> authorities = (List<Authority>) userAccount.getAuthorities();
		Assert.isTrue(authorities.get(0).toString().equals("WARDEN"));
		return this.wardenRepository.getWardenByUsername(userAccount.getUsername());
	}

	public void loggedAsWarden() {
		UserAccount userAccount;
		userAccount = LoginService.getPrincipal();
		List<Authority> authorities = (List<Authority>) userAccount.getAuthorities();
		Assert.isTrue(authorities.get(0).toString().equals("WARDEN"));
	}

	public void saveWarden(Warden warden) {
		this.loggedAsWarden();

		if (warden.getId() == 0) {
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

			warden.setBoxes(boxes);
		}

		this.wardenRepository.save(warden);
	}

	public Warden securityAndWarden() {
		UserAccount userAccount;
		userAccount = LoginService.getPrincipal();
		List<Authority> authorities = (List<Authority>) userAccount.getAuthorities();
		Assert.isTrue(authorities.get(0).toString().equals("WARDEN"));
		return this.wardenRepository.getWardenByUsername(userAccount.getUsername());
	}

	public Warden reconstruct(FormObjectWarden formWarden, BindingResult binding) {
		Warden result = this.create();

		result.setName(formWarden.getName());
		result.setMiddleName(formWarden.getMiddleName());
		result.setSurname(formWarden.getSurname());
		result.setPhoto(formWarden.getPhoto());
		result.setEmail(formWarden.getEmail());

		// USER ACCOUNT
		UserAccount userAccount = new UserAccount();

		// Authorities
		List<Authority> authorities = new ArrayList<Authority>();
		Authority authority = new Authority();
		authority.setAuthority(Authority.WARDEN);
		authorities.add(authority);
		userAccount.setAuthorities(authorities);

		// locked
		userAccount.setIsNotLocked(true);

		// Username
		userAccount.setUsername(formWarden.getUsername());

		// Password
		Md5PasswordEncoder encoder;
		encoder = new Md5PasswordEncoder();
		userAccount.setPassword(encoder.encodePassword(formWarden.getPassword(), null));

		result.setUserAccount(userAccount);

		String locale = LocaleContextHolder.getLocale().getLanguage().toUpperCase();

		// Confirmacion contrasena
		if (!formWarden.getPassword().equals(formWarden.getConfirmPassword()))
			if (locale.contains("ES"))
				binding.addError(new FieldError("formWarden", "password", formWarden.getPassword(), false, null, null,
						"Las contrasenas no coinciden"));
			else
				binding.addError(new FieldError("formWarden", "password", formWarden.getPassword(), false, null, null,
						"Passwords don't match"));

		// Confirmacion terminos y condiciones
		if (!formWarden.getTermsAndConditions())
			if (locale.contains("ES"))
				binding.addError(new FieldError("formWarden", "termsAndConditions", formWarden.getTermsAndConditions(),
						false, null, null, "Debe aceptar los terminos y condiciones"));
			else
				binding.addError(new FieldError("formWarden", "termsAndConditions", formWarden.getTermsAndConditions(),
						false, null, null, "You must accept the terms and conditions"));

		return result;
	}

	public void isolatePrisoner(Prisoner prisoner) {
		this.loggedAsWarden();
		List<Prisoner> suspects = this.prisonerService.getSuspectPrisoners();
		Assert.notNull(prisoner);
		Assert.isTrue(suspects.contains(prisoner));

		List<Visit> visits = this.wardenRepository.getFutureVisitsByPrisoner(prisoner.getId());
		List<Request> requests = this.wardenRepository.getRequestToFutureActivitiesByPrisoner(prisoner.getId());

		for (Visit v : visits) {
			v.setVisitStatus(VisitStatus.REJECTED);
			this.visitService.save(v);
		}
		for (Request r : requests) {
			r.setRejectReason("Isolated");
			r.setStatus(ActivityStatus.REJECTED);
			this.requestService.save(r);
		}

		prisoner.getUserAccount().setIsNotLocked(false);
		Charge charge = this.wardenRepository.getSuspiciousCharge();
		prisoner.getCharges().add(charge);
		prisoner.setIsIsolated(true);

		Date exitDate = prisoner.getExitDate();
		Calendar cExit = Calendar.getInstance();
		cExit.setTime(exitDate);
		cExit.add(Calendar.MONTH, charge.getMonth());
		cExit.add(Calendar.YEAR, charge.getYear());
		Date newExitDate = cExit.getTime();
		prisoner.setExitDate(newExitDate);

		this.prisonerService.save(prisoner);

	}

	public Warden findOne(int id) {
		return this.wardenRepository.findOne(id);
	}

	public boolean loggedAsWardenBoolean() {
		UserAccount userAccount;
		Boolean isWarden = false;
		userAccount = LoginService.getPrincipal();
		List<Authority> authorities = (List<Authority>) userAccount.getAuthorities();
		if (authorities.get(0).toString().equals("WARDEN"))
			isWarden = true;
		return isWarden;
	}

	public Message reconstruct(Message message, BindingResult binding) {

		UserAccount userAccount;
		userAccount = LoginService.getPrincipal();
		Actor actor = this.actorService.getActorByUsername(userAccount.getUsername());

		domain.Message result;
		result = this.messageService.create();
		if (message.getId() == 0) {
			result = message;
			result.setSender(actor.getUserAccount().getUsername());
			Date thisMoment = new Date();
			thisMoment.setTime(thisMoment.getTime() - 1000);
			result.setMoment(thisMoment);
			result.setRecipient(actor.getUserAccount().getUsername());

		} else {
			result = this.messageService.findOne(message.getId());

			result.setBody(message.getBody());
			result.setPriority(message.getPriority());
			result.setTags(message.getTags());
			result.setSubject(message.getSubject());
			result.setRecipient(actor.getUserAccount().getUsername());
			result.setSender(actor.getUserAccount().getUsername());

		}

		this.validator.validate(result, binding);

		return result;

	}

	public void broadcastMessage(Message message) {
		this.loggedAsWarden();

		UserAccount userAccount;
		userAccount = LoginService.getPrincipal();
		Actor admin = this.actorService.getActorByUsername(userAccount.getUsername());

		List<Actor> actors = new ArrayList<Actor>();
		actors = this.actorService.findAll();

		message.setRecipient("BROADCAST");
		Message messageWardenSaved = this.messageService.save(message);

		Box outBox = this.boxService.getSentBoxByActor(admin);

		outBox.getMessages().add(messageWardenSaved);

		this.actorService.save(admin);

		for (Actor a : actors)
			if (!(a.equals(admin))) {
				message.setRecipient(a.getUserAccount().getUsername());
				this.messageService.sendMessageBroadcasted(message);
			}

	}

	public void broadcastMessageGuards(Message message) {
		this.loggedAsWarden();

		UserAccount userAccount;
		userAccount = LoginService.getPrincipal();
		Actor admin = this.actorService.getActorByUsername(userAccount.getUsername());

		List<Guard> guards = new ArrayList<Guard>();
		guards = this.guardService.findAll();

		message.setRecipient("BROADCAST");
		Message messageWardenSaved = this.messageService.save(message);

		Box outBox = this.boxService.getSentBoxByActor(admin);

		outBox.getMessages().add(messageWardenSaved);

		this.actorService.save(admin);

		for (Guard a : guards) {
			message.setRecipient(a.getUserAccount().getUsername());
			this.messageService.sendMessageBroadcasted(message);
		}

	}

	public void broadcastMessagePrisoners(Message message) {
		this.loggedAsWarden();

		UserAccount userAccount;
		userAccount = LoginService.getPrincipal();
		Actor admin = this.actorService.getActorByUsername(userAccount.getUsername());

		List<Prisoner> prisoners = new ArrayList<Prisoner>();
		prisoners = this.prisonerService.findAll();

		message.setRecipient("BROADCAST");
		Message messageWardenSaved = this.messageService.save(message);

		Box outBox = this.boxService.getSentBoxByActor(admin);

		outBox.getMessages().add(messageWardenSaved);

		this.actorService.save(admin);

		for (Prisoner a : prisoners) {
			message.setRecipient(a.getUserAccount().getUsername());
			this.messageService.sendMessageBroadcasted(message);
		}

	}

	public Warden reconstruct(Warden warden, BindingResult binding) {
		Warden result = new Warden();
		Warden wardenFounded = this.wardenRepository.findOne(warden.getId());

		result = warden;

		result.setVersion(wardenFounded.getVersion());
		result.setBoxes(wardenFounded.getBoxes());
		result.setUserAccount(wardenFounded.getUserAccount());

		this.validator.validate(result, binding);

		return result;
	}

	public void deleteLoggedWarden() {
		Warden warden = this.securityAndWarden();
		Assert.isTrue(this.findAll().size() > 1);
		this.wardenRepository.delete(warden.getId());
	}

	public Prisoner getPrisonerAsWarden(int prisonerId) {
		this.loggedAsWarden();
		return this.prisonerService.findOne(prisonerId);
	}

	public List<Float> statistics() {
		List<Float> statistics = new ArrayList<Float>();

		statistics.add(this.wardenRepository.getRatioOfVisitsWithReport());
		statistics.add(this.wardenRepository.getRatioOfGuardsWithMoreThan50PercentOfVisitsWithReport());
		statistics.add(this.wardenRepository.getRatioOfPrisonersWithoutVisitsLastMonth());
		statistics.add(this.wardenRepository.getRegularVisitorToAtLeastOnePrisoner());
		statistics.add(this.wardenRepository.getRatioOfAvailableGuardsVsFutureVisitsWithoutGuard());
		statistics.add(this.wardenRepository.getRatioOfNonIsolatedVsIsolatedPrisoners());

		Float[] crime = this.wardenRepository.getStatisticsCrimeRate();
		statistics.add(crime[0]);
		statistics.add(crime[1]);
		statistics.add(crime[2]);
		statistics.add(crime[3]);

		statistics.add(this.wardenRepository.getRatioSocialWorkersWithCurriculum());

		return statistics;
	}

	public Map<String, List<String>> getCouplesWithMostVisits() {
		Map<String, List<String>> result = new HashMap<String, List<String>>();
		List<Visitor> visitors = this.wardenRepository.getVisitorsMostVisitsToAPrisoner();

		for (Visitor v : visitors)
			result.put(v.getUserAccount().getUsername(),
					this.wardenRepository.getPrisonersWithMostVisitToAVisitor(v.getId()));
		return result;
	}

	public List<String> getPrisonersWithVisitsToMostDifferentVisitors() {
		return this.wardenRepository.getPrisonersWithVisitsToMostDifferentVisitors();
	}

	public List<String> getVisitorsWithVisitsToMostDifferentPrisoners() {
		return this.wardenRepository.getVisitorsWithVisitsToMostDifferentPrisoners();
	}

	public List<String> getGuardsWithTheLargestNumberOfReportsWritten() {
		return this.wardenRepository.getGuardsWithTheLargestNumberOfReportsWritten();
	}

	public List<String> getTop3PrisonersLowestCrimeRate() {
		List<String> result = this.wardenRepository.getTop3PrisonersLowestCrimeRate();
		if (result.size() > 3)
			return result.subList(0, 2);
		return result;
	}

	public List<String> getSocialWorkerMostActivitiesFull() {
		return this.wardenRepository.getSocialWorkerMostActivitiesFull();
	}

	public List<String> getPrisonersMostRejectedRequestToDifferentActivitiesAndNoApprovedOnThoseActivities() {
		return this.wardenRepository
				.getPrisonersMostRejectedRequestToDifferentActivitiesAndNoApprovedOnThoseActivities();
	}

	public List<String> getActivitiesLargestNumberPrisoners() {
		return this.wardenRepository.getActivitiesLargestNumberPrisoners();
	}

	public List<String> getActivitiesLargestAvgCrimeRate() {
		return this.wardenRepository.getActivitiesLargestAvgCrimeRate();
	}

	public List<String> getActivitiesSmallestAvgCrimeRate() {
		return this.wardenRepository.getActivitiesSmallestAvgCrimeRate();
	}

	public List<String> getSocialWorkersLowestRatioPrisonersPerActivity() {
		return this.wardenRepository.getSocialWorkersLowestRatioPrisonersPerActivity();
	}

	public List<String> getActivitiesMostSearched() {
		return this.wardenRepository.getActivitiesMostSearched();
	}

	public List<String> getTop5PrisonersParticipatedMostActivitiesLastMonth() {
		List<String> result = this.wardenRepository.getTop5PrisonersParticipatedMostActivitiesLastMonth();
		if (result.size() > 5)
			return result.subList(0, 4);
		return result;
	}

	public List<TypeProduct> getProductTypesAssigned() {
		return this.wardenRepository.getProductTypesAssigned();
	}

	public Charge getSuspiciousCharge() {
		return this.wardenRepository.getSuspiciousCharge();
	}

	public void flush() {
		this.wardenRepository.flush();
	}

	public List<Warden> findAll() {
		return this.wardenRepository.findAll();
	}

}
