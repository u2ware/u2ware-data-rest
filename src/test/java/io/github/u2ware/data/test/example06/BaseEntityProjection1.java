package io.github.u2ware.data.test.example06;

import org.springframework.data.rest.core.config.Projection;

@Projection(name = "baseEntityProjection1", types = { BaseEntity.class }) 
public interface BaseEntityProjection1 {

	  String getName(); 
	
}