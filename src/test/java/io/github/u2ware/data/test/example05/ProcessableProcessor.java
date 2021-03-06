package io.github.u2ware.data.test.example05;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.hateoas.EntityModel;
import org.springframework.hateoas.Link;
import org.springframework.hateoas.server.RepresentationModelProcessor;
import org.springframework.stereotype.Component;
import org.springframework.util.ClassUtils;

@Component
public class ProcessableProcessor implements RepresentationModelProcessor<EntityModel<Processable>>{

	protected Log logger = LogFactory.getLog(getClass());

	@Override
	public EntityModel<Processable> process(EntityModel<Processable> model) {

		logger.info(getClass().getName());
		model.add(Link.of(getClass().getName(), ClassUtils.getShortNameAsProperty(getClass())));
		return model;
	}

}
