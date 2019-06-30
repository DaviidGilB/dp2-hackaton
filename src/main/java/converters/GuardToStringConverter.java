
package converters;

import org.springframework.core.convert.converter.Converter;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import domain.Guard;

@Component
@Transactional
public class GuardToStringConverter implements Converter<Guard, String> {

	@Override
	public String convert(Guard guard) {
		String result;

		if (guard == null)
			result = null;
		else
			result = String.valueOf(guard.getId());
		return result;
	}

}
