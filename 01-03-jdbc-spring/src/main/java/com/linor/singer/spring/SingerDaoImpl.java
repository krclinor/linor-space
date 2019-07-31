package com.linor.singer.spring;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Types;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.annotation.Resource;
import javax.sql.DataSource;

import org.springframework.dao.DataAccessException;
import org.springframework.jdbc.core.BeanPropertyRowMapper;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.ResultSetExtractor;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.jdbc.core.SqlParameter;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate;
import org.springframework.jdbc.object.BatchSqlUpdate;
import org.springframework.jdbc.object.MappingSqlQuery;
import org.springframework.jdbc.object.SqlUpdate;
import org.springframework.jdbc.support.GeneratedKeyHolder;
import org.springframework.jdbc.support.KeyHolder;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import com.linor.singer.dao.SingerDao;
import com.linor.singer.domain.Album;
import com.linor.singer.domain.Singer;

import lombok.extern.slf4j.Slf4j;

@Slf4j
@Repository
@Transactional
public class SingerDaoImpl implements SingerDao {
	
	private DataSource dataSource;

	@Resource(name="dataSource")
	public void setDataSource(DataSource dataSource) {
		this.dataSource = dataSource;
	}
	
	@Override	
	@Transactional(readOnly=true)
	public List<Singer> findAll() {
		NamedParameterJdbcTemplate template = new NamedParameterJdbcTemplate(dataSource);
		String sql = "select * from singer";
////1. RowMapper를 이용한 방법		
//		return template.query(sql,new RowMapper<Singer>() {
//			@Override
//			public Singer mapRow(ResultSet rs, int rowNum) throws SQLException {
//				Singer singer = new Singer();
//				singer.setId(rs.getInt("id"));
//				singer.setFirstName(rs.getString("first_name"));
//				singer.setLastName(rs.getString("last_name"));
//				singer.setBirthDate(rs.getDate("birth_date").toLocalDate());
//				return singer;
//			}
//		});
		
////2. 람다함수를 이용한 방법		
//		return template.query(sql, (rs, rowNum) -> {
//			Singer singer = new Singer();
//			singer.setId(rs.getInt("id"));
//			singer.setFirstName(rs.getString("first_name"));
//			singer.setLastName(rs.getString("last_name"));
//			singer.setBirthDate(rs.getDate("birth_date").toLocalDate());
//			return singer;
//		});

		
////3. BeanPropertyRowMapper를 이용한 방법		
		return template.query(sql, new BeanPropertyRowMapper<Singer>(Singer.class));
	}

	@Override
	@Transactional(readOnly=true)
	public List<Singer> findByFirstName(String firstName) {
		SelectSingerByFirstName selectSingerByFirstName = new SelectSingerByFirstName(dataSource);
		Map<String, Object> paramMap = new HashMap<>();
		paramMap.put("first_name", firstName);
		return selectSingerByFirstName.executeByNamedParam(paramMap);
	}

	private static final class SelectSingerByFirstName extends MappingSqlQuery<Singer>{
		private static final String sql =
				"select * from singer where first_name = :first_name";
		
		public SelectSingerByFirstName(DataSource dataSource) {
			super(dataSource, sql);
			super.declareParameter(new SqlParameter("first_name", Types.VARCHAR));
		}
		@Override
		protected Singer mapRow(ResultSet rs, int rowNum) throws SQLException {
			Singer singer = new Singer();
			singer.setId(rs.getInt("id"));
			singer.setFirstName(rs.getString("first_name"));
			singer.setLastName(rs.getString("last_name"));
			singer.setBirthDate(rs.getDate("birth_date").toLocalDate());
			return singer;
		}
		
	}

	@Override
	@Transactional(readOnly=true)
	public Singer findById(Integer id) {
		JdbcTemplate jdbcTemplate = new JdbcTemplate(dataSource);
		String sql = "select * from singer where id = ?";
//		return (Singer)jdbcTemplate.queryForObject(sql, new Object[] {id}, (rs, rowNum) -> {
//			Singer singer = new Singer();
//			singer.setId(rs.getInt("id"));
//			singer.setFirstName(rs.getString("first_name"));
//			singer.setLastName(rs.getString("last_name"));
//			singer.setBirthDate(rs.getDate("birth_date").toLocalDate());
//			return singer;
//		});
		return (Singer)jdbcTemplate.queryForObject(sql,	new Object[] {id}, new BeanPropertyRowMapper<Singer>(Singer.class));
	}

	@Override
	@Transactional(readOnly=true)
	public String findNameById(Integer id) {
		NamedParameterJdbcTemplate jdbcTemplate = new NamedParameterJdbcTemplate(dataSource);
		String sql = "select first_name||' '||last_name from singer where id = :singer_id";
		Map<String, Object> paramMap = new HashMap<>();
		paramMap.put("singer_id", id);
		return jdbcTemplate.queryForObject(sql, paramMap, String.class);
	}

	@Override
	@Transactional(readOnly=true)
	public String findFirstNameById(Integer id) {
		JdbcTemplate jdbcTemplate = new JdbcTemplate(dataSource);
		return jdbcTemplate.queryForObject("select first_name from singer where id = ?",	new Object[] {id}, String.class);
	}

	@Override
	public void insert(Singer singer) {
		InsertSinger insertSinger = new InsertSinger(dataSource);
		Map<String, Object> paramMap = new HashMap<>();
		paramMap.put("first_name", singer.getFirstName());
		paramMap.put("last_name", singer.getLastName());
		paramMap.put("birth_date", singer.getBirthDate());
		KeyHolder keyHolder =new GeneratedKeyHolder();
		insertSinger.updateByNamedParam(paramMap, keyHolder);
		singer.setId(keyHolder.getKey().intValue());
		log.info("추가된 가수ID: {}",singer.getId() );
	}
	private static final class InsertSinger extends SqlUpdate{
		private static final String sql = "insert into singer (first_name, last_name, birth_date)\n"+
				"values(:first_name, :last_name, :birth_date)";
		public InsertSinger(DataSource dataSource) {
			super(dataSource, sql);
			declareParameter(new SqlParameter("first_name", Types.VARCHAR));
			declareParameter(new SqlParameter("last_name", Types.VARCHAR));
			declareParameter(new SqlParameter("birth_date", Types.DATE));
			setGeneratedKeysColumnNames(new String[] {"id"});
			setReturnGeneratedKeys(true);
		}
	}
	
	@Override
	public void update(Singer singer) {
		UpdateSinger updateSinger = new UpdateSinger(dataSource);
		Map<String, Object> paraMap = new HashMap<>();
		paraMap.put("first_name", singer.getFirstName());
		paraMap.put("last_name", singer.getLastName());
		paraMap.put("birth_date", singer.getBirthDate());
		paraMap.put("id", singer.getId());
		updateSinger.updateByNamedParam(paraMap);
	}
	
	private static final class UpdateSinger extends SqlUpdate{
		private static final String sql = 
				"update singer set first_name = :first_name,\n"+
				"last_name = :last_name,\n"+
				"birth_date = :birth_date\n"+
				"where id = :id";
		
		public UpdateSinger(DataSource dataSource) {
			super(dataSource, sql);
			declareParameter(new SqlParameter("first_name", Types.VARCHAR));
			declareParameter(new SqlParameter("last_name", Types.VARCHAR));
			declareParameter(new SqlParameter("birth_date", Types.DATE));
			declareParameter(new SqlParameter("id", Types.INTEGER));
		}
	}

	@Override
	public void delete(Integer singerId) {
		Connection con = null;
		PreparedStatement stmt = null;
		try {
			con = dataSource.getConnection();
			stmt = con.prepareStatement("delete from singer\n"+
					"where id = ?");
			stmt.setInt(1, singerId);
			stmt.executeUpdate();
		}catch (Exception e) {
			e.printStackTrace();
		}finally {
			if(stmt != null ) try {stmt.close();}catch (Exception e2) {}
			if(con != null ) try {con.close();}catch (Exception e2) {}
		}
	}

	@Override
	@Transactional(readOnly=true)
	public List<Singer> findAllWithAlbums() {
		JdbcTemplate template = new JdbcTemplate(dataSource);
		String sql = "select s.id, s.first_name, s.last_name, s.birth_date,\n" + 
				"	a.id album_id, a.title, a.release_date\n" + 
				"from	singer s\n" + 
				"left outer join album a on s.id = a.singer_id";
		return template.query(sql, new SingerWithAlbumExtractor());
	}

	private static final class SingerWithAlbumExtractor implements ResultSetExtractor<List<Singer>>{

		@Override
		public List<Singer> extractData(ResultSet rs) throws SQLException, DataAccessException {
			Map<Integer, Singer> map = new HashMap<>();
			Singer singer;
			while(rs.next()) {
				Integer id = rs.getInt("id");
				singer = map.get(id);
				if(singer == null) {
					singer = new Singer();
					singer.setId(id);
					singer.setFirstName(rs.getString("first_name"));
					singer.setLastName(rs.getString("last_name"));
					singer.setBirthDate(rs.getDate("birth_date").toLocalDate());
					singer.setAlbums(new ArrayList<>());
					map.put(id, singer);
				}
				Integer albumId = rs.getInt("album_id");
				if(albumId > 0) {
					Album album = new Album();
					album.setId(albumId);
					album.setSingerId(id);
					album.setTitle(rs.getString("title"));
					album.setReleaseDate(rs.getDate("release_date").toLocalDate());
					singer.getAlbums().add(album);
				}
			}
			return new ArrayList<>(map.values());
		}
	}
	
	@Override
	public void insertWithAlbum(Singer singer) {
		InsertAlbum insertAlbum = new InsertAlbum(dataSource);
		insert(singer);
		List<Album> albums = singer.getAlbums();
		if(albums != null) {
			for(Album album:albums) {
				Map<String, Object> paramMap = new HashMap<>();
				paramMap.put("singer_id", singer.getId());
				paramMap.put("title",album.getTitle());
				paramMap.put("release_date", album.getReleaseDate());
				insertAlbum.updateByNamedParam(paramMap);
			}
			insertAlbum.flush();
		}
	}

	private static class InsertAlbum extends BatchSqlUpdate{
		private static final String sql = "insert into album (singer_id, title, release_date)\n"+
				"values(:singer_id, :title, :release_date)";
		private static final int BATCH_SIZE = 10;
		
		public InsertAlbum(DataSource dataSource) {
			super(dataSource, sql);
			declareParameter(new SqlParameter("singer_id", Types.INTEGER));
			declareParameter(new SqlParameter("title", Types.VARCHAR));
			declareParameter(new SqlParameter("release_date", Types.DATE));
			setBatchSize(BATCH_SIZE);
		}
	}
}
