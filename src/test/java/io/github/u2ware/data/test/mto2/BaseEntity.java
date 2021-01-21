package io.github.u2ware.data.test.mto2;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.ManyToOne;
import javax.persistence.Table;

import org.springframework.data.rest.core.annotation.RestResource;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Entity 
@Table(name = "mto2_BaseEntity")
@Data @Builder @AllArgsConstructor @NoArgsConstructor 
public class BaseEntity {

	@Id 
	@GeneratedValue
	private Long id;
	
	private String name;

	private Integer age;
	
	@ManyToOne
//	@JoinColumn(foreignKey=@ForeignKey(value = ConstraintMode.NO_CONSTRAINT))
	@RestResource(exported = false)
	private ManyToOneEntity manyToOneEntity;
}
