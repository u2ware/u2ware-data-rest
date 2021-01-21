package io.github.u2ware.data.test.ext03;

import java.net.URI;
import java.util.UUID;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.Table;
import javax.persistence.Transient;

import org.hibernate.annotations.GenericGenerator;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Entity
@Table(name="test_entities_UUIDEntity")
@Data @AllArgsConstructor @NoArgsConstructor @Builder
public class UUIDEntity {

	@Id 
	@GeneratedValue(generator = "UUID") @GenericGenerator(name = "UUID", strategy = "org.hibernate.id.UUIDGenerator")
	private UUID id;
	
	private String stringValue;
	private Integer intValue;
	private URI uriValue;
	private Long longValue;

	@Transient
	private String param;
}
