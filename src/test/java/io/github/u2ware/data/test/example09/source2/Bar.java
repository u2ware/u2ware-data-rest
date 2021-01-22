package io.github.u2ware.data.test.example09.source2;

import java.util.UUID;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;

import lombok.Data;

@Table(name="example09_bar")
@Entity
public @Data class Bar {

	@Id
	@Column(name = "id")
	private UUID id;
	
	private String name;

	private Integer age;
	
	public Bar() {
		
	}
	public Bar(String name) {
		
	}
	public Bar(UUID id, String name, Integer age) {
		this.id = id;
		this.name = name;
		this.age = age;
	}
	  

}
