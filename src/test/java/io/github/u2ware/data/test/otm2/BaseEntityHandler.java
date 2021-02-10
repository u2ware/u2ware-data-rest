package io.github.u2ware.data.test.otm2;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.rest.core.annotation.HandleBeforeCreate;
import org.springframework.data.rest.core.annotation.HandleBeforeSave;
import org.springframework.data.rest.core.annotation.RepositoryEventHandler;
import org.springframework.stereotype.Component;


@RepositoryEventHandler(BaseEntity.class)
@Component
public class BaseEntityHandler  {

	protected Log logger = LogFactory.getLog(getClass());
	
	private @Autowired LinkConversionService linkConversionService;

	@HandleBeforeCreate @HandleBeforeSave
	public void handleBefore2(BaseEntity e){
		
		linkConversionService.convertWithEntity(OtherEntity.class, e.getOtm(), otms->{
			e.getOtmRef().clear();
			for(OtherEntity otm : otms) {
				e.getOtmRef().add(otm);
			}
		});
	}

}
