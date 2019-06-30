
package converters;

import org.springframework.core.convert.converter.Converter;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import domain.Visit;

@Component
@Transactional
public class VisitToStringConverter implements Converter<Visit, String> {

	@Override
	public String convert(Visit visit) {
		String result;

		if (visit == null)
			result = null;
		else
			result = String.valueOf(visit.getId());
		return result;
	}

}
