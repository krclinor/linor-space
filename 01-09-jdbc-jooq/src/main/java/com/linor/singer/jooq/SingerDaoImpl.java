package com.linor.singer.jooq;

import static com.linor.jooq.model.tables.Album.ALBUM;
import static com.linor.jooq.model.tables.Instrument.INSTRUMENT;
import static com.linor.jooq.model.tables.Singer.SINGER_;
import static org.jooq.impl.DSL.concat;
import static org.jooq.impl.DSL.max;
import static org.jooq.impl.DSL.select;

import java.sql.Date;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;

import org.jooq.DSLContext;
import org.jooq.Record;
import org.jooq.Result;
import org.jooq.impl.DSL;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import com.linor.jooq.model.tables.records.AlbumRecord;
import com.linor.jooq.model.tables.records.SingerRecord;
import com.linor.singer.dao.SingerDao;
import com.linor.singer.domain.Album;
import com.linor.singer.domain.Instrument;
import com.linor.singer.domain.Singer;
import com.linor.singer.domain.SingerSummary;

import lombok.RequiredArgsConstructor;

@Repository
@Transactional
@RequiredArgsConstructor
public class SingerDaoImpl implements SingerDao {
	
	private final DSLContext dsl;

	private Singer getSingerDomain(Record record) {
		return Singer.builder()
				.id(record.getValue(SINGER_.ID))
				.firstName(record.getValue(SINGER_.FIRST_NAME))
				.lastName(record.getValue(SINGER_.LAST_NAME))
				.birthDate(record.getValue(SINGER_.BIRTH_DATE, LocalDate.class))
				.albums(new HashSet<Album>())
				.instruments(new HashSet<Instrument>())
				.build();
	}
	private Album getAlbumDomain(Record record) {
		return Album.builder()
				.id(record.getValue(ALBUM.ID))
				.singerId(record.getValue(ALBUM.SINGER_ID))
				.title(record.getValue(ALBUM.TITLE))
				.releaseDate(record.getValue(ALBUM.RELEASE_DATE, LocalDate.class))
				.build();
	}
	@Override
	public List<Singer> findAll() {
		//// 첫 번째 방법
//		List<Singer> singers = new ArrayList<Singer>();
//		Result<Record> result = dsl.select().from(SINGER_).fetch();
//		for(Record record: result) {
//			singers.add(getSingerDomain(record));
//		}
//		return singers;
		
		//// 두 번째 방법
		return dsl.select().from(SINGER_).fetch().into(Singer.class);
	}

	@Override
	public List<Singer> findAllByNativeQuery() {
		return findAll();
	}

	@Override
	public List<Singer> findByFirstName(String firstName) {
		return dsl.select()
				.from(SINGER_)
				.where(SINGER_.FIRST_NAME.like("%"+firstName+"%"))
				.fetch()
				.into(Singer.class);
	}

	@Override
	public List<Singer> findAllWithAlbums() {
		List<Singer> singers = new ArrayList<Singer>();
		Result<Record> result = dsl.select().from(SINGER_).fetch();
		for(Record record: result) {
			Singer singer = getSingerDomain(record);
			Result<Record> resultAlbums = dsl.select()
					.from(ALBUM)
					.where(ALBUM.SINGER_ID.equal(singer.getId()))
					.fetch();
			for(Record recordAlbum : resultAlbums) {
				singer.getAlbums().add(getAlbumDomain(recordAlbum));
			}
			singers.add(singer);
		}
		return singers;
	}

	@Override
	public String findNameById(Integer id) {
		return dsl.select(concat(SINGER_.FIRST_NAME, DSL.val(" "), SINGER_.LAST_NAME).as("name"))
				.from(SINGER_)
				.where(SINGER_.ID.equal(id))
				.fetchOne()
				.into(String.class);
	}

	@Override
	public Singer findById(Integer id) {
		return dsl.select()
				.from(SINGER_)
				.where(SINGER_.ID.equal(id))
				.fetchOne().into(Singer.class);
	}

	@Override
	public String findFirstNameById(Integer id) {
		return dsl.select(SINGER_.FIRST_NAME)
				.from(SINGER_)
				.where(SINGER_.ID.eq(id))
				.fetchOne()
				.into(String.class);
	}

	@Override
	public void insert(Singer singer) {
		SingerRecord record = dsl.insertInto(SINGER_)
				//.set(SINGER_.ID, singer.getId())
				.set(SINGER_.FIRST_NAME, singer.getFirstName())
				.set(SINGER_.LAST_NAME, singer.getLastName())
				.set(SINGER_.BIRTH_DATE, Date.valueOf(singer.getBirthDate()))
				.returning(SINGER_.ID)
				.fetchOne();
		
		singer.setId(record.getId());
	}

	@Override
	public void update(Singer singer) {
		dsl.update(SINGER_)
			.set(SINGER_.FIRST_NAME, singer.getFirstName())
			.set(SINGER_.LAST_NAME, singer.getLastName())
			.set(SINGER_.BIRTH_DATE, Date.valueOf(singer.getBirthDate()))
			.where(SINGER_.ID.eq(singer.getId()))
			.execute();
	}

	@Override
	public void delete(Integer singerId) {
		dsl.delete(SINGER_)
			.where(SINGER_.ID.eq(singerId))
			.execute();
	}

	@Override
	public void insertWithAlbum(Singer singer) {
		insert(singer);
		for(Album album : singer.getAlbums()) {
			AlbumRecord record = dsl.insertInto(ALBUM)
				//.set(ALBUM.ID, album.getId())
				.set(ALBUM.SINGER_ID, singer.getId())
				.set(ALBUM.TITLE, album.getTitle())
				.set(ALBUM.RELEASE_DATE, Date.valueOf(album.getReleaseDate()))
				.returning(ALBUM.ID)
				.fetchOne();
			
			album.setId(record.getId());
		}
	}

	@Override
	public List<SingerSummary> listAllSingersSummary() {
		com.linor.jooq.model.tables.Singer S = SINGER_.as("S");
		com.linor.jooq.model.tables.Album A1 = ALBUM.as("A1");
		com.linor.jooq.model.tables.Album A2 = ALBUM.as("A2");
		
		return dsl.select(S.FIRST_NAME, S.LAST_NAME, A1.TITLE.as("LAST_ALBUM"))
					.from(S)
					.leftOuterJoin(A1)
					.on(A1.SINGER_ID.eq(S.ID))
					.where(A1.RELEASE_DATE.eq(
							select(max(A2.RELEASE_DATE))
							.from(A2)
							.where(A2.SINGER_ID.eq(S.ID))
							)
						)
					.fetch()
					.into(SingerSummary.class);
	}

	@Override
	public void insertInstrument(Instrument instrument) {
		dsl.insertInto(INSTRUMENT)
			.set(INSTRUMENT.INSTRUMENT_ID, instrument.getId())
			.execute();
	}
	
	@Override
	public List<Singer> findByFirstNameAndLastName(Singer singer) {
		return dsl.select()
			.from(SINGER_)
			.where(SINGER_.FIRST_NAME.equal(singer.getFirstName()))
			.and(SINGER_.LAST_NAME.equal(singer.getLastName()))
			.fetch()
			.into(Singer.class);
	}
	
	@Override
	public List<Album> findBySinger(Singer singer) {
		return dsl.select()
				.from(ALBUM)
				.where(ALBUM.SINGER_ID.equal(singer.getId()))
				.fetch()
				.into(Album.class);
	}
	
	@Override
	public List<Album> findAlbumsByTitle(String title) {
		List<Album> albums = dsl.select()
				.from(ALBUM)
				.where(ALBUM.TITLE.like(title+"%"))
				.fetch()
				.into(Album.class);
		for(Album album:albums) {
			Singer singer = dsl.select()
					.from(SINGER_)
					.where(SINGER_.ID.equal(album.getSingerId()))
					.fetchOne()
					.into(Singer.class);
			album.setSinger(singer);
		}
		return albums;
	}

}
