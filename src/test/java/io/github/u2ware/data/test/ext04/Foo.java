package io.github.u2ware.data.test.ext04;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.PreUpdate;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Entity
public @Data @Builder @AllArgsConstructor @NoArgsConstructor class Foo {

	
	@Id @GeneratedValue
	private Long seq;
	private String name;
	private Integer age;
	private String address;
	
	
//	@javax.persistence.Transient
//	private Bar bar;
	
	@PreUpdate
	public void a() {
		System.err.println(this);
	}
	
}
