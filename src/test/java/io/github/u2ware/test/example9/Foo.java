package io.github.u2ware.test.example9;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.Table;

import com.fasterxml.jackson.annotation.JsonIgnore;

import lombok.Data;

@Entity @Table(name="example9_foo")
public @Data class Foo {


	@Id @GeneratedValue
	private Long seq;
	
	private String name;
	private Integer age;
	@JsonIgnore private String oops;


	public Foo() {
	}
	public Foo(String name, Integer age) {
		this.name = name;
		this.age = age;
	}

}
