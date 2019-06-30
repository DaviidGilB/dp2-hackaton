
package services;

import java.util.List;

import javax.transaction.Transactional;

import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.util.Assert;

import repositories.PersonalRecordRepository;
import domain.PersonalRecord;
import domain.SocialWorker;

@Service
@Transactional
public class PersonalRecordService {

	@Autowired
	private PersonalRecordRepository	personalRecordRepository;

	@Autowired
	private SocialWorkerService			socialWorkerService;


	public PersonalRecord create() {

		PersonalRecord personalRecord = new PersonalRecord();
		personalRecord.setFullName("");
		personalRecord.setPhoto("");
		personalRecord.setEmail("");
		personalRecord.setPhoneNumber("");
		personalRecord.setUrlLinkedInProfile("");

		return personalRecord;

	}

	public List<PersonalRecord> findAll() {
		return this.personalRecordRepository.findAll();
	}

	public PersonalRecord findOne(Integer id) {
		return this.personalRecordRepository.findOne(id);
	}

	public PersonalRecord save(PersonalRecord personalRecord) {
		return this.personalRecordRepository.save(personalRecord);
	}

	public PersonalRecord getPersonalRecordAsSocialWorker(String personalRecordId) {
		SocialWorker socialWorker = this.socialWorkerService.loggedSocialWorker();

		Assert.isTrue(StringUtils.isNumeric(personalRecordId));
		int personalRecordIdInt = Integer.parseInt(personalRecordId);

		Assert.isTrue(socialWorker.getCurriculum().getPersonalRecord().getId() == personalRecordIdInt);
		PersonalRecord personalRecord = this.personalRecordRepository.findOne(personalRecordIdInt);
		return personalRecord;
	}
}
