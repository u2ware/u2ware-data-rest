package io.github.u2ware.data.test.example06;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.rest.core.config.Projection;

@Projection(name = "baseEntityProjection2", types = { BaseEntity.class }) 
public interface BaseEntityProjection2 {

	@Value("#{target.name}") 
	String getAge();
	
}