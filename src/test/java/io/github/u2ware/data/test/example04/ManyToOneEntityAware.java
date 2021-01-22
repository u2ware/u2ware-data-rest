package io.github.u2ware.data.test.example04;

import java.util.Optional;

import javax.servlet.http.HttpServletRequest;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.AuditorAware;
import org.springframework.stereotype.Component;
import org.springframework.util.Assert;
import org.springframework.web.context.request.RequestAttributes;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

@Component
public class ManyToOneEntityAware implements AuditorAware<ManyToOneEntity>{

	protected Log logger = LogFactory.getLog(getClass());
	
	public static final String AUDITING_HEADER = "AuditingObject";
	
	private @Autowired ManyToOneEntityRepository manyToOneEntitytRepository;
	
	
	@Override
	public Optional<ManyToOneEntity> getCurrentAuditor() {
		
		logger.info("getCurrentAuditor");
		logger.info("getCurrentAuditor");
		logger.info("getCurrentAuditor");
		logger.info("getCurrentAuditor");
		
		HttpServletRequest request = getCurrentRequest();
		String id = request.getHeader(AUDITING_HEADER);
		return id == null ? Optional.empty() : manyToOneEntitytRepository.findById(Long.parseLong(id));
	}
	
	protected HttpServletRequest getCurrentRequest() {
		RequestAttributes attrs = RequestContextHolder.getRequestAttributes();
		Assert.state(attrs instanceof ServletRequestAttributes, "No current ServletRequestAttributes");
		return ((ServletRequestAttributes) attrs).getRequest();
	}

}
