package io.github.u2ware.data.test.example07;

import java.io.Serializable;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.data.rest.webmvc.spi.BackendIdConverter;
import org.springframework.stereotype.Component;
import org.springframework.util.ClassUtils;
import org.springframework.util.StringUtils;

@Component
public class CompositeIdEntityBackendIdConverter implements BackendIdConverter{

	private Log logger = LogFactory.getLog(getClass());
	
	@Override
	public boolean supports(Class<?> delimiter) {
		logger.info("supports "+delimiter);
		return ClassUtils.isAssignable(CompositeIdEntity.class, delimiter) ;
	}

	@Override
	public Serializable fromRequestId(String id, Class<?> entityType) {
		logger.info("fromRequestId"+id);
		String[] aa = StringUtils.delimitedListToStringArray(id, "@");
		CompositeIdEntity.ID entityId = new CompositeIdEntity.ID(aa[0], aa[1]);
		return entityId;
	}

	@Override
	public String toRequestId(Serializable id, Class<?> entityType) {
		logger.info("toRequestId");
		CompositeIdEntity.ID entityId = (CompositeIdEntity.ID)id;
		return entityId.getKey1()+"@"+entityId.getKey2();
	}

}
