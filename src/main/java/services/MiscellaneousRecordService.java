
package services;

import java.util.List;

import javax.transaction.Transactional;

import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.util.Assert;

import repositories.MiscellaneousRecordRepository;
import domain.MiscellaneousRecord;
import domain.SocialWorker;

@Service
@Transactional
public class MiscellaneousRecordService {

	// Manged Repository

	@Autowired
	private MiscellaneousRecordRepository	miscellaneousRecordRepository;

	@Autowired
	private SocialWorkerService				socialWorkerService;			;


	// Simple CRUD methods

	public MiscellaneousRecord create() {

		MiscellaneousRecord miscellaneousRecord = new MiscellaneousRecord();
		miscellaneousRecord.setTitle("");
		miscellaneousRecord.setLinkAttachment("");

		return miscellaneousRecord;

	}

	public List<MiscellaneousRecord> findAll() {
		return this.miscellaneousRecordRepository.findAll();
	}

	public MiscellaneousRecord findOne(Integer id) {
		return this.miscellaneousRecordRepository.findOne(id);
	}

	public void save(MiscellaneousRecord miscellaneousRecord) {
		this.miscellaneousRecordRepository.save(miscellaneousRecord);
	}

	public void delete(MiscellaneousRecord miscellaneousRecord) {
		this.miscellaneousRecordRepository.delete(miscellaneousRecord);
	}

	public MiscellaneousRecord getMiscellaneousRecordOfLoggedSocialWorker(String miscellaneousRecordId) {
		SocialWorker socialWorker = this.socialWorkerService.loggedSocialWorker();

		Assert.isTrue(StringUtils.isNumeric(miscellaneousRecordId));
		int miscellaneousRecordIdInt = Integer.parseInt(miscellaneousRecordId);

		MiscellaneousRecord miscellaneousRecord = this.findOne(miscellaneousRecordIdInt);
		Assert.isTrue(socialWorker.getCurriculum().getMiscellaneousRecords().contains(miscellaneousRecord));
		return miscellaneousRecord;
	}

}
