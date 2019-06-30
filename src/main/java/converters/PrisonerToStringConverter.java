
package converters;

import org.springframework.core.convert.converter.Converter;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import domain.Prisoner;

@Component
@Transactional
public class PrisonerToStringConverter implements Converter<Prisoner, String> {

	@Override
	public String convert(Prisoner prisoner) {
		String result;

		if (prisoner == null)
			result = null;
		else
			result = String.valueOf(prisoner.getId());
		return result;
	}

}
