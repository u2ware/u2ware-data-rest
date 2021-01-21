package io.github.u2ware.data.test.example06;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.Table;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Entity
@Table(name = "example06_ManyToOneEntity")
@Data @Builder @AllArgsConstructor @NoArgsConstructor
public class ManyToOneEntity {

	@Id @GeneratedValue
	private Long seq;
	
	private String name;

	private Integer age;

	

}
