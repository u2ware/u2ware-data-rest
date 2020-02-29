package io.github.u2ware.test.example1;

import java.net.URI;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.Table;
import javax.persistence.Transient;

import lombok.Data;

@Table(name="example1_foo")
@Entity
public @Data class Foo {

	@Id @GeneratedValue
	private Long seq;
	
	private String name;

	private Integer age;

	private URI uriValue;
	
	private Long longValue;
	
	public Foo() {}
	public Foo(String name, Integer age) {
		this.name = name;
		this.age = age;
	}
	
	@Transient
	private String _name;

}
