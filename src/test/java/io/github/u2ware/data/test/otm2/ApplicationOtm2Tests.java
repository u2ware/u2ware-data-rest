package io.github.u2ware.data.test.otm2;

import java.util.HashSet;
import java.util.Set;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.web.servlet.MockMvc;

import io.github.u2ware.data.test.RestMockMvc;


@SpringBootTest
@AutoConfigureMockMvc
public class ApplicationOtm2Tests { 

	
	protected Log logger = LogFactory.getLog(getClass());
	
	protected @Autowired BaseEntityRepository baseEntityRepository;
	protected @Autowired MockMvc mockMvc;

	protected RestMockMvc $;

	@BeforeEach
	public void reset() {
		$ = new RestMockMvc(mockMvc, "");
	}
	
	private Set<Object> otm(String... names){
		Set<Object> otm = new HashSet<>();
		for(String name : names) {
			otm.add($.variables().resolveValue("{"+name+".$}"));
		}
		return otm;
	}
	private Set<Object> otmLink(String... names){
		Set<Object> otm = new HashSet<>();
		for(String name : names) {
			otm.add($.variables().resolveUri("{"+name+"}"));
		}
		return otm;
	}
	

	@Test
	public void oneToManyTests() throws Exception {
		
		$.POST("/otherEntities").H().C("name", "a").is2xx().andReturn("a");
		$.POST("/otherEntities").H().C("name", "b").is2xx().andReturn("b");
		$.POST("/otherEntities").H().C("name", "c").is2xx().andReturn("c");
		$.POST("/otherEntities").H().C("name", "d").is2xx().andReturn("d");

		
		//////////////////////////////
		// POST(Create) -> link(O) json(O)
		//////////////////////////////
		$.POST("/baseEntities").H().C("name", "e1-post").C("otm", otm("a", "b")).is2xx().andExpect("otm[0].name", "b").andReturn("e1");
		$.POST("/baseEntities").H().C("name", "e2-post").C("otm", otmLink("c", "d")).is2xx().andExpect("otm[0].name", "d").andReturn("e2");

		//////////////////////////////
		// PUT (Bypass... No Changed...)
		//////////////////////////////
		$.GET("{e1}").H().is2xx().andReturn("i");
		$.PUT("{e1}").H().C("{i.$}").is2xx();
		$.GET("{e1}").H().is2xx().andExpect("otm[0].name", "b");

		
		//////////////////////////////
		// PUT(Create) -> link(O) json(O)
		//////////////////////////////
		$.PUT("{e1}").H().C("name", "e1-put").C("otm", otmLink("c", "d")).is2xx().andExpect("otm[0].name", "d");
		$.PUT("{e2}").H().C("name", "e2-put").C("otm", otm("a", "b")).is2xx().andExpect("otm[0].name", "b");
		
		
		////////////////////////////////////////
		// PATCH(Create) -> link(O) json(O)
		///////////////////////////////////////
		$.PATCH("{e1}").H().C("otm", otm("a")).is2xx().andExpect("name","e1-put").andExpect("otm[0].name", "a");
		$.PATCH("{e1}").H().C("name", "e1-patch").is2xx().andExpect("name","e1-patch").andExpect("otm[0].name", "a");

		$.PATCH("{e2}").H().C("otm", otmLink("c")).is2xx().andExpect("name","e2-put").andExpect("otm[0].name", "c");
		$.PATCH("{e2}").H().C("name", "e2-patch").is2xx().andExpect("name","e2-patch").andExpect("otm[0].name", "c");
		
		
		//////////////////////////////////////////
		// DELETE
		//////////////////////////////////////////
		$.DELETE("{e1}").is2xx();
		$.DELETE("{e2}").is2xx();
		
	}
}