
package converters;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.convert.converter.Converter;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;

import repositories.VisitRepository;
import domain.Visit;

@Component
@Transactional
public class StringToVisitConverter implements Converter<String, Visit> {

	@Autowired
	VisitRepository	visitRepository;


	@Override
	public Visit convert(String text) {

		Visit result = new Visit();
		int id;

		try {
			if (StringUtils.isEmpty(text))
				result = null;
			else {
				id = Integer.valueOf(text);
				result = this.visitRepository.findOne(id);
			}

		} catch (Throwable oops) {
			throw new IllegalArgumentException(oops);
		}
		return result;
	}
}
