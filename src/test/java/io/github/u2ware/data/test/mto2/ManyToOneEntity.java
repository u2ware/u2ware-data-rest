package io.github.u2ware.data.test.mto2;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.Table;
import javax.persistence.Transient;

import org.springframework.web.util.UriComponentsBuilder;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Entity
@Table(name = "mto2_ManyToOneEntity")
@Data @Builder @AllArgsConstructor @NoArgsConstructor
public class ManyToOneEntity {

	//또는 RepositoryRestConfiguration.exposeIdsFor(true)  PUT 지원 
	public ManyToOneEntity(String uri) {
		System.err.println("2222222 : "+this.hashCode());
		try {
			String last = UriComponentsBuilder.fromUriString(uri).build().getPathSegments().stream().reduce((first, second) -> second).orElse(null);
			this.seq = Long.parseLong(last);
		}catch(Exception e) {
		}
	}
	
	
	@Id @GeneratedValue
	private Long seq;
	
	private String name;

	private Integer age;

	@Transient
	private Long oops;


}
