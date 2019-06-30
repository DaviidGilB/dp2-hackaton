
package converters;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.convert.converter.Converter;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;

import repositories.GuardRepository;
import domain.Guard;

@Component
@Transactional
public class StringToGuardConverter implements Converter<String, Guard> {

	@Autowired
	GuardRepository	guardRepository;


	@Override
	public Guard convert(String text) {

		Guard result = new Guard();
		int id;

		try {
			if (StringUtils.isEmpty(text))
				result = null;
			else {
				id = Integer.valueOf(text);
				result = this.guardRepository.findOne(id);
			}

		} catch (Throwable oops) {
			throw new IllegalArgumentException(oops);
		}
		return result;
	}
}
