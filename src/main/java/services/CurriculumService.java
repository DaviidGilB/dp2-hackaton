
package services;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import javax.transaction.Transactional;

import org.joda.time.LocalDate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.util.Assert;

import repositories.CurriculumRepository;
import utilities.RandomString;
import domain.Curriculum;
import domain.EducationRecord;
import domain.MiscellaneousRecord;
import domain.PersonalRecord;
import domain.ProfessionalRecord;
import domain.SocialWorker;

@Service
@Transactional
public class CurriculumService {

	@Autowired
	private CurriculumRepository		curriculumRepository;

	@Autowired
	private PersonalRecordService		personalRecordService;

	@Autowired
	private SocialWorkerService			socialWorkerService;

	@Autowired
	private EducationRecordService		educationRecordService;

	@Autowired
	private ProfessionalRecordService	professionalRecordService;

	@Autowired
	private MiscellaneousRecordService	miscellaneousRecordService;


	public Curriculum create() {

		List<MiscellaneousRecord> miscellaneousRecords = new ArrayList<MiscellaneousRecord>();
		List<EducationRecord> educationRecords = new ArrayList<EducationRecord>();
		List<ProfessionalRecord> professionalRecords = new ArrayList<ProfessionalRecord>();
		PersonalRecord personalRecord = new PersonalRecord();
		personalRecord = this.personalRecordService.create();

		Curriculum curriculum = new Curriculum();
		curriculum.setTicker(this.generateTicker());
		curriculum.setMiscellaneousRecords(miscellaneousRecords);
		curriculum.setEducationRecords(educationRecords);
		curriculum.setPersonalRecord(personalRecord);
		curriculum.setProfessionalRecords(professionalRecords);

		return curriculum;

	}

	// Método auxiliar para generar el ticker-------------------------------
	public String generateTicker() {
		String res = "";
		Date date = null;
		String date1;
		String date2 = LocalDate.now().toString();
		String gen = new RandomString(6).nextString();
		List<String> tickers = this.curriculumRepository.getAllTickers();
		SimpleDateFormat df_in = new SimpleDateFormat("yyMMdd");
		SimpleDateFormat df_output = new SimpleDateFormat("yyyy-MM-dd");
		try {
			date = df_output.parse(date2);
		} catch (ParseException e) {
			e.printStackTrace();
		}
		date1 = df_in.format(date);
		res = res + date1 + "-" + gen;

		if (tickers.contains(res)) {
			return this.generateTicker();
		}
		return res;
	}

	public List<Curriculum> findAll() {
		List<Curriculum> result;

		result = this.curriculumRepository.findAll();

		return result;
	}

	public Curriculum findOne(Integer id) {

		Curriculum result = this.curriculumRepository.findOne(id);
		return result;

	}

	public Curriculum save(Curriculum curriculum) {
		return this.curriculumRepository.save(curriculum);

	}

	public SocialWorker getSocialWorkerByCurriculum(int id) {
		return this.curriculumRepository.getSocialWorkerByCurriculum(id);
	}

	public void addEducationRecord(EducationRecord educationRecord) {
		SocialWorker logguedSocialWorker = this.socialWorkerService.loggedSocialWorker();
		Curriculum curriculum = logguedSocialWorker.getCurriculum();

		Assert.notNull(curriculum);
		Assert.isTrue(educationRecord.getId() == 0);

		curriculum.getEducationRecords().add(educationRecord);
		this.save(curriculum);

	}

	public void updateEducationRecord(EducationRecord educationRecord) {
		SocialWorker logguedSocialWorker = this.socialWorkerService.loggedSocialWorker();
		Curriculum curriculum = logguedSocialWorker.getCurriculum();

		Assert.notNull(curriculum);
		Assert.isTrue(educationRecord.getId() != 0 && logguedSocialWorker.getCurriculum().getEducationRecords().contains(educationRecord));

		this.educationRecordService.save(educationRecord);

	}

	public void deleteEducationRecord(EducationRecord educationRecord) {
		SocialWorker logguedSocialWorker = this.socialWorkerService.loggedSocialWorker();
		Curriculum c = logguedSocialWorker.getCurriculum();

		Assert.isTrue(c.getEducationRecords().contains(educationRecord));

		c.getEducationRecords().remove(educationRecord);
		this.save(c);
		this.educationRecordService.delete(educationRecord);

	}

	public void addProfessionalRecord(ProfessionalRecord professionalRecord) {
		SocialWorker logguedSocialWorker = this.socialWorkerService.loggedSocialWorker();
		Curriculum curriculum = logguedSocialWorker.getCurriculum();

		Assert.notNull(curriculum);
		Assert.isTrue(professionalRecord.getId() == 0);

		curriculum.getProfessionalRecords().add(professionalRecord);
		this.save(curriculum);

	}

	public void updateProfessionalRecord(ProfessionalRecord professionalRecord) {
		SocialWorker logguedSocialWorker = this.socialWorkerService.loggedSocialWorker();
		Curriculum curriculum = logguedSocialWorker.getCurriculum();

		Assert.notNull(curriculum);
		Assert.isTrue(professionalRecord.getId() != 0 && logguedSocialWorker.getCurriculum().getProfessionalRecords().contains(professionalRecord));

		this.professionalRecordService.save(professionalRecord);

	}

	public void deleteProfessionalRecord(ProfessionalRecord professionalRecord) {
		SocialWorker logguedSocialWorker = this.socialWorkerService.loggedSocialWorker();
		Curriculum c = logguedSocialWorker.getCurriculum();

		Assert.isTrue(c.getProfessionalRecords().contains(professionalRecord));

		c.getProfessionalRecords().remove(professionalRecord);
		this.save(c);
		this.professionalRecordService.delete(professionalRecord);

	}

	public void addMiscellaneousRecord(MiscellaneousRecord miscellaneousRecord) {
		SocialWorker logguedSocialWorker = this.socialWorkerService.loggedSocialWorker();
		Curriculum curriculum = logguedSocialWorker.getCurriculum();

		Assert.notNull(curriculum);
		Assert.isTrue(miscellaneousRecord.getId() == 0);

		curriculum.getMiscellaneousRecords().add(miscellaneousRecord);
		this.save(curriculum);

	}

	public void updateMiscellaneousRecord(MiscellaneousRecord miscellaneousRecord) {
		SocialWorker logguedSocialWorker = this.socialWorkerService.loggedSocialWorker();
		Curriculum curriculum = logguedSocialWorker.getCurriculum();

		Assert.notNull(curriculum);
		Assert.isTrue(miscellaneousRecord.getId() != 0 && logguedSocialWorker.getCurriculum().getMiscellaneousRecords().contains(miscellaneousRecord));

		this.miscellaneousRecordService.save(miscellaneousRecord);

	}

	public void deleteMiscellaneousRecord(MiscellaneousRecord miscellaneousRecord) {
		SocialWorker logguedSocialWorker = this.socialWorkerService.loggedSocialWorker();
		Curriculum c = logguedSocialWorker.getCurriculum();

		Assert.isTrue(c.getMiscellaneousRecords().contains(miscellaneousRecord));

		c.getMiscellaneousRecords().remove(miscellaneousRecord);
		this.save(c);
		this.miscellaneousRecordService.delete(miscellaneousRecord);

	}

	public Curriculum getCurriculumOfLoggedSocialWorker() {
		SocialWorker socialWorker = this.socialWorkerService.loggedSocialWorker();
		Curriculum curriculum = socialWorker.getCurriculum();
		Assert.notNull(curriculum);
		return curriculum;
	}

}
