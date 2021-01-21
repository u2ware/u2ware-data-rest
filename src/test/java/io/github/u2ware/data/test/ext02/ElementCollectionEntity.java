package io.github.u2ware.data.test.ext02;

import java.util.Set;

import javax.persistence.ElementCollection;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.Table;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Entity
@Table(name="test_entities_ElementCollectionEntity")
public @Data @Builder @AllArgsConstructor @NoArgsConstructor class ElementCollectionEntity {

	
	@Id @GeneratedValue
	private Long seq;
	private String name;
	private Integer age;
	private String address;
	
	@ElementCollection
	private Set<String> phoneNumbers;
}
