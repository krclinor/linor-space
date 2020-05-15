package com.linor.singer.config;

import java.time.LocalDate;
import java.util.HashSet;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.ApplicationArguments;
import org.springframework.boot.ApplicationRunner;
import org.springframework.context.annotation.Profile;
import org.springframework.stereotype.Component;

import com.linor.singer.dao.SingerDao;
import com.linor.singer.domain.Album;
import com.linor.singer.domain.Instrument;
import com.linor.singer.domain.Singer;

@Profile("dev")
@Component
public class AppStartupRunner implements ApplicationRunner {
	@Autowired
	private SingerDao singerDao;
	
	@Override
	public void run(ApplicationArguments args) throws Exception {
		Instrument instrument = new Instrument("기타");
		singerDao.insertInstrument(instrument);
		
		instrument = new Instrument("피아노");
		singerDao.insertInstrument(instrument);

		instrument = new Instrument("드럼");
		singerDao.insertInstrument(instrument);

		instrument = new Instrument("신디사이저");
		singerDao.insertInstrument(instrument);

		Singer singer = Singer.builder()
				.firstName("종서")
				.lastName("김")
				.birthDate(LocalDate.parse("1970-12-09"))
				.albums(new HashSet<Album>())
				.instruments(new HashSet<Instrument>())
				.build();
		
		Album album = Album.builder()
				.title("아름다운 구속")
				.releaseDate(LocalDate.parse("2019-01-01"))
				.singer(singer)
				.build();
		
		singer.getAlbums().add(album);

		album = Album.builder()
				.title("날개를 활짝펴고")
				.releaseDate(LocalDate.parse("2019-02-01"))
				.singer(singer)
				.build();
		
		singer.getAlbums().add(album);

		instrument = new Instrument("기타");
		singer.getInstruments().add(instrument);
		instrument = new Instrument("피아노");
		singer.getInstruments().add(instrument);
		
		singerDao.insertWithAlbum(singer);
		
		singer = Singer.builder()
				.firstName("건모")
				.lastName("김")
				.birthDate(LocalDate.parse("1999-07-12"))
				.albums(new HashSet<Album>())
				.instruments(new HashSet<Instrument>())
				.build();
		
		album = Album.builder()
				.title("황혼의 문턱")
				.releaseDate(LocalDate.parse("2019-03-01"))
				.singer(singer)
				.build();

		singer.getAlbums().add(album);

		instrument = new Instrument();
		instrument.setInstrumentId("기타");
		singer.getInstruments().add(instrument);

		singerDao.insertWithAlbum(singer);

		singer = Singer.builder()
				.firstName("용필")
				.lastName("조")
				.birthDate(LocalDate.parse("1978-06-28"))
				.albums(new HashSet<Album>())
				.instruments(new HashSet<Instrument>())
				.build();
		
		instrument = new Instrument("드럼");
		singer.getInstruments().add(instrument);

		singerDao.insertWithAlbum(singer);
		
		singer = Singer.builder()
				.firstName("진아")
				.lastName("태")
				.birthDate(LocalDate.parse("2000-11-01"))
				.build();
		singerDao.insertWithAlbum(singer);
	}

}
