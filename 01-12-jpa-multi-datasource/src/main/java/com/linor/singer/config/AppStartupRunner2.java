package com.linor.singer.config;

import java.time.LocalDate;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.ApplicationArguments;
import org.springframework.boot.ApplicationRunner;
import org.springframework.context.annotation.Profile;
import org.springframework.stereotype.Component;

import com.linor.singer.dao2.SingerDao2;
import com.linor.singer.domain2.Album2;
import com.linor.singer.domain2.Instrument2;
import com.linor.singer.domain2.Singer2;

@Profile("dev")
@Component
public class AppStartupRunner2 implements ApplicationRunner {
	@Autowired
	private SingerDao2 singerDao2;
	
	@Override
	public void run(ApplicationArguments args) throws Exception {
		Instrument2 instrument2 = new Instrument2();
		instrument2.setInstrumentId("기타");
		singerDao2.insert(instrument2);
		
		instrument2 = new Instrument2();
		instrument2.setInstrumentId("피아노");
		singerDao2.insert(instrument2);

		instrument2 = new Instrument2();
		instrument2.setInstrumentId("드럼");
		singerDao2.insert(instrument2);

		instrument2 = new Instrument2();
		instrument2.setInstrumentId("신디사이저");
		singerDao2.insert(instrument2);

		Singer2 singer2 = new Singer2();
		singer2.setFirstName("종서");
		singer2.setLastName("김");
		singer2.setBirthDate(LocalDate.parse("1970-12-09"));
		
		Album2 album2 = new Album2();
		album2.setTitle("아름다운 구속");
		album2.setReleaseDate(LocalDate.parse("2019-01-01"));
		singer2.addAlbum(album2);

		album2 = new Album2();
		album2.setTitle("날개를 활짝펴고");
		album2.setReleaseDate(LocalDate.parse("2019-02-01"));
		singer2.addAlbum(album2);
		
		instrument2 = new Instrument2();
		instrument2.setInstrumentId("기타");
		singer2.addInstrument(instrument2);
		instrument2 = new Instrument2();
		instrument2.setInstrumentId("피아노");
		singer2.addInstrument(instrument2);
		
		singerDao2.insertWithAlbum(singer2);
		
		singer2 = new Singer2();
		singer2.setFirstName("건모");
		singer2.setLastName("김");
		singer2.setBirthDate(LocalDate.parse("1999-07-12"));
		
		album2 = new Album2();
		album2.setTitle("황혼의 문턱");
		album2.setReleaseDate(LocalDate.parse("2019-03-01"));
		singer2.addAlbum(album2);

		instrument2 = new Instrument2();
		instrument2.setInstrumentId("기타");
		singer2.addInstrument(instrument2);

		singerDao2.insertWithAlbum(singer2);

		singer2 = new Singer2();
		singer2.setFirstName("용필");
		singer2.setLastName("조");
		singer2.setBirthDate(LocalDate.parse("1978-06-28"));
		
		instrument2 = new Instrument2();
		instrument2.setInstrumentId("드럼");
		singer2.addInstrument(instrument2);

		singerDao2.insertWithAlbum(singer2);
	}

}
