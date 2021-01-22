package io.github.u2ware.data.test.example05;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.rest.core.support.SelfLinkProvider;
import org.springframework.hateoas.EntityModel;
import org.springframework.hateoas.Link;
import org.springframework.hateoas.server.RepresentationModelProcessor;
import org.springframework.stereotype.Component;
import org.springframework.util.ClassUtils;

@Component
public class ManyToOneEntityProcessor implements RepresentationModelProcessor<EntityModel<ManyToOneEntity>>{

	protected Log logger = LogFactory.getLog(getClass());

	private @Autowired SelfLinkProvider selfLinkProvider;

	@Override
	public EntityModel<ManyToOneEntity> process(EntityModel<ManyToOneEntity> model) {

		
		ManyToOneEntity content = model.getContent();
		Link link = selfLinkProvider.createSelfLinkFor(content);
		Link self = Link.of(link.expand().getHref(), "self");
		logger.info(content);
		logger.info(link);
		logger.info(self);
		logger.info(selfLinkProvider.getClass());
		
		
		logger.info(getClass().getName());
		model.add(Link.of(getClass().getName(), ClassUtils.getShortNameAsProperty(getClass())));
		return model;
	}

}
