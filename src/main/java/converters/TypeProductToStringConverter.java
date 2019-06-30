
package converters;

import org.springframework.core.convert.converter.Converter;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import domain.TypeProduct;

@Component
@Transactional
public class TypeProductToStringConverter implements Converter<TypeProduct, String> {

	@Override
	public String convert(TypeProduct typeProduct) {
		String result;

		if (typeProduct == null) {
			result = null;
		} else {
			result = String.valueOf(typeProduct.getId());
		}
		return result;
	}

}
