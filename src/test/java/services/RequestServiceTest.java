package services;

import java.util.ArrayList;
import java.util.List;

import javax.validation.ConstraintViolationException;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.Assert;

import domain.Activity;
import domain.ActivityStatus;
import domain.Prisoner;
import domain.Request;
import domain.SocialWorker;
import utilities.AbstractTest;

@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(locations = { "classpath:spring/junit.xml" })
@Transactional
public class RequestServiceTest extends AbstractTest {

	@Autowired
	private RequestService requestService;

	@Autowired
	private PrisonerService prisonerService;

	@Autowired
	private ActivityService activityService;

	@Autowired
	private SocialWorkerService socialWorkerService;

	/**
	 * SENTENCE COVERAGE: - RequestServiceTest = %
	 */

	/**
	 * R31. An actor who is authenticated as a Prisoner must be able to:
	 *
	 * 1. Manage his requests, which includes listing and showing them.
	 *
	 * Ratio of data coverage: 100% - Access as a Prisoner or not.
	 *
	 **/
	@Test
	public void driverListRequest() {

		Object testingData[][] = {

				/**
				 * POSITIVE TEST: Prisoner is listing his or her requests
				 **/
				{ "prisoner1", null },
				/**
				 * NEGATIVE TEST: Another user is trying to list his or her
				 * request
				 **/
				{ "socialWorker1", IllegalArgumentException.class } };

		for (int i = 0; i < testingData.length; i++)
			this.listRequestsTemplate((String) testingData[i][0], (Class<?>) testingData[i][1]);
	}

	private void listRequestsTemplate(String prisoner, Class<?> expected) {

		Class<?> caught = null;

		try {
			super.startTransaction();

			super.authenticate(prisoner);

			List<Request> requests = this.requestService.getLogguedPrisonerRequests();

			Assert.isTrue(this.prisonerService.loggedPrisoner().getRequests().equals(requests));

			super.unauthenticate();
		} catch (Throwable oops) {
			caught = oops.getClass();
		} finally {
			this.rollbackTransaction();
		}

		super.checkExceptions(expected, caught);

	}

	/**
	 * R39. An actor who is authenticated as a SocialWorker must be able to:
	 *
	 * 1. Manage his requests, which includes listing them.
	 *
	 * Ratio of data coverage: 100% - Access as a SocialWorker or not.
	 *
	 **/

	@Test
	public void driverListRequestSocialWorker() {

		Object testingData[][] = {

				/**
				 * POSITIVE TEST: SocialWorker is listing his or her requests
				 **/
				{ "socialWorker1", super.getEntityId("activity2"), null },
				/**
				 * NEGATIVE TEST: Another user is trying to list his or her
				 * request
				 **/
				{ "prisoner1", super.getEntityId("activity2"), IllegalArgumentException.class } };

		for (int i = 0; i < testingData.length; i++)
			this.listRequestsTemplateSocialWorker((String) testingData[i][0], (int) testingData[i][1],
					(Class<?>) testingData[i][2]);
	}

	private void listRequestsTemplateSocialWorker(String socialWorker, int activityId, Class<?> expected) {

		Class<?> caught = null;

		try {
			super.startTransaction();

			super.authenticate(socialWorker);

			List<Request> requests = this.requestService.getRequestsFromSocialWorker(activityId);

			Assert.isTrue(requests.size() == 2);

			super.unauthenticate();
		} catch (Throwable oops) {
			caught = oops.getClass();
		} finally {
			this.rollbackTransaction();
		}

		super.checkExceptions(expected, caught);

	}

	/**
	 * R31. An actor who is authenticated as a Prisoner must be able to:
	 *
	 * 1. Manage his requests, which includes creating them.
	 *
	 * Ratio of data coverage: 100%.
	 *
	 **/
	@Test
	public void driverCreateRequests() {

		Object testingData[][] = {
				/**
				 * POSITIVE TEST: Prisoner is creating a request with correct
				 * information
				 **/
				{ "prisoner1", ActivityStatus.PENDING, "motivation1", "", null },
				/**
				 * NEGATIVE TEST: Another user is trying to create a request
				 **/
				{ "socialWorker1", ActivityStatus.PENDING, "motivation1", "", IllegalArgumentException.class },
				/**
				 * NEGATIVE TEST: Prisoner is trying to create a request with
				 * the motivation in blank
				 **/
				{ "prisoner1", ActivityStatus.PENDING, "", "", ConstraintViolationException.class },
				/**
				 * NEGATIVE TEST: Prisoner is trying to create a request with
				 * the activity status as null
				 **/
				{ "prisoner1", null, "motivation1", "", ConstraintViolationException.class } };

		for (int i = 0; i < testingData.length; i++)
			this.createRequestsTemplate((String) testingData[i][0], (ActivityStatus) testingData[i][1],
					(String) testingData[i][2], (String) testingData[i][3], (Class<?>) testingData[i][4]);
	}

	private void createRequestsTemplate(String prisoner, ActivityStatus activityStatus, String motivation,
			String rejectReason, Class<?> expected) {

		Activity activity1 = this.activityService.findAll().get(0);

		Class<?> caught = null;

		try {
			super.startTransaction();

			super.authenticate(prisoner);
			Prisoner prisoner1 = this.prisonerService.loggedPrisoner();
			if (prisoner1 != null) {
				Integer number1 = prisoner1.getRequests().size();

				Request request = this.requestService.create();
				request.setMotivation(motivation);
				request.setRejectReason(rejectReason);
				request.setStatus(activityStatus);

				this.activityService.securityActivityForRequests(activity1);
				this.requestService.assignRequest(request, activity1.getId());

				Integer number2 = prisoner1.getRequests().size();

				Assert.isTrue(number1 + 1 == number2);
			} else
				throw new IllegalArgumentException();

			super.unauthenticate();
		} catch (Throwable oops) {
			caught = oops.getClass();
		} finally {
			this.rollbackTransaction();
		}

		super.checkExceptions(expected, caught);

	}

	/**
	 * R39. An actor who is authenticated as a SocialWorker must be able to:
	 *
	 * 1. Manage his requests, which includes approving them.
	 *
	 * Ratio of data coverage: 100% - Access as a SocialWorker or not.
	 *
	 **/

	@Test
	public void driverApproveRequestSocialWorker() {

		Object testingData[][] = {

				/**
				 * POSITIVE TEST: SocialWorker is approving his or her requests
				 **/
				{ "socialWorker1", super.getEntityId("request2"), super.getEntityId("activity2"), null },
				/**
				 * NEGATIVE TEST: Another user is trying to approving his or her
				 * request
				 **/
				{ "prisoner1", super.getEntityId("request2"), super.getEntityId("activity2"),
						IllegalArgumentException.class },
				/**
				 * NEGATIVE TEST: SocialWorker is trying to approve a accepted
				 * or rejected request
				 **/
				{ "socialWorker1", super.getEntityId("request5"), super.getEntityId("activity2"),
						IllegalArgumentException.class } };

		for (int i = 0; i < testingData.length; i++)
			this.approveRequestsTemplateSocialWorker((String) testingData[i][0], (int) testingData[i][1],
					(int) testingData[i][2], (Class<?>) testingData[i][3]);
	}

	private void approveRequestsTemplateSocialWorker(String socialWorker, int requestId, int activityId,
			Class<?> expected) {

		Class<?> caught = null;

		try {
			super.startTransaction();

			super.authenticate(socialWorker);

			this.requestService.approveRequest(requestId, activityId);

			List<Request> requests = this.requestService.getRequestsFromSocialWorker(activityId);

			List<Request> approved = new ArrayList<Request>();

			for (int i = 0; i < requests.size(); i++)
				if (requests.get(i).getStatus() == ActivityStatus.APPROVED)
					approved.add(requests.get(i));

			Assert.isTrue(approved.size() == 2);

			super.unauthenticate();
		} catch (Throwable oops) {
			caught = oops.getClass();
		} finally {
			this.rollbackTransaction();
		}

		super.checkExceptions(expected, caught);

	}

	/**
	 * R39. An actor who is authenticated as a SocialWorker must be able to:
	 *
	 * 1. Manage his requests, which includes rejecting them.
	 *
	 * Ratio of data coverage: 100% - Access as a SocialWorker or not.
	 *
	 **/

	@Test
	public void driverRejectRequestSocialWorker() {

		Object testingData[][] = {

				/**
				 * POSITIVE TEST: SocialWorker is rejecting his or her requests
				 **/
				{ "socialWorker1", super.getEntityId("request2"), super.getEntityId("activity2"), "rejectReason",
						null },
				/**
				 * NEGATIVE TEST: Another user is trying to rejecting his or her
				 * request
				 **/
				{ "prisoner1", super.getEntityId("request2"), super.getEntityId("activity2"), "rejectReason",
						IllegalArgumentException.class },
				/**
				 * NEGATIVE TEST: SocialWorker is trying to reject a accepted or
				 * rejected request
				 **/
				{ "socialWorker1", super.getEntityId("request5"), super.getEntityId("activity2"), "rejectReason",
						IllegalArgumentException.class },
				/**
				 * NEGATIVE TEST: SocialWorker is trying to reject a request
				 * with a blank reject request
				 **/
				{ "socialWorker1", super.getEntityId("request5"), super.getEntityId("activity2"), "",
						IllegalArgumentException.class } };

		for (int i = 0; i < testingData.length; i++)
			this.rejectRequestsTemplateSocialWorker((String) testingData[i][0], (int) testingData[i][1],
					(int) testingData[i][2], (String) testingData[i][3], (Class<?>) testingData[i][4]);
	}

	private void rejectRequestsTemplateSocialWorker(String socialWorker, int requestId, int activityId,
			String rejectReason, Class<?> expected) {

		Class<?> caught = null;

		try {
			super.startTransaction();

			super.authenticate(socialWorker);

			Request request = this.requestService.findeOne(requestId);

			if (request.getStatus() == ActivityStatus.PENDING) {
				request.setRejectReason(rejectReason);

				Request rec = this.requestService.reconstructRejectRequest(request, null);

				Request recSaved = this.requestService.save(rec);

				this.requestService.rejectRequest(recSaved, activityId);

				List<Request> requests = this.requestService.getRequestsFromSocialWorker(activityId);

				List<Request> rejected = new ArrayList<Request>();

				for (int i = 0; i < requests.size(); i++)
					if (requests.get(i).getStatus() == ActivityStatus.REJECTED)
						rejected.add(requests.get(i));

				Assert.isTrue(rejected.size() == 1);
			} else
				throw new IllegalArgumentException();

			super.unauthenticate();
		} catch (Throwable oops) {
			caught = oops.getClass();
		} finally {
			this.rollbackTransaction();
		}

		super.checkExceptions(expected, caught);

	}

	/**
	 * R31. An actor who is authenticated as a Prisoner must be able to:
	 *
	 * 1. Manage his requests, which includes deleting them. A request can only
	 * be deleted in pending status
	 *
	 * Ratio of data coverage: 100% - Access as a Prisoner or not.
	 *
	 **/
	@Test
	public void driverDeleteRequests() {
		Object testingData[][] = {
				/**
				 * POSITIVE TEST: Prisoner is deleting a request
				 **/
				{ "prisoner1", super.getEntityId("request2"), null },
				/**
				 * NEGATIVE TEST: Another user is trying to delete a request
				 **/
				{ "socialWorker1", super.getEntityId("request2"), IllegalArgumentException.class },
				/**
				 * NEGATIVE TEST: Prisoner is trying to delete a request that
				 * isn't in pending status
				 **/
				{ "prisoner1", super.getEntityId("request1"), IllegalArgumentException.class },
				/**
				 * NEGATIVE TEST: Prisoner is trying to delete a request of
				 * another prisoner
				 **/
				{ "prisoner1", super.getEntityId("request4"), IllegalArgumentException.class } };

		for (int i = 0; i < testingData.length; i++)
			this.deleteRequestsTemplate((String) testingData[i][0], (Integer) testingData[i][1],
					(Class<?>) testingData[i][2]);
	}

	private void deleteRequestsTemplate(String prisoner, Integer requestId, Class<?> expected) {

		Request request = this.requestService.findeOne(requestId);

		Class<?> caught = null;

		try {
			super.startTransaction();

			super.authenticate(prisoner);

			Prisoner p = this.prisonerService.loggedPrisoner();
			if (p != null) {
				Integer number1 = p.getRequests().size();

				this.requestService.deleteRequestFromPrisoner(request);
				p = this.prisonerService.loggedPrisoner();
				Integer number2 = p.getRequests().size();

				Assert.isTrue(number1 == number2 + 1);

				super.flushTransaction();
			} else
				throw new IllegalArgumentException();

			super.unauthenticate();
		} catch (Throwable oops) {
			caught = oops.getClass();
		} finally {
			this.rollbackTransaction();
		}

		super.checkExceptions(expected, caught);
	}

	/**
	 * R39. An actor who is authenticated as a SocialWorker must be able to:
	 *
	 * 1. Manage his requests, which includes deleting them. A request can only
	 * be deleted in pending status
	 *
	 * Ratio of data coverage: 100% - Access as a SocialWorker or not.
	 *
	 **/
	@Test
	public void driverDeleteRequestsSocialWorker() {
		Object testingData[][] = {
				/**
				 * POSITIVE TEST: SocialWorker is deleting a request
				 **/
				{ "socialWorker1", super.getEntityId("request2"), super.getEntityId("activity2"), null },
				/**
				 * NEGATIVE TEST: Another user is trying to delete a request
				 **/
				{ "prisoner1", super.getEntityId("request2"), super.getEntityId("activity2"),
						IllegalArgumentException.class },
				/**
				 * NEGATIVE TEST: SocialWorker is trying to delete a request
				 * that isn't in pending status
				 **/
				{ "socialWorker1", super.getEntityId("request1"), super.getEntityId("activity1"),
						IllegalArgumentException.class } };

		for (int i = 0; i < testingData.length; i++)
			this.deleteRequestsTemplateSW((String) testingData[i][0], (Integer) testingData[i][1],
					(Integer) testingData[i][2], (Class<?>) testingData[i][3]);
	}

	private void deleteRequestsTemplateSW(String socialWorker, Integer requestId, Integer activityId,
			Class<?> expected) {

		Request request = this.requestService.findeOne(requestId);

		Class<?> caught = null;

		try {
			super.startTransaction();

			super.authenticate(socialWorker);

			SocialWorker p = this.socialWorkerService.loggedSocialWorker();
			if (p != null) {
				Integer number1 = this.requestService.getRequestsFromSocialWorker(activityId).size();

				this.requestService.deleteRequestFromSocialWorker(request);
				p = this.socialWorkerService.loggedSocialWorker();
				Integer number2 = this.requestService.getRequestsFromSocialWorker(activityId).size();

				Assert.isTrue(number1 == number2 + 1);

				super.flushTransaction();
			} else
				throw new IllegalArgumentException();

			super.unauthenticate();
		} catch (Throwable oops) {
			caught = oops.getClass();
		} finally {
			this.rollbackTransaction();
		}

		super.checkExceptions(expected, caught);
	}

}
