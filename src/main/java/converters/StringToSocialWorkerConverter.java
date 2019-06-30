
package converters;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.convert.converter.Converter;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;

import repositories.SocialWorkerRepository;
import domain.SocialWorker;

@Component
@Transactional
public class StringToSocialWorkerConverter implements Converter<String, SocialWorker> {

	@Autowired
	SocialWorkerRepository	socialWorkerRepository;


	@Override
	public SocialWorker convert(String text) {

		SocialWorker result = new SocialWorker();
		int id;

		try {
			if (StringUtils.isEmpty(text))
				result = null;
			else {
				id = Integer.valueOf(text);
				result = this.socialWorkerRepository.findOne(id);
			}

		} catch (Throwable oops) {
			throw new IllegalArgumentException(oops);
		}
		return result;
	}
}
