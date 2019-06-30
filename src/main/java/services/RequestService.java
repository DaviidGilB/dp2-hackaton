
package services;

import java.util.List;

import javax.transaction.Transactional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.i18n.LocaleContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.util.Assert;
import org.springframework.validation.BindingResult;
import org.springframework.validation.FieldError;
import org.springframework.validation.Validator;

import domain.Activity;
import domain.ActivityStatus;
import domain.Prisoner;
import domain.Request;
import domain.SocialWorker;
import repositories.RequestRepository;

@Service
@Transactional
public class RequestService {

	@Autowired
	private RequestRepository requestRepository;

	@Autowired
	private PrisonerService prisonerService;

	@Autowired
	private ActivityService activityService;

	@Autowired
	private SocialWorkerService socialWorkerService;

	@Autowired
	private Validator validator;

	// CRUS

	public Request create() {
		Request res = new Request();

		res.setActivity(null);
		res.setMotivation("");
		res.setRejectReason("");
		res.setStatus(ActivityStatus.PENDING);

		return res;
	}

	public Request findeOne(Integer id) {
		return this.requestRepository.findOne(id);
	}

	public List<Request> findAll() {
		return this.requestRepository.findAll();
	}

	public void delete(Request request) {
		this.requestRepository.delete(request);
	}

	public Request save(Request request) {
		return this.requestRepository.save(request);
	}

	public void deleteRequest(Request request) {

		Activity activity = this.activityService.findOne(request.getActivity().getId());

		Prisoner prisoner = request.getPrisoner();

		List<Request> activityRequests = activity.getRequests();
		List<Request> prisonerRequests = prisoner.getRequests();

		activityRequests.remove(request);
		prisonerRequests.remove(request);

		prisoner.setRequests(prisonerRequests);
		activity.setRequests(activityRequests);

		this.activityService.save(activity);
		this.prisonerService.save(prisoner);

		this.delete(request);

	}
	// Request Prisoner ------------------------------------------------------

	public Request reconstructPrisoner(Request request, Integer activityId, BindingResult binding) {
		Request res = new Request();

		res.setId(0);
		res.setStatus(ActivityStatus.PENDING);
		res.setRejectReason("");
		res.setMotivation(request.getMotivation());
		res.setActivity(this.activityService.findOne(activityId));
		res.setPrisoner(this.prisonerService.loggedPrisoner());
		res.setVersion(0);

		this.validator.validate(res, binding);

		return res;

	}

	public List<Request> getLogguedPrisonerRequests() {
		this.prisonerService.loggedAsPrisoner();
		Prisoner prisoner = this.prisonerService.loggedPrisoner();

		return prisoner.getRequests();
	}

	public void assignRequest(Request request, Integer activityId) {
		Prisoner prisoner = this.prisonerService.loggedPrisoner();
		Activity activity = this.activityService.findOne(activityId);

		request = this.save(request);

		this.requestRepository.flush();

		List<Request> prisonerRequests = prisoner.getRequests();
		List<Request> requests = activity.getRequests();
		prisonerRequests.add(request);
		requests.add(request);
		activity.setRequests(requests);
		prisoner.setRequests(prisonerRequests);

		this.prisonerService.save(prisoner);
		this.activityService.save(activity);

	}

	public void deleteRequestFromPrisoner(Request request) {
		Prisoner prisoner = this.prisonerService.loggedPrisoner();

		List<Request> prisonerRequests = prisoner.getRequests();

		Assert.isTrue(request.getPrisoner().equals(prisoner) && prisonerRequests.contains(request));
		Assert.isTrue(request.getStatus().equals(ActivityStatus.PENDING));

		this.deleteRequest(request);
	}

	// Request Social Worker ---------------------------------------------------

	public List<Request> getRequestsFromSocialWorker(Integer activityId) {
		SocialWorker socialWorker = this.socialWorkerService.loggedSocialWorker();
		return this.requestRepository.getRequestsFromSocialWorker(socialWorker, activityId);
	}

	public void deleteRequestFromSocialWorker(Request request) {
		SocialWorker socialWorker = this.socialWorkerService.loggedSocialWorker();

		Activity activity = this.activityService.findOne(request.getActivity().getId());

		Assert.isTrue(socialWorker.getActivities().contains(activity));
		Assert.isTrue(request.getStatus().equals(ActivityStatus.PENDING));

		this.deleteRequest(request);
	}

	public Request reconstructRejectRequest(Request request, BindingResult binding) {
		Request res = new Request();

		Request requestDB = this.findeOne(request.getId());

		res.setId(request.getId());
		res.setStatus(ActivityStatus.REJECTED);

		String locale = LocaleContextHolder.getLocale().getLanguage().toUpperCase();

		if (request.getRejectReason() == "" || request.getRejectReason() == null)
			if (locale.contains("ES"))
				binding.addError(new FieldError("request", "rejectReason", "El motivo no puede estar vacio"));
			else
				binding.addError(new FieldError("request", "rejectReason", "The reason can not be blank"));
		else
			res.setRejectReason(request.getRejectReason());

		res.setMotivation(requestDB.getMotivation());
		res.setActivity(requestDB.getActivity());
		res.setPrisoner(requestDB.getPrisoner());
		res.setVersion(requestDB.getVersion());

		this.validator.validate(res, binding);

		return res;

	}

	public void approveRequest(Integer requestId, Integer activityId) {
		this.securityRequestSocialWorker(activityId, requestId);

		Request request = this.findeOne(requestId);

		request.setStatus(ActivityStatus.APPROVED);
		this.save(request);

	}

	public void rejectRequest(Request request, Integer activityId) {
		this.securityRequestSocialWorker(activityId, request.getId());

		this.save(request);
	}

	public void securityRequestSocialWorker(Integer activityId, Integer requestId) {
		Request request = this.findeOne(requestId);
		SocialWorker socialWorker = this.socialWorkerService.loggedSocialWorker();

		Activity activity = this.activityService.findOne(activityId);

		Assert.isTrue(socialWorker.getActivities().contains(activity));
		Assert.isTrue(activity.getRequests().contains(request));

	}

	public List<Request> requestToContabilicePoints() {
		return this.requestRepository.requestToContabilicePoints();
	}

	public List<Request> getAprovedRequestByPrisoner(Activity activity) {
		return this.requestRepository.getAprovedRequestByPrisoner(activity);
	}
}
