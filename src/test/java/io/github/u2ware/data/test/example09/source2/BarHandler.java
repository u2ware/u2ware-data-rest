package io.github.u2ware.data.test.example09.source2;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.data.rest.core.annotation.HandleAfterCreate;
import org.springframework.data.rest.core.annotation.HandleAfterDelete;
import org.springframework.data.rest.core.annotation.HandleAfterSave;
import org.springframework.data.rest.core.annotation.HandleBeforeCreate;
import org.springframework.data.rest.core.annotation.HandleBeforeDelete;
import org.springframework.data.rest.core.annotation.HandleBeforeSave;
import org.springframework.data.rest.core.annotation.RepositoryEventHandler;
import org.springframework.stereotype.Component;

import io.github.u2ware.data.rest.core.annotation.HandleAfterRead;
import io.github.u2ware.data.rest.core.annotation.HandleBeforeRead;


@Component
@RepositoryEventHandler
public class BarHandler {
	
	protected Log logger = LogFactory.getLog(getClass());

	
	@HandleBeforeCreate
	public void onBeforeCreate(Bar entity) {
	}
	@HandleAfterCreate
	public void onAfterCreate(Bar entity) {
	}

	
	@HandleBeforeSave
	public void onBeforeSave(Bar entity) {
	}
	@HandleAfterSave
	public void onAfterSave(Bar entity) {
	}

	
	@HandleBeforeDelete
	public void onBeforeDelete(Bar entity) {
	}
	@HandleAfterDelete
	public void onAfterDelete(Bar entity) {
	}
	
	
	@HandleBeforeRead
	public void onBeforeRead(Bar entity, Object query) {
	}
	@HandleAfterRead
	public void onAfterRead(Bar entity) {
	}	
	

}
