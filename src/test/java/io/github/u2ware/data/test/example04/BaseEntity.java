package io.github.u2ware.data.test.example04;

import javax.persistence.Column;
import javax.persistence.ConstraintMode;
import javax.persistence.Entity;
import javax.persistence.EntityListeners;
import javax.persistence.ForeignKey;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.Table;

import org.springframework.data.annotation.CreatedBy;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.LastModifiedBy;
import org.springframework.data.annotation.LastModifiedDate;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;
import org.springframework.data.rest.core.annotation.RestResource;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Entity 
@Table(name = "example04_BaseEntity")
@EntityListeners(AuditingEntityListener.class)
@Data @Builder @AllArgsConstructor @NoArgsConstructor 
public class BaseEntity {

	@Id 
	@GeneratedValue
	private Long id;
	
	private String name;

	private Integer age;
	
	@CreatedBy
	@ManyToOne
	@JoinColumn(name="insertedObject" , insertable = true, updatable = false, foreignKey=/* Not exists*/@ForeignKey(value=ConstraintMode.NO_CONSTRAINT))
	@RestResource(exported=false)
	private ManyToOneEntity insertedObject;
	
	@CreatedDate
	@Column(insertable = true, updatable = false)
	private Long insertedTime;

	@LastModifiedBy
	@ManyToOne
	@JoinColumn(name="updatedObject" , insertable = true, updatable = true, foreignKey=/* Not exists*/@ForeignKey(value=ConstraintMode.NO_CONSTRAINT))
	@RestResource(exported=false)
	private ManyToOneEntity updatedObject;
	
	@LastModifiedDate
	private Long updatedTime;
	
}

