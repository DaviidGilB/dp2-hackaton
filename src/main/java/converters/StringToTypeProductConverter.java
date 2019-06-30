
package converters;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.convert.converter.Converter;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;

import repositories.TypeProductRepository;
import domain.TypeProduct;

@Component
@Transactional
public class StringToTypeProductConverter implements Converter<String, TypeProduct> {

	@Autowired
	TypeProductRepository	typeProductRepository;


	@Override
	public TypeProduct convert(String text) {

		TypeProduct result = new TypeProduct();
		int id;

		try {
			if (StringUtils.isEmpty(text)) {
				result = null;
			} else {
				id = Integer.valueOf(text);
				result = this.typeProductRepository.findOne(id);
			}

		} catch (Throwable oops) {
			throw new IllegalArgumentException(oops);
		}
		return result;
	}
}
