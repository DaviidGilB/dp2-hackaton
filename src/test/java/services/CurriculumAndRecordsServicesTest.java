
package services;

import java.util.Calendar;
import java.util.Date;

import javax.validation.ConstraintViolationException;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.Assert;

import utilities.AbstractTest;
import domain.Curriculum;
import domain.EducationRecord;
import domain.MiscellaneousRecord;
import domain.PersonalRecord;
import domain.ProfessionalRecord;

@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(locations = {
	"classpath:spring/junit.xml"
})
@Transactional
public class CurriculumAndRecordsServicesTest extends AbstractTest {

	@Autowired
	private CurriculumService			curriculumService;

	@Autowired
	private SocialWorkerService			socialWorkerService;

	@Autowired
	private PersonalRecordService		personalRecordService;

	@Autowired
	private EducationRecordService		educationRecordService;

	@Autowired
	private ProfessionalRecordService	professionalRecordService;

	@Autowired
	private MiscellaneousRecordService	miscellaneousRecordService;


	/**
	 * SENTENCE COVERAGE: - CurriculumService = 92.3% - EducationRecordService =
	 * 58.3% - ProfessionalRecordService = 59% - MiscellaneousRecordService =
	 * 68.6% - PersonalRecordService = 81.8%
	 */

	/**
	 * R39. An actor who is authenticated as a SocialWorker must be able to:
	 * 
	 * 3. Manage his or her curriculum, which includes showing it.
	 * 
	 * Ratio of data coverage: 100% - Access as a SocialWorker or not. -
	 * SocialWorker has a curriculum or not.
	 * 
	 **/
	@Test
	public void driverShowCurriculum() {

		Object testingData[][] = {

			/**
			 * POSITIVE TEST: SocialWorker is showing his or her curriculum
			 **/
			{
				"socialWorker1", null
			},
			/**
			 * NEGATIVE TEST: Another user is trying to show a curriculum
			 **/
			{
				"salesMan1", IllegalArgumentException.class
			},
			/**
			 * NEGATIVE TEST: SocialWorker without curriculum is trying to
			 * edit his or her curriculum
			 **/
			{
				"socialWorker3", IllegalArgumentException.class
			}
		};

		for (int i = 0; i < testingData.length; i++) {
			this.showCurriculumTemplate((String) testingData[i][0], (Class<?>) testingData[i][1]);
		}

	}

	private void showCurriculumTemplate(String socialWorker, Class<?> expected) {

		Class<?> caught = null;

		try {
			super.startTransaction();

			super.authenticate(socialWorker);

			Curriculum curriculum = this.curriculumService.getCurriculumOfLoggedSocialWorker();

			Assert.isTrue(this.socialWorkerService.getSocialWorkerByUsername(socialWorker).getCurriculum().getId() == curriculum.getId());

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
	 * 3. Manage his or her curriculum, which includes to create a curriculum
	 * with a personal record.
	 * 
	 * Ratio of data coverage: 5/7 = 71.42% - Access as a SocialWorker or not.
	 * -SocialWorker has a curriculum or not - 4 attributes with restrictions
	 * 
	 **/
	@Test
	public void driverCreateCurriculumPersonalRecord() {

		Object testingData[][] = {

			/**
			 * POSITIVE TEST: SocialWorker is creating a curriculum with
			 * personal record with correct information
			 **/
			{
				"socialWorker3", "Name", "http://www.image.com", "email@email.com", "+34609235520", "http://www.linkedin.com", null
			},
			/**
			 * NEGATIVE TEST: SocialWorker is creating a curriculum but he
			 * or she has a curriculum
			 **/
			{
				"socialWorker1", "Name", "http://www.image.com", "email@email.com", "+34609235520", "http://www.linkedin.com", IllegalArgumentException.class
			},
			/**
			 * NEGATIVE TEST: Another user is trying to create a curriculum
			 **/
			{
				"salesMan1", "Name", "http://www.image.com", "email@email.com", "+34609235520", "http://www.linkedin.com", IllegalArgumentException.class
			},
			/**
			 * NEGATIVE TEST: SocialWorker is creating a curriculum with
			 * personal record with an incorrect emails
			 **/
			{
				"socialWorker3", "Name", "http://www.image.com", "email", "+34609235520", "http://www.linkedin.com", ConstraintViolationException.class
			},
			/**
			 * NEGATIVE TEST: SocialWorker is creating a curriculum with
			 * personal record with a blank name
			 **/
			{
				"socialWorker3", "", "http://www.image.com", "email@gmail.com", "+34609235520", "http://www.linkedin.com", ConstraintViolationException.class
			}
		};

		for (int i = 0; i < testingData.length; i++) {
			this.createPersonalDataTemplate((String) testingData[i][0], (String) testingData[i][1], (String) testingData[i][2], (String) testingData[i][3], (String) testingData[i][4], (String) testingData[i][5], (Class<?>) testingData[i][6]);
		}

	}

	private void createPersonalDataTemplate(String socialWorker, String name, String image, String email, String phone, String linkedin, Class<?> expected) {

		PersonalRecord personalRecord = new PersonalRecord();
		personalRecord.setFullName(name);
		personalRecord.setPhoto(image);
		personalRecord.setEmail(email);
		personalRecord.setPhoneNumber(phone);
		personalRecord.setUrlLinkedInProfile(linkedin);

		Class<?> caught = null;

		try {
			super.startTransaction();

			super.authenticate(socialWorker);

			this.socialWorkerService.addCurriculum(personalRecord);

			this.flushTransaction();

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
	 * 3. Manage his or her curriculum, which includes to update a curriculum
	 * with a personal record.
	 * 
	 * Ratio of data coverage: 5/7 = 71.42% - Access as a SocialWorker or not. -
	 * SocialWorker owns the personal record or not - 4 attributes with
	 * restrictions
	 * 
	 **/
	@Test
	public void driverUpdateCurriculumPersonalRecord() {

		Object testingData[][] = {

			/**
			 * POSITIVE TEST: SocialWorker is updating his or her curriculum
			 * with the personal record with correct information
			 **/
			{
				"socialWorker1", super.getEntityId("personalRecord1"), "Name", "http://www.image.com", "email@email.com", "+34609235520", "http://www.linkedin.com", null
			},
			/**
			 * NEGATIVE TEST: SocialWorker is trying to update a curriculum
			 * and personal record that does not belong to his or her
			 **/
			{
				"socialWorker1", super.getEntityId("personalRecord2"), "Name", "http://www.image.com", "email@email.com", "+34609235520", "http://www.linkedin.com", IllegalArgumentException.class
			},
			/**
			 * NEGATIVE TEST: Another user is trying to update a curriculum
			 **/
			{
				"salesMan1", super.getEntityId("personalRecord1"), "Name", "http://www.image.com", "email@email.com", "+34609235520", "http://www.linkedin.com", IllegalArgumentException.class
			},
			/**
			 * NEGATIVE TEST: SocialWorker is trying to update his or her
			 * curriculum with personal record with an incorrect emails
			 **/
			{
				"socialWorker1", super.getEntityId("personalRecord1"), "Name", "http://www.image.com", "email", "+34609235520", "http://www.linkedin.com", ConstraintViolationException.class
			},
			/**
			 * NEGATIVE TEST: SocialWorker is trying to update his or her
			 * curriculum with personal record with a blank name
			 **/
			{
				"socialWorker1", super.getEntityId("personalRecord1"), "", "http://www.image.com", "email@gmail.com", "+34609235520", "http://www.linkedin.com", ConstraintViolationException.class
			}
		};

		for (int i = 0; i < testingData.length; i++) {
			this.createPersonalDataTemplate((String) testingData[i][0], (Integer) testingData[i][1], (String) testingData[i][2], (String) testingData[i][3], (String) testingData[i][4], (String) testingData[i][5], (String) testingData[i][6],
				(Class<?>) testingData[i][7]);
		}

	}

	private void createPersonalDataTemplate(String socialWorker, Integer personalRecordId, String name, String image, String email, String phone, String linkedin, Class<?> expected) {

		PersonalRecord personalRecord = this.personalRecordService.findOne(personalRecordId);
		personalRecord.setFullName(name);
		personalRecord.setPhoto(image);
		personalRecord.setEmail(email);
		personalRecord.setPhoneNumber(phone);
		personalRecord.setUrlLinkedInProfile(linkedin);

		Class<?> caught = null;

		try {
			super.startTransaction();

			super.authenticate(socialWorker);

			this.personalRecordService.getPersonalRecordAsSocialWorker(personalRecordId.toString());
			this.socialWorkerService.updateCurriculum(personalRecord);

			this.flushTransaction();

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
	 * 3. Manage his or her curriculum, which includes to create an education
	 * record
	 * 
	 * Ratio of data coverage: 5/8 = 62.5% - Access as a SocialWorker or not. -
	 * SocialWorker has a curriculum or not - 5 attributes with restrictions
	 * 
	 **/
	@Test
	public void driverCreateEducationRecord() {

		Calendar c1 = Calendar.getInstance();
		c1.add(Calendar.YEAR, -4);
		Date date1 = c1.getTime();

		Calendar c2 = Calendar.getInstance();
		c2.add(Calendar.YEAR, -3);
		Date date2 = c2.getTime();

		Calendar c3 = Calendar.getInstance();
		c3.add(Calendar.YEAR, 1);
		Date date3 = c3.getTime();

		Object testingData[][] = {

			/**
			 * POSITIVE TEST: SocialWorker is creating an education record
			 * with correct information
			 **/
			{
				"socialWorker1", "Title", "Institution", "http://www.attachment.com", date1, date2, null
			},
			/**
			 * NEGATIVE TEST: SocialWorker is creating an education record
			 * but he or she has not a curriculum
			 **/
			{
				"socialWorker3", "Title", "Institution", "http://www.attachment.com", date1, date2, IllegalArgumentException.class
			},
			/**
			 * NEGATIVE TEST: Another user is trying to create an education
			 * record
			 **/
			{
				"salesMan1", "Title", "Institution", "http://www.attachment.com", date1, date2, IllegalArgumentException.class
			},
			/**
			 * NEGATIVE TEST: SocialWorker is trying to create an education
			 * record record with incorrect dates
			 **/
			{
				"socialWorker1", "Title", "Institution", "http://www.attachment.com", date3, date2, ConstraintViolationException.class
			},
			/**
			 * NEGATIVE TEST: SocialWorker is trying to create an education
			 * record with a blank name title
			 **/
			{
				"socialWorker1", "", "Institution", "http://www.attachment.com", date1, date2, ConstraintViolationException.class
			}
		};

		for (int i = 0; i < testingData.length; i++) {
			this.createEducationRecordTemplate((String) testingData[i][0], (String) testingData[i][1], (String) testingData[i][2], (String) testingData[i][3], (Date) testingData[i][4], (Date) testingData[i][5], (Class<?>) testingData[i][6]);
		}

	}

	private void createEducationRecordTemplate(String socialWorker, String title, String institution, String attachment, Date startDate, Date endDate, Class<?> expected) {

		EducationRecord educationRecord = new EducationRecord();
		educationRecord.setTitle(title);
		educationRecord.setInstitution(institution);
		educationRecord.setLink(attachment);
		educationRecord.setStartDateStudy(startDate);
		educationRecord.setEndDateStudy(endDate);

		Class<?> caught = null;

		try {
			super.startTransaction();

			super.authenticate(socialWorker);

			this.curriculumService.addEducationRecord(educationRecord);

			this.flushTransaction();

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
	 * 3. Manage his or her curriculum, which includes to update an education
	 * record
	 * 
	 * Ratio of data coverage: 6/9 = 66.67% - Access as a SocialWorker or not. -
	 * SocialWorker has a curriculum or not - SocialWorker has the education
	 * record or not - 5 attributes with restrictions
	 * 
	 **/
	@Test
	public void driverUpdateEducationRecord() {

		Calendar c1 = Calendar.getInstance();
		c1.add(Calendar.YEAR, -4);
		Date date1 = c1.getTime();

		Calendar c2 = Calendar.getInstance();
		c2.add(Calendar.YEAR, -3);
		Date date2 = c2.getTime();

		Calendar c3 = Calendar.getInstance();
		c3.add(Calendar.YEAR, 1);
		Date date3 = c3.getTime();

		Object testingData[][] = {

			/**
			 * POSITIVE TEST: SocialWorker is updating an education record
			 * with correct information
			 **/
			{
				"socialWorker1", super.getEntityId("educationRecord1"), "Title", "Institution", "http://www.attachment.com", date1, date2, null
			},
			/**
			 * NEGATIVE TEST: SocialWorker is trying to update an education
			 * record but he or she has not a curriculum
			 **/
			{
				"socialWorker3", super.getEntityId("educationRecord1"), "Title", "Institution", "http://www.attachment.com", date1, date2, NullPointerException.class
			},
			/**
			 * NEGATIVE TEST: SocialWorker is trying to update an education
			 * record that does not belong to him or her
			 **/
			{
				"socialWorker2", super.getEntityId("educationRecord1"), "Title", "Institution", "http://www.attachment.com", date1, date2, IllegalArgumentException.class
			},
			/**
			 * NEGATIVE TEST: Another user is trying to update an education
			 * record
			 **/
			{
				"salesMan1", super.getEntityId("educationRecord1"), "Title", "Institution", "http://www.attachment.com", date1, date2, IllegalArgumentException.class
			},
			/**
			 * NEGATIVE TEST: SocialWorker is trying to update an education
			 * record record with incorrect dates
			 **/
			{
				"socialWorker1", super.getEntityId("educationRecord1"), "Title", "Institution", "http://www.attachment.com", date3, date2, ConstraintViolationException.class
			},
			/**
			 * NEGATIVE TEST: SocialWorker is trying to update an education
			 * record with a blank name title
			 **/
			{
				"socialWorker1", super.getEntityId("educationRecord1"), "", "Institution", "http://www.attachment.com", date1, date2, ConstraintViolationException.class
			}
		};

		for (int i = 0; i < testingData.length; i++) {
			this.updateEducationRecordTemplate((String) testingData[i][0], (Integer) testingData[i][1], (String) testingData[i][2], (String) testingData[i][3], (String) testingData[i][4], (Date) testingData[i][5], (Date) testingData[i][6],
				(Class<?>) testingData[i][7]);
		}

	}

	private void updateEducationRecordTemplate(String socialWorker, Integer educationRecordId, String title, String institution, String attachment, Date startDate, Date endDate, Class<?> expected) {

		EducationRecord educationRecord = this.educationRecordService.findOne(educationRecordId);
		educationRecord.setTitle(title);
		educationRecord.setInstitution(institution);
		educationRecord.setLink(attachment);
		educationRecord.setStartDateStudy(startDate);
		educationRecord.setEndDateStudy(endDate);

		Class<?> caught = null;

		try {
			super.startTransaction();

			super.authenticate(socialWorker);

			this.educationRecordService.getEducationRecordOfLoggedSocialWorker(educationRecordId.toString());
			this.curriculumService.updateEducationRecord(educationRecord);

			this.flushTransaction();

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
	 * 3. Manage his or her curriculum, which includes to delete an education
	 * record
	 * 
	 * Ratio of data coverage: 100% - Access as a SocialWorker or not. -
	 * SocialWorker has a curriculum or not - SocialWorker has the education
	 * record or not
	 * 
	 **/
	@Test
	public void driverDeleteEducationRecord() {

		Object testingData[][] = {

			/**
			 * POSITIVE TEST: SocialWorker is deleting an education record
			 **/
			{
				"socialWorker1", super.getEntityId("educationRecord1"), null
			},
			/**
			 * NEGATIVE TEST: SocialWorker is trying to update an education
			 * record but he or she has not a curriculum
			 **/
			{
				"socialWorker3", super.getEntityId("educationRecord1"), NullPointerException.class
			},
			/**
			 * NEGATIVE TEST: SocialWorker is trying to delete an education
			 * record that does not belong to him or her
			 **/
			{
				"socialWorker2", super.getEntityId("educationRecord1"), IllegalArgumentException.class
			},
			/**
			 * NEGATIVE TEST: Another user is trying to update an education
			 * record
			 **/
			{
				"salesMan1", super.getEntityId("educationRecord1"), IllegalArgumentException.class
			}
		};

		for (int i = 0; i < testingData.length; i++) {
			this.deleteEducationRecordTemplate((String) testingData[i][0], (Integer) testingData[i][1], (Class<?>) testingData[i][2]);
		}

	}

	private void deleteEducationRecordTemplate(String socialWorker, Integer educationRecordId, Class<?> expected) {

		EducationRecord educationRecord = this.educationRecordService.findOne(educationRecordId);

		Class<?> caught = null;

		try {
			super.startTransaction();

			super.authenticate(socialWorker);

			this.curriculumService.deleteEducationRecord(educationRecord);

			this.flushTransaction();

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
	 * 3. Manage his or her curriculum, which includes to create a professional
	 * record
	 * 
	 * Ratio of data coverage: 5/8 = 62.5% - Access as a SocialWorker or not. -
	 * SocialWorker has a curriculum or not - 5 attributes with restrictions
	 * 
	 **/
	@Test
	public void driverCreateProfessionalRecord() {

		Calendar c1 = Calendar.getInstance();
		c1.add(Calendar.YEAR, -4);
		Date date1 = c1.getTime();

		Calendar c2 = Calendar.getInstance();
		c2.add(Calendar.YEAR, -3);
		Date date2 = c2.getTime();

		Calendar c3 = Calendar.getInstance();
		c3.add(Calendar.YEAR, 1);
		Date date3 = c3.getTime();

		Object testingData[][] = {

			/**
			 * POSITIVE TEST: SocialWorker is creating a professional record
			 * with correct information
			 **/
			{
				"socialWorker1", "Name", "Role", "http://www.attachment.com", date1, date2, null
			},
			/**
			 * NEGATIVE TEST: SocialWorker is creating a professional record
			 * but he or she has not a curriculum
			 **/
			{
				"socialWorker3", "Name", "Role", "http://www.attachment.com", date1, date2, IllegalArgumentException.class
			},
			/**
			 * NEGATIVE TEST: Another user is trying to create a
			 * professional record
			 **/
			{
				"salesMan1", "Name", "Role", "http://www.attachment.com", date1, date2, IllegalArgumentException.class
			},
			/**
			 * NEGATIVE TEST: SocialWorker is trying to create a
			 * professional record with incorrect dates
			 **/
			{
				"socialWorker1", "Name", "Role", "http://www.attachment.com", date3, date2, ConstraintViolationException.class
			},
			/**
			 * NEGATIVE TEST: SocialWorker is trying to create a
			 * professional record with a blank name
			 **/
			{
				"socialWorker1", "", "Role", "http://www.attachment.com", date1, date2, ConstraintViolationException.class
			}
		};

		for (int i = 0; i < testingData.length; i++) {
			this.createProfessionalRecordTemplate((String) testingData[i][0], (String) testingData[i][1], (String) testingData[i][2], (String) testingData[i][3], (Date) testingData[i][4], (Date) testingData[i][5], (Class<?>) testingData[i][6]);
		}

	}

	private void createProfessionalRecordTemplate(String socialWorker, String name, String role, String attachment, Date startDate, Date endDate, Class<?> expected) {

		ProfessionalRecord professionalRecord = new ProfessionalRecord();
		professionalRecord.setNameCompany(name);
		professionalRecord.setRole(role);
		professionalRecord.setLinkAttachment(attachment);
		professionalRecord.setStartDate(startDate);
		professionalRecord.setEndDate(endDate);

		Class<?> caught = null;

		try {
			super.startTransaction();

			super.authenticate(socialWorker);

			this.curriculumService.addProfessionalRecord(professionalRecord);

			this.flushTransaction();

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
	 * 3. Manage his or her curriculum, which includes to update a professional
	 * record
	 * 
	 * Ratio of data coverage: 6/9 = 66.67% - Access as a SocialWorker or not. -
	 * SocialWorker has a curriculum or not - SocialWorker has the professional
	 * record or not - 5 attributes with restrictions
	 * 
	 **/
	@Test
	public void driverUpdateProfessionalRecord() {

		Calendar c1 = Calendar.getInstance();
		c1.add(Calendar.YEAR, -4);
		Date date1 = c1.getTime();

		Calendar c2 = Calendar.getInstance();
		c2.add(Calendar.YEAR, -3);
		Date date2 = c2.getTime();

		Calendar c3 = Calendar.getInstance();
		c3.add(Calendar.YEAR, 1);
		Date date3 = c3.getTime();

		Object testingData[][] = {

			/**
			 * POSITIVE TEST: SocialWorker is updating a professional record
			 * with correct information
			 **/
			{
				"socialWorker1", super.getEntityId("professionalRecord1"), "Name", "Role", "http://www.attachment.com", date1, date2, null
			},
			/**
			 * NEGATIVE TEST: SocialWorker is trying to update a
			 * professional record but he or she has not a curriculum
			 **/
			{
				"socialWorker3", super.getEntityId("professionalRecord1"), "Name", "Role", "http://www.attachment.com", date1, date2, NullPointerException.class
			},
			/**
			 * NEGATIVE TEST: SocialWorker is trying to update a
			 * professional record that does not belong to him or her
			 **/
			{
				"socialWorker2", super.getEntityId("professionalRecord1"), "Name", "Role", "http://www.attachment.com", date1, date2, IllegalArgumentException.class
			},
			/**
			 * NEGATIVE TEST: Another user is trying to update a
			 * professional record
			 **/
			{
				"salesMan1", super.getEntityId("professionalRecord1"), "Name", "Role", "http://www.attachment.com", date1, date2, IllegalArgumentException.class
			},
			/**
			 * NEGATIVE TEST: SocialWorker is trying to update a
			 * professional record with incorrect dates
			 **/
			{
				"socialWorker1", super.getEntityId("professionalRecord1"), "Name", "Role", "http://www.attachment.com", date3, date2, ConstraintViolationException.class
			},
			/**
			 * NEGATIVE TEST: SocialWorker is trying to update a
			 * professional record with a blank name title
			 **/
			{
				"socialWorker1", super.getEntityId("professionalRecord1"), "", "Role", "http://www.attachment.com", date1, date2, ConstraintViolationException.class
			}
		};

		for (int i = 0; i < testingData.length; i++) {
			this.updateProfessionalRecordTemplate((String) testingData[i][0], (Integer) testingData[i][1], (String) testingData[i][2], (String) testingData[i][3], (String) testingData[i][4], (Date) testingData[i][5], (Date) testingData[i][6],
				(Class<?>) testingData[i][7]);
		}

	}

	private void updateProfessionalRecordTemplate(String socialWorker, Integer professionalRecordId, String name, String role, String attachment, Date startDate, Date endDate, Class<?> expected) {

		ProfessionalRecord professionalRecord = this.professionalRecordService.findOne(professionalRecordId);
		professionalRecord.setNameCompany(name);
		professionalRecord.setRole(role);
		professionalRecord.setLinkAttachment(attachment);
		professionalRecord.setStartDate(startDate);
		professionalRecord.setEndDate(endDate);

		Class<?> caught = null;

		try {
			super.startTransaction();

			super.authenticate(socialWorker);

			this.professionalRecordService.getProfessionalRecordOfLoggedSocialWorker(professionalRecordId.toString());
			this.curriculumService.updateProfessionalRecord(professionalRecord);

			this.flushTransaction();

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
	 * 3. Manage his or her curriculum, which includes to delete a professional
	 * record
	 * 
	 * Ratio of data coverage: 100% - Access as a SocialWorker or not. -
	 * SocialWorker has a curriculum or not - SocialWorker has the professional
	 * record or not
	 * 
	 **/
	@Test
	public void driverDeleteProfessionalRecord() {

		Object testingData[][] = {

			/**
			 * POSITIVE TEST: SocialWorker is deleting a professional record
			 **/
			{
				"socialWorker1", super.getEntityId("professionalRecord1"), null
			},
			/**
			 * NEGATIVE TEST: SocialWorker is trying to update a
			 * professional record but he or she has not a curriculum
			 **/
			{
				"socialWorker3", super.getEntityId("professionalRecord1"), NullPointerException.class
			},
			/**
			 * NEGATIVE TEST: SocialWorker is trying to delete a
			 * professional record that does not belong to him or her
			 **/
			{
				"socialWorker2", super.getEntityId("professionalRecord1"), IllegalArgumentException.class
			},
			/**
			 * NEGATIVE TEST: Another user is trying to delete a
			 * professional record
			 **/
			{
				"salesMan1", super.getEntityId("professionalRecord1"), IllegalArgumentException.class
			}
		};

		for (int i = 0; i < testingData.length; i++) {
			this.deleteProfessionalRecordTemplate((String) testingData[i][0], (Integer) testingData[i][1], (Class<?>) testingData[i][2]);
		}

	}

	private void deleteProfessionalRecordTemplate(String socialWorker, Integer professionalRecordId, Class<?> expected) {

		ProfessionalRecord professionalRecord = this.professionalRecordService.findOne(professionalRecordId);

		Class<?> caught = null;

		try {
			super.startTransaction();

			super.authenticate(socialWorker);

			this.curriculumService.deleteProfessionalRecord(professionalRecord);

			this.flushTransaction();

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
	 * 3. Manage his or her curriculum, which includes to create a miscellaneous
	 * record
	 * 
	 * Ratio of data coverage: 100% - Access as a SocialWorker or not. -
	 * SocialWorker has a curriculum or not - 2 attributes with restrictions
	 * 
	 **/
	@Test
	public void driverCreateMiscellaneousRecord() {

		Object testingData[][] = {

			/**
			 * POSITIVE TEST: SocialWorker is creating a miscellaneous
			 * record with correct information
			 **/
			{
				"socialWorker1", "Title", "http://www.attachment.com", null
			},
			/**
			 * NEGATIVE TEST: SocialWorker is trying to create a
			 * miscellaneous record but he or she has not a curriculum
			 **/
			{
				"socialWorker3", "Title", "http://www.attachment.com", IllegalArgumentException.class
			},
			/**
			 * NEGATIVE TEST: Another user is trying to create a
			 * miscellaneous record
			 **/
			{
				"salesMan1", "Title", "http://www.attachment.com", IllegalArgumentException.class
			},
			/**
			 * NEGATIVE TEST: SocialWorker is trying to create a
			 * miscellaneous record with incorrect link
			 **/
			{
				"socialWorker1", "Title", "invalidURL", ConstraintViolationException.class
			},
			/**
			 * NEGATIVE TEST: SocialWorker is trying to create a
			 * professional record with a blank name
			 **/
			{
				"socialWorker1", "", "http://www.attachment.com", ConstraintViolationException.class
			}
		};

		for (int i = 0; i < testingData.length; i++) {
			this.createMiscellaneousRecordTemplate((String) testingData[i][0], (String) testingData[i][1], (String) testingData[i][2], (Class<?>) testingData[i][3]);
		}

	}

	private void createMiscellaneousRecordTemplate(String socialWorker, String title, String attachment, Class<?> expected) {

		MiscellaneousRecord miscellaneousRecord = new MiscellaneousRecord();
		miscellaneousRecord.setTitle(title);
		miscellaneousRecord.setLinkAttachment(attachment);

		Class<?> caught = null;

		try {
			super.startTransaction();

			super.authenticate(socialWorker);

			this.curriculumService.addMiscellaneousRecord(miscellaneousRecord);

			this.flushTransaction();

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
	 * 3. Manage his or her curriculum, which includes to update a miscellaneous
	 * record
	 * 
	 * Ratio of data coverage: 100% - Access as a SocialWorker or not. -
	 * SocialWorker has a curriculum or not - SocialWorker has the miscellaneous
	 * record or not - 2 attributes with restrictions
	 * 
	 **/
	@Test
	public void driverUpdateMiscellaneousRecord() {

		Object testingData[][] = {

			/**
			 * POSITIVE TEST: SocialWorker is updating a miscellaneous
			 * record with correct information
			 **/
			{
				"socialWorker1", super.getEntityId("miscellaneousRecord1"), "Title", "http://www.attachment.com", null
			},
			/**
			 * NEGATIVE TEST: SocialWorker is trying to update a
			 * miscellaneous record but he or she has not a curriculum
			 **/
			{
				"socialWorker3", super.getEntityId("miscellaneousRecord1"), "Title", "http://www.attachment.com", NullPointerException.class
			},
			/**
			 * NEGATIVE TEST: SocialWorker is trying to update a
			 * miscellaneous record that does not belong to him or her
			 **/
			{
				"socialWorker2", super.getEntityId("miscellaneousRecord1"), "Title", "http://www.attachment.com", IllegalArgumentException.class
			},
			/**
			 * NEGATIVE TEST: Another user is trying to update a
			 * miscellaneous record
			 **/
			{
				"salesMan1", super.getEntityId("miscellaneousRecord1"), "Title", "http://www.attachment.com", IllegalArgumentException.class
			},
			/**
			 * NEGATIVE TEST: SocialWorker is trying to update a
			 * miscellaneous record with incorrect attachment
			 **/
			{
				"socialWorker1", super.getEntityId("miscellaneousRecord1"), "Title", "invalidURL", ConstraintViolationException.class
			},
			/**
			 * NEGATIVE TEST: SocialWorker is trying to update a
			 * miscellaneous record with a blank name title
			 **/
			{
				"socialWorker1", super.getEntityId("miscellaneousRecord1"), "", "http://www.attachment.com", ConstraintViolationException.class
			}
		};

		for (int i = 0; i < testingData.length; i++) {
			this.updateProfessionalRecordTemplate((String) testingData[i][0], (Integer) testingData[i][1], (String) testingData[i][2], (String) testingData[i][3], (Class<?>) testingData[i][4]);
		}

	}

	private void updateProfessionalRecordTemplate(String socialWorker, Integer miscellaneousRecordId, String title, String attachment, Class<?> expected) {

		MiscellaneousRecord miscellaneousRecord = this.miscellaneousRecordService.findOne(miscellaneousRecordId);
		miscellaneousRecord.setTitle(title);
		miscellaneousRecord.setLinkAttachment(attachment);

		Class<?> caught = null;

		try {
			super.startTransaction();

			super.authenticate(socialWorker);

			this.miscellaneousRecordService.getMiscellaneousRecordOfLoggedSocialWorker(miscellaneousRecordId.toString());
			this.curriculumService.updateMiscellaneousRecord(miscellaneousRecord);

			this.flushTransaction();

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
	 * 3. Manage his or her curriculum, which includes to delete a miscellaneous
	 * record
	 * 
	 * Ratio of data coverage: 100% - Access as a SocialWorker or not. -
	 * SocialWorker has a curriculum or not - SocialWorker has the miscellaneous
	 * record or not
	 * 
	 **/
	@Test
	public void driverDeleteMiscellaneousRecord() {

		Object testingData[][] = {

			/**
			 * POSITIVE TEST: SocialWorker is deleting a miscellaneous
			 * record
			 **/
			{
				"socialWorker1", super.getEntityId("miscellaneousRecord1"), null
			},
			/**
			 * NEGATIVE TEST: SocialWorker is trying to update a
			 * miscellaneous record but he or she has not a curriculum
			 **/
			{
				"socialWorker3", super.getEntityId("miscellaneousRecord1"), NullPointerException.class
			},
			/**
			 * NEGATIVE TEST: SocialWorker is trying to delete a
			 * miscellaneous record that does not belong to him or her
			 **/
			{
				"socialWorker2", super.getEntityId("miscellaneousRecord1"), IllegalArgumentException.class
			},
			/**
			 * NEGATIVE TEST: Another user is trying to delete a
			 * miscellaneous record
			 **/
			{
				"salesMan1", super.getEntityId("miscellaneousRecord1"), IllegalArgumentException.class
			}
		};

		for (int i = 0; i < testingData.length; i++) {
			this.deleteMiscellaneousRecordTemplate((String) testingData[i][0], (Integer) testingData[i][1], (Class<?>) testingData[i][2]);
		}

	}

	private void deleteMiscellaneousRecordTemplate(String socialWorker, Integer miscellaneousRecordId, Class<?> expected) {

		MiscellaneousRecord miscellaneousRecord = this.miscellaneousRecordService.findOne(miscellaneousRecordId);

		Class<?> caught = null;

		try {
			super.startTransaction();

			super.authenticate(socialWorker);

			this.curriculumService.deleteMiscellaneousRecord(miscellaneousRecord);

			this.flushTransaction();

			super.unauthenticate();
		} catch (Throwable oops) {
			caught = oops.getClass();
		} finally {
			this.rollbackTransaction();
		}

		super.checkExceptions(expected, caught);

	}

}
