package services;

import java.util.Date;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.transaction.annotation.Transactional;

import domain.Activity;
import domain.SocialWorker;
import utilities.AbstractTest;

@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(locations = { "classpath:spring/junit.xml" })
@Transactional
public class ActivityServiceTest extends AbstractTest {

	@Autowired
	private ActivityService activityService;

	@Autowired
	private SocialWorkerService socialWorkerService;

	/**
	 * We are going to test the requirement 39.
	 *
	 * An actor who is authenticated as a Social Worker must be able to:
	 *
	 * 1. Create, edit, delete and List his Activities and navigate to its
	 * participators
	 *
	 */

	@SuppressWarnings("deprecation")
	@Test
	public void testCreateActivity() {

		Date currentDate = new Date();

		Date dateAfter = new Date();
		dateAfter.setDate(currentDate.getDate() + 100);

		Date dateBefore = new Date();
		dateBefore.setDate(currentDate.getDate() - 100);

		Object testingData[][] = {
				// Positive test: A Social Worker is creating an Activity
				{ "socialWorker1", "test", "test", dateAfter, 4, 50, true, null },
				// Positive test: A Social Worker is creating an Activity in
				// draft mode
				{ "socialWorker1", "test", "test", dateAfter, 4, 50, false, null },
				// Negative test: A Social Worker trying to create an activity
				// with no title
				{ "socialWorker1", "", "test", dateAfter, 4, 50, true, NullPointerException.class },
				// Negative test: A Social Worker trying to create an activity
				// with no description
				{ "socialWorker1", "test", "", dateAfter, 4, 50, true, NullPointerException.class },
				// Negative test: A Social Worker trying to create an activity
				// with a not valid date
				{ "socialWorker1", "test", "test", dateBefore, 4, 50, true, NullPointerException.class },
				// Negative test: A Social Worker trying to create an activity
				// with 0 participants
				{ "socialWorker1", "test", "test", dateAfter, 0, 50, true, NullPointerException.class },
				// Negative test: A Social Worker trying to create an activity
				// with no reward points
				{ "socialWorker1", "test", "test", dateAfter, 4, 0, true, NullPointerException.class },
				// Negative test: A Warden is trying to create an Activity
				{ "warden1", "test", "test", dateAfter, 4, 50, true, IllegalArgumentException.class },
				// Negative test: A Prisoner is trying to create an Activity
				{ "prisoner1", "test", "test", dateAfter, 4, 50, true, IllegalArgumentException.class },
				// Negative test: A Guard is trying to create an Activity
				{ "guard1", "test", "test", dateAfter, 4, 50, true, IllegalArgumentException.class },
				// Negative test: A Sales man is trying to create an Activity
				{ "salesman1", "test", "test", dateAfter, 4, 50, true, IllegalArgumentException.class },
				// Negative test: A Visitor is trying to create an Activity
				{ "visitor1", "test", "test", dateAfter, 4, 50, true, IllegalArgumentException.class },

		};

		/**
		 * Data Coverage: 100%
		 *
		 */

		for (int i = 0; i < testingData.length; i++)
			this.templateCreateActivity((String) testingData[i][0], (String) testingData[i][1],
					(String) testingData[i][2], (Date) testingData[i][3], (Integer) testingData[i][4],
					(Integer) testingData[i][5], (Boolean) testingData[i][6], (Class<?>) testingData[i][7]);

	}

	@Test
	public void testEditActivity() {

		Object testingData[][] = {
				// Positive test: A Social Worker is editing his or her activity
				// title
				{ "socialWorker1", "test", "activity1", null },
				// Negative test: A Social Worker is trying to edit an activity
				// that he or she does't own
				{ "socialWorker2", "test", "activity1", IllegalArgumentException.class },
				// Negative test: A Warden is trying to edit an activity
				{ "warden1", "test", "activity1", IllegalArgumentException.class },
				// Negative test: A Guard is trying to edit an activity
				{ "guard1", "test", "activity1", IllegalArgumentException.class },
				// Negative test: A Visitor is trying to edit an activity
				{ "visito1", "test", "activity1", IllegalArgumentException.class },
				// Negative test: A Prisoner is trying to edit an activity
				{ "prisoner1", "test", "activity1", IllegalArgumentException.class },
				// Negative test: A Sales man is trying to edit an activity
				{ "salesmane1", "test", "activity1", IllegalArgumentException.class } };

		for (int i = 0; i < testingData.length; i++)
			this.templateEditActivity((String) testingData[i][0], (String) testingData[i][1],
					(String) testingData[i][2], (Class<?>) testingData[i][3]);

	}

	@Test
	public void testDeleteActivity() {

		Object testingData[][] = {
				// Positive test: A Social Worker is deleting his or her
				// activity
				{ "socialWorker1", "activity1", null },
				// Negative test: A Social Worker is trying to delete an
				// activity
				// that he or she does't own
				{ "socialWorker2", "activity1", IllegalArgumentException.class },
				// Negative test: A Warden is trying to delete an activity
				{ "warden1", "activity1", IllegalArgumentException.class },
				// Negative test: A Guard is trying to delete an activity
				{ "guard1", "activity1", IllegalArgumentException.class },
				// Negative test: A Visitor is trying to delete an activity
				{ "visito1", "activity1", IllegalArgumentException.class },
				// Negative test: A Prisoner is trying to delete an activity
				{ "prisoner1", "activity1", IllegalArgumentException.class },
				// Negative test: A Sales man is trying to delete an activity
				{ "salesmane1", "activity1", IllegalArgumentException.class } };

		for (int i = 0; i < testingData.length; i++)
			this.templateDeleteActivity((String) testingData[i][0], (String) testingData[i][1],
					(Class<?>) testingData[i][2]);

	}

	protected void templateDeleteActivity(String username, String activityName, Class<?> expected) {
		Class<?> caught = null;

		try {
			this.startTransaction();
			super.authenticate(username);

			SocialWorker sw = this.socialWorkerService.loggedSocialWorker();
			Activity activity = this.activityService.findOne(this.getEntityId(activityName));

			this.activityService.deleteActivity(activity, sw);

			super.authenticate(null);

		} catch (Throwable oops) {
			caught = oops.getClass();
		} finally {
			this.rollbackTransaction();
		}

		super.checkExceptions(expected, caught);
	}

	protected void templateEditActivity(String username, String title, String activityName, Class<?> expected) {
		Class<?> caught = null;

		try {
			this.startTransaction();
			super.authenticate(username);

			Activity activity = this.activityService.findOne(this.getEntityId(activityName));

			activity.setTitle(title);

			activity = this.activityService.reconstruct(activity, null);

			this.activityService.saveActivity(activity);

			super.authenticate(null);

		} catch (Throwable oops) {
			caught = oops.getClass();
		} finally {
			this.rollbackTransaction();
		}

		super.checkExceptions(expected, caught);
	}

	protected void templateCreateActivity(String username, String title, String description, Date date,
			Integer assistants, Integer rewardPoints, Boolean mode, Class<?> expected) {

		Class<?> caught = null;

		try {
			this.startTransaction();
			super.authenticate(username);
			Activity activity = this.activityService.createActivity();

			activity.setTitle(title);
			activity.setDescription(description);
			activity.setRealizationDate(date);
			activity.setRewardPoints(rewardPoints);
			activity.setIsFinalMode(mode);
			activity.setMaxAssistant(assistants);

			activity = this.activityService.reconstruct(activity, null);

			this.activityService.saveActivity(activity);

			super.authenticate(null);

		} catch (Throwable oops) {
			caught = oops.getClass();
		} finally {
			this.rollbackTransaction();
		}

		super.checkExceptions(expected, caught);

	}

}
