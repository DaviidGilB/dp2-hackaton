
package converters;

import org.springframework.core.convert.converter.Converter;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import domain.SalesMan;

@Component
@Transactional
public class SalesManToStringConverter implements Converter<SalesMan, String> {

	@Override
	public String convert(SalesMan salesMan) {
		String result;

		if (salesMan == null)
			result = null;
		else
			result = String.valueOf(salesMan.getId());
		return result;
	}

}
