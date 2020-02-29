package org.springframework.data.rest.webmvc.support;

import java.io.Serializable;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.rest.core.config.RepositoryRestConfiguration;
import org.springframework.data.rest.core.mapping.ResourceMappings;
import org.springframework.data.rest.core.mapping.ResourceMetadata;
import org.springframework.data.rest.webmvc.BaseUri;
import org.springframework.hateoas.Link;
import org.springframework.hateoas.Resource;
import org.springframework.hateoas.ResourceProcessor;

public class EntityViewSerializer<T, ID extends Serializable> implements ResourceProcessor<Resource<EntityView<T, ID>>>{

//	protected Log logger = LogFactory.getLog(getClass());
	
	private @Autowired RepositoryRestConfiguration config;
	private @Autowired ResourceMappings mappings;
	
	private RepositoryLinkBuilder repositoryLinkBuilder(Class<?> entityType) {
		BaseUri baseUri = new BaseUri(config.getBaseUri());
		ResourceMetadata metadata = mappings.getMetadataFor(entityType) ;
		RepositoryLinkBuilder builder = new RepositoryLinkBuilder(metadata, baseUri);
		return builder;
	}
	
	@Override
	public Resource<EntityView<T,ID>> process(Resource<EntityView<T,ID>> resource) {
		
		try {
			EntityView<T,ID> content = resource.getContent();
//			logger.info("content "+content);
			
			T target = content.serialize();
//			logger.info("target "+target);
			
			Link link = repositoryLinkBuilder(target.getClass()).slash(content.getId()).withSelfRel();
//			logger.info("link "+link);
			
			resource.add(link);
		}catch(Exception e) {
//			logger.debug("", e);
		}
		return resource;
	}
}