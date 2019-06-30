
package converters;

import org.springframework.core.convert.converter.Converter;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import domain.Warden;

@Component
@Transactional
public class WardenToStringConverter implements Converter<Warden, String> {

	@Override
	public String convert(Warden warden) {
		String result;

		if (warden == null)
			result = null;
		else
			result = String.valueOf(warden.getId());
		return result;
	}

}
