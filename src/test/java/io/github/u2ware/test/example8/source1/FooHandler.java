package io.github.u2ware.test.example8.source1;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.data.rest.core.annotation.HandleAfterCreate;
import org.springframework.data.rest.core.annotation.HandleAfterDelete;
import org.springframework.data.rest.core.annotation.HandleAfterRead;
import org.springframework.data.rest.core.annotation.HandleAfterSave;
import org.springframework.data.rest.core.annotation.HandleBeforeCreate;
import org.springframework.data.rest.core.annotation.HandleBeforeDelete;
import org.springframework.data.rest.core.annotation.HandleBeforeRead;
import org.springframework.data.rest.core.annotation.HandleBeforeSave;
import org.springframework.data.rest.core.annotation.RepositoryEventHandler;
import org.springframework.stereotype.Component;

@Component
@RepositoryEventHandler
public class FooHandler {
	
	protected Log logger = LogFactory.getLog(getClass());

	
	@HandleBeforeCreate
	public void onBeforeCreate(Foo entity) {
		logger.info("onBeforeCreate: "+entity.getBar());
		logger.info("onBeforeCreate: "+entity.getBars());
	}
	@HandleAfterCreate
	public void onAfterCreate(Foo entity) {
	}

	
	@HandleBeforeSave
	public void onBeforeSave(Foo entity) {
	}
	@HandleAfterSave
	public void onAfterSave(Foo entity) {
	}

	
	@HandleBeforeDelete
	public void onBeforeDelete(Foo entity) {
	}
	@HandleAfterDelete
	public void onAfterDelete(Foo entity) {
	}
	
	
	@HandleBeforeRead
	public void onBeforeRead(Foo entity, Object query) {
	}
	@HandleAfterRead
	public void onAfterRead(Foo entity) {
	}	
}
