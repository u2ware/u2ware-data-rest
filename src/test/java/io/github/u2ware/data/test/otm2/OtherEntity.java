package io.github.u2ware.data.test.otm2;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.Table;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Entity 
@Table(name = "otm2_OtherEntity")
@Data @Builder @AllArgsConstructor @NoArgsConstructor 
public class OtherEntity {

	@Id 
	@GeneratedValue
	private Long seq;
	
	private String name;
	private Integer age;

	
	public OtherEntity(String link) {
		System.err.println(link);
	}
}
