
package converters;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.convert.converter.Converter;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;

import repositories.SalesManRepository;
import domain.SalesMan;

@Component
@Transactional
public class StringToSalesManConverter implements Converter<String, SalesMan> {

	@Autowired
	SalesManRepository	salesManRepository;


	@Override
	public SalesMan convert(String text) {

		SalesMan result = new SalesMan();
		int id;

		try {
			if (StringUtils.isEmpty(text))
				result = null;
			else {
				id = Integer.valueOf(text);
				result = this.salesManRepository.findOne(id);
			}

		} catch (Throwable oops) {
			throw new IllegalArgumentException(oops);
		}
		return result;
	}
}
