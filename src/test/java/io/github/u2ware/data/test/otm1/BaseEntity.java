package io.github.u2ware.data.test.otm1;

import java.util.Set;

import javax.persistence.CollectionTable;
import javax.persistence.ElementCollection;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.OneToMany;
import javax.persistence.Table;

import org.springframework.data.rest.core.annotation.RestResource;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Entity 
@Table(name = "otm1_BaseEntity")
@Data @Builder @AllArgsConstructor @NoArgsConstructor 
public class BaseEntity {

	@Id 
	@GeneratedValue
	private Long id;
	
	private String name;

	private Integer age;
	
	@RestResource(exported = false)
	@OneToMany 
//	@CollectionTable(joinColumns=@JoinColumn(name="aaaaa"))
	private Set<OneToManyEntity> manyToOneEntities;
	
}
