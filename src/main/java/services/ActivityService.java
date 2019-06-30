package services;

import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.transaction.Transactional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.util.Assert;
import org.springframework.validation.BindingResult;
import org.springframework.validation.Validator;

import domain.Activity;
import domain.FinderActivities;
import domain.Prisoner;
import domain.Request;
import domain.SocialWorker;
import repositories.ActivityRepository;

@Service
@Transactional
public class ActivityService {

	@Autowired
	private ActivityRepository activityRepository;

	@Autowired
	private SocialWorkerService socialWorkerService;

	@Autowired
	private RequestService requestService;

	@Autowired
	private FinderActivitiesService finderActivitiesService;

	@Autowired
	private Validator validator;

	// CRUDS
	public List<Activity> findAll() {
		return this.activityRepository.findAll();
	}

	public Activity findOne(int id) {
		return this.activityRepository.findOne(id);
	}

	public void delete(Activity activity) {
		this.activityRepository.delete(activity);
	}

	public List<Prisoner> getPrisonersPerActivity(Activity a) {
		this.socialWorkerService.loggedAsSocialWorker();
		List<Prisoner> res = new ArrayList<Prisoner>();
		res = this.activityRepository.getPrisonersPerActivity(a);
		return res;
	}

	public List<FinderActivities> getFinderActivitiesByActivity(Activity a) {
		return this.activityRepository.getFindersByActivity(a);
	}

	public Activity save(Activity activity) {
		return this.activityRepository.save(activity);
	}

	// Activities

	public Map<Activity, Integer> getNumberOfApprobedRequestPerActivity(List<Activity> activities) {
		Map<Activity, Integer> res = new HashMap<>();
		for (Activity activity : activities)
			res.put(activity, this.activityRepository.getNumberOfApprobedRequest(activity));
		return res;
	}

	public void securityActivityForRequests(Activity activity) {
		Assert.isTrue(activity.getMaxAssistant() != this.activityRepository.getNumberOfApprobedRequest(activity));
	}

	public List<Activity> getFutureActivites() {
		Date date = new Date();

		return this.activityRepository.getPostActivities(date);

	}

	public List<Activity> getFinalActivitiesSocialWorker(SocialWorker sw) {
		return this.activityRepository.getFinalActivitiesSocialWorker(sw);
	}

	public Activity createActivity() {
		Activity res = new Activity();
		res.setIsFinalMode(false);
		return res;
	}

	public Activity reconstruct(Activity activity, BindingResult binding) {
		this.socialWorkerService.loggedAsSocialWorker();
		SocialWorker sw = this.socialWorkerService.loggedSocialWorker();
		Activity result = new Activity();

		if (activity.getId() == 0)
			result = activity;
		else {
			Activity copy = this.findOne(activity.getId());

			result.setId(copy.getId());
			result.setVersion(copy.getVersion());
			result.setDescription(copy.getDescription());
			result.setIsFinalMode(copy.getIsFinalMode());
			result.setMaxAssistant(copy.getMaxAssistant());
			result.setRewardPoints(copy.getRewardPoints());
			result.setRealizationDate(copy.getRealizationDate());
			result.setTitle(copy.getTitle());
			result.setRequests(copy.getRequests());
		}
		this.validator.validate(result, binding);
		return result;
	}

	public void saveActivity(Activity activity) {
		SocialWorker sw = this.socialWorkerService.loggedSocialWorker();

		if (activity.getId() == 0) {
			Activity saved = this.save(activity);
			List<Activity> la = sw.getActivities();
			la.add(saved);
			sw.setActivities(la);
			this.socialWorkerService.save(sw);
		} else {
			Assert.isTrue(sw.getActivities().contains(activity));
			this.save(activity);
		}
	}

	public void deleteActivity(Activity activity, SocialWorker sw) {
		Assert.isTrue(sw.getActivities().contains(activity));
		List<Request> lr = activity.getRequests();
		for (int i = 0; i < lr.size(); i++) {
			lr.get(i).setPrisoner(null);
			this.requestService.delete(lr.get(i));
		}
		sw.getActivities().remove(activity);
		this.socialWorkerService.save(sw);

		List<FinderActivities> findersOfActivity = this.getFinderActivitiesByActivity(activity);
		for (int i = 0; i < findersOfActivity.size(); i++) {
			FinderActivities finder = findersOfActivity.get(i);
			List<Activity> activities = finder.getActivities();
			activities.remove(activity);
			finder.setActivities(activities);
			this.finderActivitiesService.save(finder);
		}

		this.delete(activity);
	}
}
