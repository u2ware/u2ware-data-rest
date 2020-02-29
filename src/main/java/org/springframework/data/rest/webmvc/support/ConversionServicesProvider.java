package org.springframework.data.rest.webmvc.support;

import java.util.Collection;

import org.springframework.beans.BeansException;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;
import org.springframework.core.convert.ConversionService;

public class ConversionServicesProvider implements ApplicationContextAware{

	private static Collection<ConversionService> conversionServices;
	
	private static void setConversionServices(ApplicationContext applicationContext){
		conversionServices = applicationContext.getBeansOfType(ConversionService.class).values();
	}
	
	public static Collection<ConversionService> getConversionServices(){
		return conversionServices;
	}

	@Override
	public void setApplicationContext(ApplicationContext applicationContext) throws BeansException {
		ConversionServicesProvider.setConversionServices(applicationContext);
	}
}
