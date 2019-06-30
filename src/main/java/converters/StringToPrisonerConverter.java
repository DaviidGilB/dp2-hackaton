
package converters;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.convert.converter.Converter;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;

import repositories.PrisonerRepository;
import domain.Prisoner;

@Component
@Transactional
public class StringToPrisonerConverter implements Converter<String, Prisoner> {

	@Autowired
	PrisonerRepository	prisonerRepository;


	@Override
	public Prisoner convert(String text) {

		Prisoner result = new Prisoner();
		int id;

		try {
			if (StringUtils.isEmpty(text))
				result = null;
			else {
				id = Integer.valueOf(text);
				result = this.prisonerRepository.findOne(id);
			}

		} catch (Throwable oops) {
			throw new IllegalArgumentException(oops);
		}
		return result;
	}
}
