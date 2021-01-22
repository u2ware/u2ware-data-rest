package io.github.u2ware.data.test.otm1;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.Table;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Entity
@Table(name = "otm1_OneToManyEntity")
@Data @Builder @NoArgsConstructor @AllArgsConstructor
public class OneToManyEntity {
	
	@Id @GeneratedValue
	private @Setter Long seq;
	private @Setter @Getter String name;
	private @Setter @Getter Integer age;
	
}