package com.linor.singer.config;

import java.time.LocalDate;
import java.util.HashSet;

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
	private SingerDao1 singerDao;
	
	@Override
	public void run(ApplicationArguments args) throws Exception {
		Instrument1 instrument = new Instrument1("기타");
		singerDao.insertInstrument(instrument);
		
		instrument = new Instrument1("피아노");
		singerDao.insertInstrument(instrument);

		instrument = new Instrument1("드럼");
		singerDao.insertInstrument(instrument);

		instrument = new Instrument1("신디사이저");
		singerDao.insertInstrument(instrument);

		Singer1 singer = Singer1.builder()
				.firstName("종서")
				.lastName("김")
				.birthDate(LocalDate.parse("1970-12-09"))
				.albums(new HashSet<Album1>())
				.instruments(new HashSet<Instrument1>())
				.build();
		
		Album1 album = Album1.builder()
				.title("아름다운 구속")
				.releaseDate(LocalDate.parse("2019-01-01"))
				.singer(singer)
				.build();
		
		singer.getAlbums().add(album);

		album = Album1.builder()
				.title("날개를 활짝펴고")
				.releaseDate(LocalDate.parse("2019-02-01"))
				.singer(singer)
				.build();
		
		singer.getAlbums().add(album);

		instrument = new Instrument1("기타");
		singer.getInstruments().add(instrument);
		instrument = new Instrument1("피아노");
		singer.getInstruments().add(instrument);
		
		singerDao.insertWithAlbum(singer);
		
		singer = Singer1.builder()
				.firstName("건모")
				.lastName("김")
				.birthDate(LocalDate.parse("1999-07-12"))
				.albums(new HashSet<Album1>())
				.instruments(new HashSet<Instrument1>())
				.build();
		
		album = Album1.builder()
				.title("황혼의 문턱")
				.releaseDate(LocalDate.parse("2019-03-01"))
				.singer(singer)
				.build();

		singer.getAlbums().add(album);

		instrument = new Instrument1();
		instrument.setInstrumentId("기타");
		singer.getInstruments().add(instrument);

		singerDao.insertWithAlbum(singer);

		singer = Singer1.builder()
				.firstName("용필")
				.lastName("조")
				.birthDate(LocalDate.parse("1978-06-28"))
				.albums(new HashSet<Album1>())
				.instruments(new HashSet<Instrument1>())
				.build();
		
		instrument = new Instrument1("드럼");
		singer.getInstruments().add(instrument);

		singerDao.insertWithAlbum(singer);
		
		singer = Singer1.builder()
				.firstName("진아")
				.lastName("태")
				.birthDate(LocalDate.parse("2000-11-01"))
				.build();
		singerDao.insertWithAlbum(singer);
	}

}
