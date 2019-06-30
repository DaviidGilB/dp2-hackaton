
package converters;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.convert.converter.Converter;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;

import repositories.VisitorRepository;
import domain.Visitor;

@Component
@Transactional
public class StringToVisitorConverter implements Converter<String, Visitor> {

	@Autowired
	VisitorRepository	visitorRepository;


	@Override
	public Visitor convert(String text) {

		Visitor result = new Visitor();
		int id;

		try {
			if (StringUtils.isEmpty(text))
				result = null;
			else {
				id = Integer.valueOf(text);
				result = this.visitorRepository.findOne(id);
			}

		} catch (Throwable oops) {
			throw new IllegalArgumentException(oops);
		}
		return result;
	}
}
