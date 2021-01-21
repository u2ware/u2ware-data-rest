package io.github.u2ware.data.test.mto3;

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
@Table(name = "manytoone1_ManyToOneEntity")
//@EqualsAndHashCode(callSuper = false) @ToString
@Data  @Builder @NoArgsConstructor @AllArgsConstructor
public class ManyToOneEntity {// extends RepresentationModel<ManyToOneEntity>{

	@Id @GeneratedValue
	private @Setter Long seq;
	private @Setter @Getter String name;
	private @Setter @Getter Integer age;
	
//	public ManyToOneEntity(String uri) {
//		System.err.println("2222222 : "+this.hashCode());
//		try {
//			String last = UriComponentsBuilder.fromUriString(uri).build().getPathSegments().stream().reduce((first, second) -> second).orElse(null);
//			this.seq = Long.parseLong(last);
//		}catch(Exception e) {
//		}
//	}

	
//	@Override
//	public ManyToOneEntity add(Link link) {
//		System.out.println(link);
//		System.out.println(link);
//		System.out.println(link);
//		return super.add(link);
//	}
	
//	public Long getSeq() {
//		if(seq != null) return seq;
//		if(hasLink("self")) {
//			Link self = getRequiredLink("self");
////			String[] parts = self.toUri().getPath().split("/");
////			String text = parts[parts.length - 1];
//			String text = UriComponentsBuilder.fromUri(self.toUri()).build().getPathSegments().stream().reduce((first, second) -> second).orElse(null);
//			this.seq = Long.parseLong(text);
//		}
//		return seq;
//	}
}