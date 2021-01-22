package io.github.u2ware.data.test.mto3;

import java.io.IOException;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.boot.jackson.JsonComponent;
import org.springframework.hateoas.Link;

import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.core.JsonToken;
import com.fasterxml.jackson.databind.DeserializationContext;
import com.fasterxml.jackson.databind.JsonDeserializer;
import com.fasterxml.jackson.databind.JsonNode;

@JsonComponent
public class LinkJsonDeserializer extends JsonDeserializer<Link> {

	protected Log logger = LogFactory.getLog(getClass());

	@Override
	public Link deserialize(JsonParser p, DeserializationContext ctxt)  throws IOException, JsonProcessingException {
		try {
			if (p.getCurrentToken() == JsonToken.START_OBJECT) {
				JsonNode node = p.getCodec().readTree(p);
				String link = node.get("_links").get("self").get("href").asText();
				logger.info("link: "+link);
				return Link.of(link).withSelfRel();
			}else {
				String link = p.getValueAsString();
				logger.info("text: "+link);
				return Link.of(link).withSelfRel();
			}
		}catch(Exception e) {
			logger.info("error: "+e);
			return null;//Link.of(".").withSelfRel();
		}
	}
}