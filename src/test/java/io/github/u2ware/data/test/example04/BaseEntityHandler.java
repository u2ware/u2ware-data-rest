package io.github.u2ware.data.test.example04;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.data.rest.core.annotation.HandleAfterCreate;
import org.springframework.data.rest.core.annotation.HandleAfterSave;
import org.springframework.data.rest.core.annotation.HandleBeforeCreate;
import org.springframework.data.rest.core.annotation.HandleBeforeSave;
import org.springframework.data.rest.core.annotation.RepositoryEventHandler;
import org.springframework.stereotype.Component;

@Component
@RepositoryEventHandler
public class BaseEntityHandler {


	private Log logger = LogFactory.getLog(getClass());

	
	@HandleBeforeCreate
	protected void handleBeforeCreate(BaseEntity entity) {
		logger.info("HandleBeforeCreate : "+entity);
	}
	@HandleAfterCreate
	protected void handleAfterCreate(BaseEntity entity) {
		logger.info("HandleAfterCreate : "+entity);
	}
	@HandleBeforeSave
	protected void handleBeforeSave(BaseEntity entity) {
		logger.info("HandleBeforeSave : "+entity);
	}
	@HandleAfterSave
	protected void handleAfterSave(BaseEntity entity) {
		logger.info("HandleAfterSave : "+entity);
	}
	
	

}
