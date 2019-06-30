
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

import domain.Activity;
import domain.Box;
import domain.Curriculum;
import domain.FinderActivities;
import domain.PersonalRecord;
import domain.Prisoner;
import domain.Request;
import domain.SocialWorker;
import forms.FormObjectSocialWorker;
import repositories.SocialWorkerRepository;
import security.Authority;
import security.LoginService;
import security.UserAccount;

@Service
@Transactional
public class SocialWorkerService {

	@Autowired
	private SocialWorkerRepository socialWorkerRepository;

	@Autowired
	private CurriculumService curriculumService;

	@Autowired
	private BoxService boxService;

	@Autowired
	private RequestService requestService;

	@Autowired
	private ActivityService activityService;

	@Autowired
	private FinderActivitiesService finderActivitiesService;

	@Autowired
	private PrisonerService prisonerService;

	@Autowired
	private Validator validator;

	// ----------------------------------------CRUD
	// METHODS--------------------------
	// ------------------------------------------------------------------------------

	public SocialWorker create() {
		SocialWorker res = new SocialWorker();

		// SE CREAN LAS LISTAS VACIAS
		List<Box> boxes = new ArrayList<Box>();
		List<Activity> activities = new ArrayList<Activity>();

		// SE AÑADE EL USERNAME Y EL PASSWORD
		UserAccount userAccountActor = new UserAccount();
		userAccountActor.setUsername("");
		userAccountActor.setPassword("");

		// SE A�ADEN TODOS LOS ATRIBUTOS
		res.setName("");
		res.setMiddleName("");
		res.setSurname("");
		res.setPhoto("");
		res.setBoxes(boxes);

		res.setTitle("");
		res.setActivities(activities);

		List<Authority> authorities = new ArrayList<Authority>();

		Authority authority = new Authority();
		authority.setAuthority(Authority.SOCIALWORKER);
		authorities.add(authority);

		userAccountActor.setAuthorities(authorities);
		// NOTLOCKED A TRUE EN LA INICIALIZACION, O SE CREARA UNA CUENTA BANEADA
		userAccountActor.setIsNotLocked(true);

		res.setUserAccount(userAccountActor);

		return res;
	}

	public void saveSocialWorker(SocialWorker socialWorker) {

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

		socialWorker.setBoxes(boxes);

		this.socialWorkerRepository.save(socialWorker);

	}

	public SocialWorker save(SocialWorker SocialWorker) {
		return this.socialWorkerRepository.save(SocialWorker);
	}

	public SocialWorker saveEdit(SocialWorker SocialWorker) {
		this.loggedAsSocialWorker();
		return this.socialWorkerRepository.save(SocialWorker);
	}

	public void deleteLogguedSocialWorker() {
		SocialWorker socialWorker = this.loggedSocialWorker();

		List<Activity> activities = socialWorker.getActivities();
		List<Request> requests = this.socialWorkerRepository.getRequestsBySocialWorker(socialWorker);
		List<Activity> activitiesVoid = new ArrayList<Activity>();
		socialWorker.setActivities(activitiesVoid);
		this.save(socialWorker);

		for (Request r : requests)
			this.requestService.deleteRequest(r);
		for (Activity a : activities) {
			List<FinderActivities> f = this.activityService.getFinderActivitiesByActivity(a);
			for (FinderActivities finder : f) {

				List<Activity> activitiesFinder = finder.getActivities();
				activitiesFinder.remove(a);
				finder.setActivities(activitiesFinder);

				FinderActivities finderSaved = this.finderActivitiesService.save(finder);
				Prisoner prisoner = this.socialWorkerRepository.getPrisonerFromFinder(finder);

				prisoner.setFinderActivities(finderSaved);
				this.prisonerService.save(prisoner);
			}
			this.activityService.delete(a);
		}
		this.socialWorkerRepository.delete(socialWorker);
	}

	// -----------------------------------------SECURITY-----------------------------
	// ------------------------------------------------------------------------------

	/**
	 * LoggedSocialWorker now contains the security of loggedAsSocialWorker
	 *
	 * @return
	 */
	public SocialWorker loggedSocialWorker() {
		UserAccount userAccount;
		userAccount = LoginService.getPrincipal();
		List<Authority> authorities = (List<Authority>) userAccount.getAuthorities();
		Assert.isTrue(authorities.get(0).toString().equals("SOCIALWORKER"));
		return this.socialWorkerRepository.getSocialWorkerByUsername(userAccount.getUsername());
	}

	public void loggedAsSocialWorker() {
		UserAccount userAccount;
		userAccount = LoginService.getPrincipal();
		List<Authority> authorities = (List<Authority>) userAccount.getAuthorities();
		Assert.isTrue(authorities.get(0).toString().equals("SOCIALWORKER"));

	}

	/// ----------------------------------------RECONSTRUCT
	// ------------------------------------------------------------------------------

	public SocialWorker reconstruct(FormObjectSocialWorker formSocialWorker, BindingResult binding) {
		SocialWorker result = this.create();

		result.setName(formSocialWorker.getName());
		result.setMiddleName(formSocialWorker.getMiddleName());
		result.setSurname(formSocialWorker.getSurname());
		result.setPhoto(formSocialWorker.getPhoto());
		result.setTitle(formSocialWorker.getTitle());

		// USER ACCOUNT
		UserAccount userAccount = new UserAccount();

		// Authorities
		List<Authority> authorities = new ArrayList<Authority>();
		Authority authority = new Authority();
		authority.setAuthority(Authority.SOCIALWORKER);
		authorities.add(authority);
		userAccount.setAuthorities(authorities);

		// Username
		userAccount.setUsername(formSocialWorker.getUsername());

		// Password
		Md5PasswordEncoder encoder;
		encoder = new Md5PasswordEncoder();
		userAccount.setPassword(encoder.encodePassword(formSocialWorker.getPassword(), null));
		userAccount.setIsNotLocked(true);

		result.setUserAccount(userAccount);
		String locale = LocaleContextHolder.getLocale().getLanguage().toUpperCase();

		// Confirmacion contrasena
		if (!formSocialWorker.getPassword().equals(formSocialWorker.getConfirmPassword()))
			if (locale.contains("ES"))
				binding.addError(new FieldError("formPrisoner", "password", formSocialWorker.getPassword(), false, null,
						null, "Las contrasenas no coinciden"));
			else
				binding.addError(new FieldError("formPrisoner", "password", formSocialWorker.getPassword(), false, null,
						null, "Passwords don't match"));

		// Confirmacion terminos y condiciones
		if (!formSocialWorker.getTermsAndConditions())
			if (locale.contains("ES"))
				binding.addError(
						new FieldError("formPrisoner", "termsAndConditions", formSocialWorker.getTermsAndConditions(),
								false, null, null, "Debe aceptar los terminos y condiciones"));
			else
				binding.addError(
						new FieldError("formPrisoner", "termsAndConditions", formSocialWorker.getTermsAndConditions(),
								false, null, null, "You must accept the terms and conditions"));

		return result;
	}

	public SocialWorker reconstruct(SocialWorker socialWorker, BindingResult binding) {
		SocialWorker result = new SocialWorker();
		SocialWorker socialWorkerFounded = this.socialWorkerRepository.findOne(socialWorker.getId());

		result = socialWorker;

		result.setVersion(socialWorkerFounded.getVersion());
		result.setBoxes(socialWorkerFounded.getBoxes());
		result.setUserAccount(socialWorkerFounded.getUserAccount());

		this.validator.validate(result, binding);

		return result;
	}

	public void saveNewSocialWorker(SocialWorker SocialWorker) {
		this.loggedAsSocialWorker();
		this.socialWorkerRepository.save(SocialWorker);
	}

	public SocialWorker findOne(int socialId) {
		return this.socialWorkerRepository.findOne(socialId);
	}

	public SocialWorker getSocialWorkerByUsername(String username) {
		return this.socialWorkerRepository.getSocialWorkerByUsername(username);
	}

	public void addCurriculum(PersonalRecord personalRecord) {

		SocialWorker logguedSocialWorker = this.loggedSocialWorker();

		Assert.isNull(logguedSocialWorker.getCurriculum());
		Assert.isTrue(personalRecord.getId() == 0);
		Curriculum curriculum = this.curriculumService.create();
		curriculum.setPersonalRecord(personalRecord);

		logguedSocialWorker.setCurriculum(curriculum);
		this.socialWorkerRepository.save(logguedSocialWorker);

	}

	public void updateCurriculum(PersonalRecord personalRecord) {

		SocialWorker logguedSocialWorker = this.loggedSocialWorker();

		Assert.notNull(logguedSocialWorker.getCurriculum());
		Assert.isTrue(personalRecord.getId() == logguedSocialWorker.getCurriculum().getPersonalRecord().getId());
		Curriculum curriculum = logguedSocialWorker.getCurriculum();
		curriculum.setPersonalRecord(personalRecord);

		logguedSocialWorker.setCurriculum(curriculum);
		this.socialWorkerRepository.save(logguedSocialWorker);

	}

	public void flush() {
		this.socialWorkerRepository.flush();
	}

}
