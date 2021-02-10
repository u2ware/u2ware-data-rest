package io.github.u2ware.data.test.otm1;

import java.util.HashSet;
import java.util.Set;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.web.servlet.MockMvc;

import io.github.u2ware.data.test.RestMockMvc;
import io.github.u2ware.data.test.otm1.BaseEntity.OneToManyEntity;


@SpringBootTest
@AutoConfigureMockMvc
public class ApplicationOtm1Tests { 

	
	protected Log logger = LogFactory.getLog(getClass());
	
	protected @Autowired BaseEntityRepository baseEntityRepository;
	protected @Autowired MockMvc mockMvc;
	

	private Set<OneToManyEntity> otm(String... names){
		Set<OneToManyEntity> otm = new HashSet<>();
		for(String name : names) {
			otm.add(OneToManyEntity.builder().name(name).build());
		}
		return otm;
	}
	
	@Test
	public void manyToOneTest() throws Exception {
		RestMockMvc $ = new RestMockMvc(mockMvc, "");
		
		//////////////////////////////
		// POST(Create) 
		//////////////////////////////
		$.POST("/baseEntities").H().C("name", "e1-post").C("otm", otm("a", "b")).is2xx().andExpect("otm[0].name", "a").andReturn("e1");

		//////////////////////////////
		// PUT (Bypass... All Delete , All Insert)
		//////////////////////////////
		$.GET("{e1}").H().is2xx().andReturn("i");
		$.PUT("{e1}").H().C($.variables().resolveValue("{i.$}")).is2xx();
		$.GET("{e1}").H().is2xx().andExpect("otm[0].name", "a");
		
		
		//////////////////////////////
		// PUT(Create) 
		//////////////////////////////
		$.PUT("{e1}").H().C("name", "e1-put").C("otm", otm("c","d","e")).is2xx().andExpect("otm[0].name", "c");
	
		
		
		////////////////////////////////////////
		// PATCH(Create) 
		///////////////////////////////////////
		$.PATCH("{e1}").H().C("otm", otm("f")).is2xx().andExpect("otm[0].name", "f").andExpect("name", "e1-put");
		
		
		//////////////////////////////////////////
		// DELETE
		//////////////////////////////////////////
		$.DELETE("{e1}").is2xx();
	}
}