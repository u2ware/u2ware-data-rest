package io.github.u2ware.data.test.otm1;

import java.util.Set;

import javax.persistence.CollectionTable;
import javax.persistence.ElementCollection;
import javax.persistence.Embeddable;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.Table;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Entity 
@Table(name = "otm2_BaseEntity")
@Data @Builder @AllArgsConstructor @NoArgsConstructor 
public class BaseEntity {

	@Id 
	@GeneratedValue
	private Long id;
	
	private String name;
	private Integer age;
	
	@ElementCollection
	@CollectionTable(name="otm2_BaseEntity_child", joinColumns=@JoinColumn(name="parent"))
	private Set<OneToManyEntity> otm;
	
	@Embeddable
	@Data @Builder @NoArgsConstructor @AllArgsConstructor
	public static class OneToManyEntity {
		
		private String name;
		private Integer age;
		
	}
	
}
