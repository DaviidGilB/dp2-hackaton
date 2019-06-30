
package converters;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.convert.converter.Converter;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;

import repositories.WardenRepository;
import domain.Warden;

@Component
@Transactional
public class StringToWardenConverter implements Converter<String, Warden> {

	@Autowired
	WardenRepository	wardenRepository;


	@Override
	public Warden convert(String text) {

		Warden result = new Warden();
		int id;

		try {
			if (StringUtils.isEmpty(text))
				result = null;
			else {
				id = Integer.valueOf(text);
				result = this.wardenRepository.findOne(id);
			}

		} catch (Throwable oops) {
			throw new IllegalArgumentException(oops);
		}
		return result;
	}
}
