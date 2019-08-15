package com.linor.singer.config;

import java.time.LocalDate;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.ApplicationArguments;
import org.springframework.boot.ApplicationRunner;
import org.springframework.context.annotation.Profile;
import org.springframework.stereotype.Component;

import com.linor.singer.dao1.SingerDao1;
import com.linor.singer.domain1.Album1;
import com.linor.singer.domain1.Instrument1;
import com.linor.singer.domain1.Singer1;

@Profile("dev")
@Component
public class AppStartupRunner1 implements ApplicationRunner {
	@Autowired
	private SingerDao1 singerDao1;
	
	@Override
	public void run(ApplicationArguments args) throws Exception {
		Instrument1 instrument1 = new Instrument1();
		instrument1.setInstrumentId("기타");
		singerDao1.insert(instrument1);
		
		instrument1 = new Instrument1();
		instrument1.setInstrumentId("피아노");
		singerDao1.insert(instrument1);

		instrument1 = new Instrument1();
		instrument1.setInstrumentId("드럼");
		singerDao1.insert(instrument1);

		instrument1 = new Instrument1();
		instrument1.setInstrumentId("신디사이저");
		singerDao1.insert(instrument1);

		Singer1 singer1 = new Singer1();
		singer1.setFirstName("종서");
		singer1.setLastName("김");
		singer1.setBirthDate(LocalDate.parse("1970-12-09"));
		
		Album1 album1 = new Album1();
		album1.setTitle("아름다운 구속");
		album1.setReleaseDate(LocalDate.parse("2019-01-01"));
		singer1.addAlbum(album1);

		album1 = new Album1();
		album1.setTitle("날개를 활짝펴고");
		album1.setReleaseDate(LocalDate.parse("2019-02-01"));
		singer1.addAlbum(album1);
		
		instrument1 = new Instrument1();
		instrument1.setInstrumentId("기타");
		singer1.addInstrument(instrument1);
		instrument1 = new Instrument1();
		instrument1.setInstrumentId("피아노");
		singer1.addInstrument(instrument1);
		
		singerDao1.insertWithAlbum(singer1);
		
		singer1 = new Singer1();
		singer1.setFirstName("건모");
		singer1.setLastName("김");
		singer1.setBirthDate(LocalDate.parse("1999-07-12"));
		
		album1 = new Album1();
		album1.setTitle("황혼의 문턱");
		album1.setReleaseDate(LocalDate.parse("2019-03-01"));
		singer1.addAlbum(album1);

		instrument1 = new Instrument1();
		instrument1.setInstrumentId("기타");
		singer1.addInstrument(instrument1);

		singerDao1.insertWithAlbum(singer1);

		singer1 = new Singer1();
		singer1.setFirstName("용필");
		singer1.setLastName("조");
		singer1.setBirthDate(LocalDate.parse("1978-06-28"));
		
		instrument1 = new Instrument1();
		instrument1.setInstrumentId("드럼");
		singer1.addInstrument(instrument1);

		singerDao1.insertWithAlbum(singer1);
	}

}
