package com.linor.singer.domain;

import java.time.LocalDateTime;

import javax.persistence.EntityListeners;
import javax.persistence.MappedSuperclass;

import org.springframework.data.annotation.CreatedBy;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.LastModifiedBy;
import org.springframework.data.annotation.LastModifiedDate;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

import lombok.Data;

@MappedSuperclass
@EntityListeners(AuditingEntityListener.class)
@Data
public abstract class Auditable<U> {

	@CreatedBy
	protected U createdBy;
	
	@CreatedDate
	protected LocalDateTime createdDate;
	
	@LastModifiedBy
	protected U lastModifiedBy;
	
	@LastModifiedDate
	protected LocalDateTime lastModifiedDate;
}
