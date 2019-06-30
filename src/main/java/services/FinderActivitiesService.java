
package services;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

import javax.transaction.Transactional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.util.Assert;
import org.springframework.validation.BindingResult;
import org.springframework.validation.Validator;

import domain.Activity;
import domain.FinderActivities;
import domain.Prisoner;
import repositories.FinderActivitiesRepository;

@Service
@Transactional
public class FinderActivitiesService {

	@Autowired
	private FinderActivitiesRepository finderActivitiesRepository;

	@Autowired
	private PrisonerService prisonerService;

	@Autowired
	private ActivityService activityService;

	@Autowired
	private ConfigurationService configurationService;

	@Autowired
	private Validator validator;

	// CRUDS ---------------------------------------------------------------
	public FinderActivities create() {
		FinderActivities res = new FinderActivities();

		List<Activity> activities = new ArrayList<>();

		Date date = new Date();

		res.setActivities(activities);
		res.setKeyWord("");
		res.setMaxDate(null);
		res.setMinDate(null);
		res.setLastEdit(date);

		return res;
	}

	public List<FinderActivities> findAll() {
		return this.finderActivitiesRepository.findAll();
	}

	public FinderActivities findOne(Integer id) {
		return this.finderActivitiesRepository.findOne(id);
	}

	public void delete(FinderActivities finderActivities) {
		this.finderActivitiesRepository.delete(finderActivities);
	}

	public FinderActivities save(FinderActivities finderActivities) {
		return this.finderActivitiesRepository.save(finderActivities);
	}

	// Finder --------------------------------------------------------

	public void filter(FinderActivities finder) {

		Prisoner prisoner = this.prisonerService.loggedPrisoner();

		Assert.notNull(finder);
		Assert.isTrue(finder.getId() > 0);
		Assert.isTrue(prisoner.getFinderActivities().getId() == finder.getId());

		List<Activity> res = this.activityService.getFutureActivites();
		List<Activity> filter = new ArrayList<>();

		if (!finder.getKeyWord().equals(null) && !finder.getKeyWord().contentEquals("")) {
			filter = this.finderActivitiesRepository.filterByKeyWord("%" + finder.getKeyWord() + "%");
			res.retainAll(filter);
		}
		if (finder.getMaxDate() != null) {
			filter = this.finderActivitiesRepository.filterByDateMax(finder.getMaxDate());
			res.retainAll(filter);
		}
		if (finder.getMinDate() != null) {
			filter = this.finderActivitiesRepository.filterByDateMin(finder.getMinDate());
			res.retainAll(filter);
		}

		finder.setActivities(res);

		FinderActivities finderRes = this.finderActivitiesRepository.save(finder);
		prisoner.setFinderActivities(finderRes);

		this.prisonerService.save(prisoner);

	}

	public List<Activity> getResults(FinderActivities finder) {
		Prisoner prisoner = this.prisonerService.loggedPrisoner();

		Assert.notNull(finder);
		Assert.isTrue(finder.getId() > 0);
		Assert.isTrue(prisoner.getFinderActivities().getId() == finder.getId());

		List<Activity> res = new ArrayList<>();
		List<Activity> activities = finder.getActivities();

		if (finder.getLastEdit() != null) {

			// Current Date
			Date currentDate = new Date();

			Calendar calendar1 = Calendar.getInstance();
			calendar1.setTime(currentDate);

			// LastEdit Finder
			Date lastEdit = finder.getLastEdit();

			Calendar calendar2 = Calendar.getInstance();
			calendar2.setTime(lastEdit);

			Integer time = this.configurationService.getConfiguration().getTimeFinderActivities();

			calendar2.add(Calendar.HOUR, time);

			if (calendar2.after(calendar1)) {
				// TODO Hay que cambiarlo
				Integer numFinderResult = this.configurationService.getConfiguration().getFinderResult();

				if (activities.size() > numFinderResult)
					for (int i = 0; i < numFinderResult; i++)
						res.add(activities.get(i));
				else
					res = activities;
			}
		}
		return res;
	}

	public FinderActivities reconstruct(FinderActivities finderForm, BindingResult binding) {
		FinderActivities res = new FinderActivities();

		FinderActivities finder = this.findOne(finderForm.getId());

		res.setId(finder.getId());
		res.setVersion(finder.getVersion());
		res.setActivities(finder.getActivities());

		Date date = new Date();
		res.setLastEdit(date);

		res.setKeyWord(finderForm.getKeyWord());
		res.setMaxDate(finderForm.getMaxDate());
		res.setMinDate(finderForm.getMinDate());

		this.validator.validate(res, binding);

		return res;
	}

}
