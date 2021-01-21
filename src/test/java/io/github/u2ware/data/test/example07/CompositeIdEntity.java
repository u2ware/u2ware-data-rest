package io.github.u2ware.data.test.example07;

import java.io.Serializable;
import java.net.URI;

import javax.persistence.Embeddable;
import javax.persistence.EmbeddedId;
import javax.persistence.Entity;
import javax.persistence.Table;
import javax.persistence.Transient;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Entity
@Table(name="example07_CompositeEntity")
@Data @Builder @AllArgsConstructor @NoArgsConstructor
public class CompositeIdEntity {

	
	@EmbeddedId
	private ID id;
	
	private String stringValue;
	private Integer intValue;
	private URI uriValue;
	private Long longValue;
	
	@Transient
	private String param;

	
	@SuppressWarnings("serial")
	@Embeddable
	@Data @Builder @AllArgsConstructor @NoArgsConstructor
	public static class ID implements Serializable{
		private String key1;
		private String key2;
		
		public String toString() {
			return key1+"#"+key2;
		}
		
	}
}
