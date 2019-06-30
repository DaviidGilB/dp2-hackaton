
package converters;

import org.springframework.core.convert.converter.Converter;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import domain.Charge;

@Component
@Transactional
public class ChargeToStringConverter implements Converter<Charge, String> {

	@Override
	public String convert(Charge charge) {
		String result;

		if (charge == null)
			result = null;
		else
			result = String.valueOf(charge.getId());
		return result;
	}

}
