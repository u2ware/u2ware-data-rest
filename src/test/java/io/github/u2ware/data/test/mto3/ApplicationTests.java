package io.github.u2ware.data.test.mto3;

import java.util.Map;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.context.TestConfiguration;
import org.springframework.context.annotation.Import;
import org.springframework.data.rest.core.config.RepositoryRestConfiguration;
import org.springframework.data.rest.webmvc.config.RepositoryRestConfigurer;
import org.springframework.test.web.servlet.MockMvc;

import io.github.u2ware.data.test.RestMockMvc;
import io.github.u2ware.data.test.mto3.ApplicationTests.ApplicationTestsRepositoryRestConfigurer;


@SpringBootTest
@Import(ApplicationTestsRepositoryRestConfigurer.class)
@AutoConfigureMockMvc
public class ApplicationTests { 

	@TestConfiguration
	public static class ApplicationTestsRepositoryRestConfigurer implements RepositoryRestConfigurer{
		@Override
		public void configureRepositoryRestConfiguration(RepositoryRestConfiguration config) {
//			config.exposeIdsFor(ManyToOneEntity.class);
//			config.setReturnBodyOnCreate(true);
//			config.setReturnBodyOnUpdate(true);
		}
	}
	
	protected Log logger = LogFactory.getLog(getClass());
	
	protected @Autowired BaseEntityRepository baseEntityRepository;
	protected @Autowired ManyToOneEntityRepository manyToOneEntityRepository;
	protected @Autowired MockMvc mockMvc;
	
	
	@Test
	public void manyToOneTest() throws Exception {
		RestMockMvc $ = new RestMockMvc(mockMvc, "");

		$.POST("/manyToOneEntities").C(ManyToOneEntity.builder().name("mto_a").age(1).build()).is2xx().andReturn("mtoLink1");		
		$.POST("/manyToOneEntities").C(ManyToOneEntity.builder().name("mto_b").age(2).build()).is2xx().andReturn("mtoLink2");
		$.POST("/manyToOneEntities").H("Accept", "application/json").C(ManyToOneEntity.builder().name("mto_c").age(1).build()).is2xx().andReturn("mtoJson1");		
		$.POST("/manyToOneEntities").H("Accept", "application/json").C(ManyToOneEntity.builder().name("mto_d").age(2).build()).is2xx().andReturn("mtoJson2");		

//		logger.info($.variables().resolveUri("aaaa_{mtoJson1}_bbbb"));
//		logger.info($.variables().resolveUri("aaaa_{mtoJson1.$}_bbbb"));
//		logger.info($.variables().resolveUri("aaaa_{mtoJson1.$.name}_bbbb"));
//		
//		logger.info($.variables().resolveValue("aaaa_{mtoJson1}_bbbb"));
//		logger.info($.variables().resolveValue("aaaa_{mtoJson1.$}_bbbb"));
//		logger.info($.variables().resolveValue("aaaa_{mtoJson1.$.name}_bbbb"));
//
//		logger.info($.variables().resolveUri("{mtoJson1}"));
//		logger.info($.variables().resolveUri("{mtoJson1.$}"));
//		logger.info($.variables().resolveUri("{mtoJson1.$.name}"));
//
//		logger.info($.variables().resolveValue("{mtoJson1}"));
//		logger.info($.variables().resolveValue("{mtoJson1.$}"));
//		logger.info($.variables().resolveValue("{mtoJson1.$.name}"));
		
		
		
		//////////////////////////////
		// POST(Create) -> link(O) json(O)
		//////////////////////////////
		$.POST("/baseEntities").H().C("name", "e1-post").C("manyToOneEntity", "{mtoLink1}").is2xx().andExpect("manyToOneEntity.name", "mto_a").andReturn("e1");
		$.POST("/baseEntities").H().C("name", "e2-post").C("manyToOneEntity", "{mtoJson1.$}").is2xx().andExpect("manyToOneEntity.name", "mto_c").andReturn("e2");
		
		
		//////////////////////////////
		// PUT(Create) -> link(O) json(O)
		//////////////////////////////
		$.PUT("{e1}").H().C("name", "e1-put").C("manyToOneEntity", "{mtoLink2}").is2xx().andExpect("manyToOneEntity.name", "mto_b");
		$.PUT("{e2}").H().C("name", "e2-put").C("manyToOneEntity", "{mtoJson2.$}").is2xx().andExpect("manyToOneEntity.name", "mto_d");
		
		
		////////////////////////////////////////
		// PATCH(Create) -> link(O) json(O)
		///////////////////////////////////////
		$.PATCH("{e1}").H().C("manyToOneEntity", "{mtoLink1}").is2xx().andExpect("name","e1-put").andExpect("manyToOneEntity.name", "mto_a");
		$.PATCH("{e1}").H().C("name", "e1-patch").is2xx().andExpect("name","e1-patch").andExpect("manyToOneEntity.name", "mto_a");

		$.PATCH("{e2}").H().C("manyToOneEntity", "{mtoJson1.$}").is2xx().andExpect("name","e2-put").andExpect("manyToOneEntity.name", "mto_c");
		$.PATCH("{e2}").H().C("name", "e2-patch").is2xx().andExpect("name","e2-patch").andExpect("manyToOneEntity.name", "mto_c");
		
		
		//////////////////////////////////////////
		// 
		//////////////////////////////////////////
		$.GET("{e1}").is2xx().andReturn("e1");
		$.GET("{e2}").is2xx().andReturn("e2");
		
		@SuppressWarnings({ "unchecked", "rawtypes" })
		Map<String,Object> b1 = (Map)$.variables().resolveValue("{e1.$}"); 
		b1.put("name", "hello");
		
		$.PUT("{e1}").H().C(b1).is2xx().andExpect("name", "hello");
	}
}