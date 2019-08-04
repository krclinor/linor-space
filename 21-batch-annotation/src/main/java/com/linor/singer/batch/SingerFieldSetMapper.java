package com.linor.singer.batch;

import java.time.LocalDate;

import org.springframework.batch.item.file.mapping.FieldSetMapper;
import org.springframework.batch.item.file.transform.FieldSet;
import org.springframework.validation.BindException;

import com.linor.singer.domain.Singer;

public class SingerFieldSetMapper implements FieldSetMapper<Singer> {

	@Override
	public Singer mapFieldSet(FieldSet fieldSet) throws BindException {
		return Singer.builder()
//				.id(fieldSet.readInt(0))
				.firstName(fieldSet.readString(1))
				.lastName(fieldSet.readString(2))
				.birthDate(LocalDate.parse(fieldSet.readString(3)))
				.build();
	}

}
