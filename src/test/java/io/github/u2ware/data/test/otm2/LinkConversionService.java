package io.github.u2ware.data.test.otm2;

import java.util.HashSet;
import java.util.Set;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.core.convert.ConversionService;
import org.springframework.hateoas.Link;
import org.springframework.stereotype.Component;

@Component
public class LinkConversionService {

	protected Log logger = LogFactory.getLog(getClass());

	private @Autowired @Qualifier("defaultConversionService")ConversionService conversionService;
	
	public <T> void convertWithEntity(Class<T> entityType, Link link, EntityCallback<T> callback) {
		try {
			T entity = conversionService.convert(link.toUri(), entityType);
			callback.convertWith(entity);
		}catch(Exception e) {
			e.printStackTrace();
			
		}
	}
	
	public <T> void convertWithEntity(Class<T> entityType, Set<Link> links, EntitiesCallback<T> callback) {
		Set<T> entities = new HashSet<>();
		for(Link r : links) {
			convertWithEntity(entityType, r, item->{
				entities.add((T)item);
			});
		}
		if(entities.size() > 0) {
			callback.convertWith(entities);
		}
	}
	
	
	@FunctionalInterface
	public interface EntityCallback<T> {
		void convertWith(T entity);
	}

	@FunctionalInterface
	public interface EntitiesCallback<T> {
		void convertWith(Set<T> entity);
	}

	
}
