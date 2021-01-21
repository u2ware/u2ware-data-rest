package io.github.u2ware.data.test.example09.source1;

import java.util.UUID;

import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;

import lombok.Data;


@Entity @Table(name="example09_foo")
public @Data class Foo {

	@Id
	private UUID id;
	
	private String name;

	private Integer age;
	
	public Foo() {
		
	}
	public Foo(UUID id, String name, Integer age) {
		this.id = id;
		this.name = name;
		this.age = age;
	}
	
	
}
