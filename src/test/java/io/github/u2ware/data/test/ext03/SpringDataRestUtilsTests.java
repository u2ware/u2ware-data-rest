package io.github.u2ware.data.test.ext03;

import java.net.URI;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;


	
@SpringBootTest
public class SpringDataRestUtilsTests {

	protected Log logger = LogFactory.getLog(getClass());

	private @Autowired UUIDEntityRepository uuidEntityRepository;
	
	@Test
	public void contextLoads() throws Exception{

		/////////////////////////////
		//
		/////////////////////////////
		UUIDEntity uuidEntity1 = uuidEntityRepository.save(UUIDEntity.builder().stringValue("a").build());

		URI uri = SpringDataRestUtils.entityToUri(uuidEntity1);
		UUIDEntity uuidEntity2 = SpringDataRestUtils.uriToEntity(uri);
		Assertions.assertEquals(uuidEntity1.getId(), uuidEntity2.getId());
		Assertions.assertEquals(uuidEntity1.getStringValue(), uuidEntity2.getStringValue());
		
		UUIDEntity uuidEntity3 = SpringDataRestUtils.uriToEntity(uri, false);
		Assertions.assertEquals(uuidEntity1.getId(), uuidEntity3.getId());
		Assertions.assertEquals(null, uuidEntity3.getStringValue());
	}
	
	
}
