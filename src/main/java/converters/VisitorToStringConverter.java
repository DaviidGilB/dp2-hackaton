
package converters;

import org.springframework.core.convert.converter.Converter;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import domain.Visitor;

@Component
@Transactional
public class VisitorToStringConverter implements Converter<Visitor, String> {

	@Override
	public String convert(Visitor visitor) {
		String result;

		if (visitor == null)
			result = null;
		else
			result = String.valueOf(visitor.getId());
		return result;
	}

}
