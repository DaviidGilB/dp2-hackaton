
package converters;

import org.springframework.core.convert.converter.Converter;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import domain.SocialWorker;

@Component
@Transactional
public class SocialWorkerToStringConverter implements Converter<SocialWorker, String> {

	@Override
	public String convert(SocialWorker socialWorker) {
		String result;

		if (socialWorker == null)
			result = null;
		else
			result = String.valueOf(socialWorker.getId());
		return result;
	}

}
