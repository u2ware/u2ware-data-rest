package io.github.u2ware.data.test.mto3;

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
	


//	private @Autowired @Qualifier("defaultConversionService")ConversionService conversionService;
//	
//	@HandleBeforeCreate @HandleBeforeSave
//	public void handleBefore(BaseEntity e){
//		logger.info("handleBefore "+e);
//
//		try {
//			ManyToOneEntity manyToOneEntityRef = conversionService.convert(e.getManyToOneEntity().toUri(),  ManyToOneEntity.class);
//			e.setManyToOneEntityRef(manyToOneEntityRef);
//		}catch(Exception ex) {
////			ex.printStackTrace();
//		}
//	}

	private @Autowired LinkConversionService linkConversionService;
	
	@HandleBeforeCreate @HandleBeforeSave
	public void handleBefore(BaseEntity e){
		logger.info("handleBefore "+e);

		linkConversionService.convertWithEntity(ManyToOneEntity.class, e.getManyToOneEntity(), ref->{
			e.setManyToOneEntityRef(ref);
		});
		
	}
	
}
