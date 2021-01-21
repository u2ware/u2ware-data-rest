package io.github.u2ware.data.test.example03;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.Table;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Entity
@Table(name = "example03_foo")
public @Data @Builder @AllArgsConstructor @NoArgsConstructor class Foo {

	
	@Id @GeneratedValue
	private Long seq;
	private String name;
	private Integer age;
	private String address;
}
