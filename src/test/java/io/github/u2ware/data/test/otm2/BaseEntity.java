package io.github.u2ware.data.test.otm2;

import java.util.HashSet;
import java.util.Set;

import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.JoinTable;
import javax.persistence.ManyToMany;
import javax.persistence.Table;

import org.springframework.data.rest.core.annotation.RestResource;
import org.springframework.hateoas.Link;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonProperty.Access;

import lombok.Data;

@Entity 
@Table(name = "otm2_BaseEntity")
public @Data class BaseEntity {

	@Id 
	@GeneratedValue
	private Long id;
	
	private String name;
	private Integer age;
	
	
	@ManyToMany(fetch=FetchType.EAGER)
	@JoinTable
	@RestResource(exported = false)
	@JsonProperty(access = Access.READ_ONLY, value = "otm")
	private Set<OtherEntity> otmRef = new HashSet<>();
	
//	@Convert(converter=LinkSetAttributeConverter.class)
//	@JsonDeserialize(using=EntityLinkDeserializer.class)
	@JsonProperty(access = Access.WRITE_ONLY, value = "otm")
	private Set<Link> otm;
}
