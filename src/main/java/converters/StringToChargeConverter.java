
package converters;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.convert.converter.Converter;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;

import repositories.ChargeRepository;
import domain.Charge;

@Component
@Transactional
public class StringToChargeConverter implements Converter<String, Charge> {

	@Autowired
	ChargeRepository	chargeRepository;


	@Override
	public Charge convert(String text) {

		Charge result = new Charge();
		int id;

		try {
			if (StringUtils.isEmpty(text))
				result = null;
			else {
				id = Integer.valueOf(text);
				result = this.chargeRepository.findOne(id);
			}

		} catch (Throwable oops) {
			throw new IllegalArgumentException(oops);
		}
		return result;
	}
}
