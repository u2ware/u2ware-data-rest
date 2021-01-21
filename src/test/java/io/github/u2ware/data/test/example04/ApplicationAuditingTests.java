package io.github.u2ware.data.test.example04;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.context.TestConfiguration;
import org.springframework.context.annotation.Import;
import org.springframework.data.jpa.repository.config.EnableJpaAuditing;
import org.springframework.test.web.servlet.MockMvc;

import io.github.u2ware.data.test.RestMockMvc;
import io.github.u2ware.data.test.example04.ApplicationAuditingTests.ApplicationAuditingTestsConfig;

@SpringBootTest
@Import(ApplicationAuditingTestsConfig.class)
@AutoConfigureMockMvc
public class ApplicationAuditingTests {


	////////////////////////////////////////////////////////////////////////////////////////
	// @EnableJpaAuditing  in configuration + @EntityListeners(AuditingEntityListener.class) in Entity Class
	////////////////////////////////////////////////////////////////////////////////////////
	@TestConfiguration
	@EnableJpaAuditing
	public static class ApplicationAuditingTestsConfig{
	}
	
	
	protected Log logger = LogFactory.getLog(getClass());
	
	protected @Autowired MockMvc mockMvc;
	protected @Autowired BaseEntityRepository baseEntityRepository;
	protected @Autowired ManyToOneEntityRepository manyToOneEntityRepository;

	@Test
	public void contextLoads() throws Exception{
		
		RestMockMvc $ = new RestMockMvc(mockMvc, "");
		$.GET("/profile").is2xx();

		ManyToOneEntity mtoJson1 = manyToOneEntityRepository.save(ManyToOneEntity.builder().name("mto_a").age(3).build());	
		ManyToOneEntity mtoJson2 = manyToOneEntityRepository.save(ManyToOneEntity.builder().name("mto_b").age(4).build());	
		
		$.POST("/baseEntities").C("name", "base1").C("insertedObject", mtoJson1).is2xx().andReturn("b1");
		$.GET("{b1}").is2xx().andExpect("insertedObject.name", mtoJson1.getName()).andExpect("updatedObject.name", null).andReturn("b1");
		Long t11 = $.variables().get("b1").path("insertedTime");
//		Long t12 = $.variables().get("b1").content("updatedTime");
		Thread.sleep(1000);
		
		$.POST("/baseEntities").H(ManyToOneEntityAware.AUDITING_HEADER, mtoJson2.getSeq()).C("name", "base1").C("insertedObject", mtoJson1).is2xx().andReturn("b2");
		$.GET("{b2}").is2xx().andExpect("insertedObject.name", mtoJson2.getName()).andExpect("updatedObject.name", mtoJson2.getName()).andReturn("b2");
//		Long t21 = $.variables().get("b2").content("insertedTime");
//		Long t22 = $.variables().get("b2").content("updatedTime");
		Thread.sleep(1000);
		
		$.PUT("{b1}").H(ManyToOneEntityAware.AUDITING_HEADER, mtoJson2.getSeq()).C("name", "base1111").is2xx().andReturn("b1");
		$.GET("{b1}").is2xx().andExpect("insertedObject.name", mtoJson1.getName()).andExpect("insertedTime", t11).andExpect("updatedObject.name", mtoJson2.getName());
		
		$.PUT("{b1}").H(ManyToOneEntityAware.AUDITING_HEADER, mtoJson1.getSeq()).C("name", "base11111111").is2xx().andReturn("b1");
		$.GET("{b1}").is2xx().andExpect("insertedObject.name", mtoJson1.getName()).andExpect("insertedTime", t11).andExpect("updatedObject.name", mtoJson1.getName());
	}	
}


