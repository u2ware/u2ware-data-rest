package io.github.u2ware.data.test.mto3;

import javax.persistence.AttributeConverter;
import javax.persistence.Converter;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.hateoas.Link;

@Converter(autoApply = true)
public class LinkAttributeConverter implements AttributeConverter<Link, String>{

	protected Log logger = LogFactory.getLog(getClass());

	@Override
	public String convertToDatabaseColumn(Link attribute) {
		logger.info("convertToDatabaseColumn: "+attribute);
		return null;
	}

	@Override
	public Link convertToEntityAttribute(String dbData) {
		logger.info("convertToEntityAttribute: "+dbData);
		return Link.of(".").withSelfRel();
	}
}
