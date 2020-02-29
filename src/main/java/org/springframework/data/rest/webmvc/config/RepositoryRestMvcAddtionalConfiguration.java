package org.springframework.data.rest.webmvc.config;

import java.io.Serializable;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.jpa.repository.query.support.ExtendedEntityPathResolver;
import org.springframework.data.querydsl.EntityPathResolver;
import org.springframework.data.rest.core.event.AnnotatedReadEventHandlerInvoker;
import org.springframework.data.rest.webmvc.RepositoryRestController;
import org.springframework.data.rest.webmvc.support.ConversionServicesProvider;
import org.springframework.data.rest.webmvc.support.EntityViewSerializer;

@RepositoryRestController
@Configuration
public class RepositoryRestMvcAddtionalConfiguration {
	
	@Bean 
	public AnnotatedReadEventHandlerInvoker annotatedReadEventHandlerInvoker() {
		return new AnnotatedReadEventHandlerInvoker();
	}
	
	@Bean 
	public EntityPathResolver extendedEntityPathResolver() {
		return new ExtendedEntityPathResolver();
	}

	@Bean
	public ConversionServicesProvider conversionServicesProvider() {
		return new ConversionServicesProvider();
	}

	@Bean 
	public <T,ID extends Serializable> EntityViewSerializer<T,ID> entityViewSerializer() {
		return new EntityViewSerializer<>();
	}
//	
//	@Bean 
//	public UriLinkParser uriLinkParser() {
//		return new UriLinkParser();
//	}
//	@Bean 
//	public UriLinkBuilder uriLinkBuilder() {
//		return new UriLinkBuilder();
//	}
	
}
