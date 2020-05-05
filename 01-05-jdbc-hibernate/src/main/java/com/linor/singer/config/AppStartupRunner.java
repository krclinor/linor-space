package com.linor.singer.config;

import java.time.LocalDate;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.ApplicationArguments;
import org.springframework.boot.ApplicationRunner;
import org.springframework.context.annotation.Profile;
import org.springframework.stereotype.Component;

import com.linor.singer.dao.SingerDao;
import com.linor.singer.domain.Album;
import com.linor.singer.domain.Instrument;
import com.linor.singer.domain.Singer;

//@Profile("dev")
//@Component
public class AppStartupRunner implements ApplicationRunner {
	@Autowired
	private SingerDao singerDao;
	
	@Override
	public void run(ApplicationArguments args) throws Exception {
		Instrument instrument = new Instrument();
		instrument.setInstrumentId("기타");
		singerDao.insert(instrument);
		
		instrument = new Instrument();
		instrument.setInstrumentId("피아노");
		singerDao.insert(instrument);

		instrument = new Instrument();
		instrument.setInstrumentId("드럼");
		singerDao.insert(instrument);

		instrument = new Instrument();
		instrument.setInstrumentId("신디사이저");
		singerDao.insert(instrument);

		Singer singer = new Singer();
		singer.setFirstName("종서");
		singer.setLastName("김");
		singer.setBirthDate(LocalDate.parse("1970-12-09"));
		
		Album album = new Album();
		album.setTitle("아름다운 구속");
		album.setReleaseDate(LocalDate.parse("2019-01-01"));
		singer.addAlbum(album);

		album = new Album();
		album.setTitle("날개를 활짝펴고");
		album.setReleaseDate(LocalDate.parse("2019-02-01"));
		singer.addAlbum(album);
		
		instrument = new Instrument();
		instrument.setInstrumentId("기타");
		singer.addInstrument(instrument);
		instrument = new Instrument();
		instrument.setInstrumentId("피아노");
		singer.addInstrument(instrument);
		
		singerDao.insertWithAlbum(singer);
		
		singer = new Singer();
		singer.setFirstName("건모");
		singer.setLastName("김");
		singer.setBirthDate(LocalDate.parse("1999-07-12"));
		
		album = new Album();
		album.setTitle("황혼의 문턱");
		album.setReleaseDate(LocalDate.parse("2019-03-01"));
		singer.addAlbum(album);

		instrument = new Instrument();
		instrument.setInstrumentId("기타");
		singer.addInstrument(instrument);

		singerDao.insertWithAlbum(singer);

		singer = new Singer();
		singer.setFirstName("용필");
		singer.setLastName("조");
		singer.setBirthDate(LocalDate.parse("1978-06-28"));
		
		instrument = new Instrument();
		instrument.setInstrumentId("드럼");
		singer.addInstrument(instrument);

		singerDao.insertWithAlbum(singer);
	}

}
