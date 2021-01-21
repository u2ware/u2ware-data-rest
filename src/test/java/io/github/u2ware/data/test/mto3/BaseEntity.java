package io.github.u2ware.data.test.mto3;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.ManyToOne;
import javax.persistence.Table;

import org.springframework.data.rest.core.annotation.RestResource;
import org.springframework.hateoas.Link;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonProperty.Access;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Entity 
@Table(name = "mto3_BaseEntity")
@Data @Builder @AllArgsConstructor @NoArgsConstructor 
public class BaseEntity {

	@Id 
	@GeneratedValue
	private Long id;
	
	private String name;

	private Integer age;
	
	@ManyToOne @RestResource(exported = false)
	@JsonProperty(access = Access.READ_ONLY, value = "manyToOneEntity")
	private ManyToOneEntity manyToOneEntityRef;
	
//	@Convert(converter= EntityLinkConverter.class)
//	@JsonDeserialize(using=EntityLinkDeserializer.class)
	@JsonProperty(access = Access.WRITE_ONLY, value = "manyToOneEntity")
	private Link manyToOneEntity;
	
}
