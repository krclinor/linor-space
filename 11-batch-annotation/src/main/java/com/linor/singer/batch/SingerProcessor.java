package com.linor.singer.batch;

import org.springframework.batch.item.ItemProcessor;
import org.springframework.stereotype.Service;

import com.linor.singer.domain.Singer;

import lombok.extern.slf4j.Slf4j;

@Service
@Slf4j
public class SingerProcessor implements ItemProcessor<Singer, Singer> {

	@Override
	public Singer process(Singer item) throws Exception {
		String firstName = item.getFirstName();
		if("리노".equals(firstName)) {
			return null;
		}
		item.setFirstName(firstName + "님");
		log.info("{} 에서 {}로 변환됨.", firstName, item.getFirstName());
		return item;
	}
	

}
