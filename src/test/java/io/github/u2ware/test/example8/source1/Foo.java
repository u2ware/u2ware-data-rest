package io.github.u2ware.test.example8.source1;

import java.util.Set;
import java.util.UUID;

import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;
import javax.persistence.Transient;

import org.springframework.data.rest.webmvc.support.EntityViewDeserializer;

import com.fasterxml.jackson.databind.annotation.JsonDeserialize;

import io.github.u2ware.test.example8.source2.Bar;
import lombok.Data;


@Entity @Table(name="example8_foo")
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
	
	
	@Transient
	@JsonDeserialize(using = EntityViewDeserializer.class)
	private Bar bar;
	
	@Transient
	@JsonDeserialize(contentUsing = EntityViewDeserializer.class)
	private Set<Bar> bars;
}
