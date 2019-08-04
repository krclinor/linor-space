package com.linor.singer.batch;

import java.util.List;

import org.springframework.batch.item.ItemWriter;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.linor.singer.dao.SingerDao;
import com.linor.singer.domain.Singer;

import lombok.extern.slf4j.Slf4j;

@Service
@Slf4j
public class SingerDbWriter implements ItemWriter<Singer> {

	@Autowired
	private SingerDao singerDao;
	
	@Override
	public void write(List<? extends Singer> items) throws Exception {
		log.info("저장될 가수: {}", items);
		items.forEach(singer -> {
			singerDao.insert(singer);
			log.info("저당된 가수: {}", singer.toString());
		});
	}

}
