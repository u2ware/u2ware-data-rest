package io.github.u2ware.data.test.mto1;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.Table;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Entity
@Table(name = "mto1_ManyToOneEntity")
@Data @Builder @AllArgsConstructor @NoArgsConstructor
public class ManyToOneEntity {

	@Id @GeneratedValue
	private Long seq;
	
	private String name;

	private Integer age;

	
//	public ManyToOneEntity() {
//		System.err.println("1111111 : "+this.hashCode());
//	}
//	
//	public ManyToOneEntity(String uri) {
//		System.err.println("2222222 : "+this.hashCode());
//		try {
////			URI source =  UriComponentsBuilder.fromUriString(uri).build().toUri();
////			ManyToOneEntity a = SpringDataRestUtils.conversionService().convert(source, ManyToOneEntity.class);
////			this.seq = a.getSeq();
//			String last = UriComponentsBuilder.fromUriString(uri).build().getPathSegments().stream().reduce((first, second) -> second).orElse(null);
//			this.seq = Long.parseLong(last);
//		}catch(Exception e) {
//		}
//	}

}
