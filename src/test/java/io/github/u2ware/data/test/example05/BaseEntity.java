package io.github.u2ware.data.test.example05;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.Table;
import javax.persistence.Transient;

import org.springframework.hateoas.EntityModel;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Entity 
@Table(name = "example05_BaseEntity")
@Data @Builder @AllArgsConstructor @NoArgsConstructor 
public class BaseEntity implements Processable{

	@Id 
	@GeneratedValue
	private Long id;
	
	private String name;

	private Integer age;
	
//	@ManyToOne
	@Transient
	private EntityModel<ManyToOneEntity> manyToOneEntity; 

	
	/*request: uri only. response:  link */
//	@JsonProperty(access =Access.READ_ONLY)
//	private EntityModel<ManyToOneEntity> manyToOneEntityResponse; /*request: uri only. response:  link */
	
}
