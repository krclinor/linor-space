package com.linor.singer.jdbc;

import java.sql.Connection;
import java.sql.Date;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import javax.sql.DataSource;

import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import com.linor.singer.dao.SingerDao;
import com.linor.singer.domain.Album;
import com.linor.singer.domain.Instrument;
import com.linor.singer.domain.Singer;
import com.linor.singer.domain.SingerSummary;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Repository
@Transactional
@RequiredArgsConstructor
public class SingerDaoImpl implements SingerDao {
	
	private final DataSource dataSource;

	@Override
	public List<Singer> findAll() {
		Connection con = null;
		PreparedStatement stmt = null;
		ResultSet rs = null;
		try {
			con = dataSource.getConnection();
			stmt = con.prepareStatement("select * from singer");
			rs = stmt.executeQuery();
			List<Singer> singers = new ArrayList<>();
			
			while(rs.next()) {
				Singer singer = Singer.builder()
						.id(rs.getInt("id"))
						.firstName(rs.getString("first_name"))
						.lastName(rs.getString("last_name"))
						.birthDate(rs.getDate("birth_date").toLocalDate())
						.build();
				singers.add(singer);
			}
			return singers;
		} catch (SQLException e) {
			log.error("에러코드: {}, 에러내역: {}", e.getErrorCode(), e.getMessage());
			return null;
		}finally {
			if(rs != null ) try {rs.close();}catch (Exception e2) {}
			if(stmt != null ) try {stmt.close();}catch (Exception e2) {}
			if(con != null ) try {con.close();}catch (Exception e2) {}
		}
	}

	@Override
	public List<Singer> findByFirstName(String firstName) {
		Connection con = null;
		PreparedStatement stmt = null;
		ResultSet rs = null;
		try {
			con = dataSource.getConnection();
			stmt = con.prepareStatement("select * from singer\n"+
					"where first_name = ?");
			stmt.setString(1, firstName);
			rs = stmt.executeQuery();
			List<Singer> singers = new ArrayList<>();
			
			while(rs.next()) {
				Singer singer = Singer.builder()
						.id(rs.getInt("id"))
						.firstName(rs.getString("first_name"))
						.lastName(rs.getString("last_name"))
						.birthDate(rs.getDate("birth_date").toLocalDate())
						.build();
				singers.add(singer);
			}
			return singers;
		} catch (SQLException e) {
			e.printStackTrace();
			return null;
		}finally {
			if(rs != null ) try {rs.close();}catch (Exception e2) {}
			if(stmt != null ) try {stmt.close();}catch (Exception e2) {}
			if(con != null ) try {con.close();}catch (Exception e2) {}
		}
	}

	@Override
	public String findNameById(Integer id) {
		Connection con = null;
		PreparedStatement stmt = null;
		ResultSet rs = null;
		try {
			con = dataSource.getConnection();
			stmt = con.prepareStatement("select first_name || ' ' || last_name as name from singer\n"+
					"where id = ?");
			stmt.setInt(1, id);
			rs = stmt.executeQuery();
			rs.next();
			return rs.getString("name");
		} catch (SQLException e) {
			e.printStackTrace();
			return null;
		}finally {
			if(rs != null ) try {rs.close();}catch (Exception e2) {}
			if(stmt != null ) try {stmt.close();}catch (Exception e2) {}
			if(con != null ) try {con.close();}catch (Exception e2) {}
		}
	}

	@Override
	public Singer findById(Integer id) {
		Connection con = null;
		PreparedStatement stmt = null;
		ResultSet rs = null;
		try {
			con = dataSource.getConnection();
			stmt = con.prepareStatement("select * from singer\n"+
					"where id = ?");
			stmt.setInt(1, id);
			rs = stmt.executeQuery();
			if(rs.next()) {
				return Singer.builder()
						.id(rs.getInt("id"))
						.firstName(rs.getString("first_name"))
						.lastName(rs.getString("last_name"))
						.birthDate(rs.getDate("birth_date").toLocalDate())
						.build();
			}
			return null;
		} catch (SQLException e) {
			e.printStackTrace();
			return null;
		}finally {
			if(rs != null ) try {rs.close();}catch (Exception e2) {}
			if(stmt != null ) try {stmt.close();}catch (Exception e2) {}
			if(con != null ) try {con.close();}catch (Exception e2) {}
		}
	}

	@Override
	public String findFirstNameById(Integer id) {
		Connection con = null;
		PreparedStatement stmt = null;
		ResultSet rs = null;
		try {
			con = dataSource.getConnection();
			stmt = con.prepareStatement(
					"select first_name  from singer\n"+
					"where id = ?");
			stmt.setInt(1, id);
			rs = stmt.executeQuery();
			rs.next();
			return rs.getString("first_name");
		} catch (SQLException e) {
			e.printStackTrace();
			return null;
		}finally {
			if(rs != null ) try {rs.close();}catch (Exception e2) {}
			if(stmt != null ) try {stmt.close();}catch (Exception e2) {}
			if(con != null ) try {con.close();}catch (Exception e2) {}
		}
	}

	@Override
	public void insert(Singer singer) {
		Connection con = null;
		PreparedStatement stmt = null;
		ResultSet rs = null;
		try {
			con = dataSource.getConnection();
			stmt = con.prepareStatement("insert into SINGER\n"+
					"(first_name, last_name, birth_date)\n"+
					"values(?, ?, ?)\n",
					Statement.RETURN_GENERATED_KEYS);
			stmt.setString(1, singer.getFirstName());
			stmt.setString(2, singer.getLastName());
			stmt.setDate(3, Date.valueOf(singer.getBirthDate()));
			stmt.execute();
			rs = stmt.getGeneratedKeys();
			if(rs.next()) {
				singer.setId(rs.getInt(1));
			}
		}catch (Exception e) {
			e.printStackTrace();
		}finally {
			if(rs != null ) try {rs.close();}catch (Exception e2) {}
			if(stmt != null ) try {stmt.close();}catch (Exception e2) {}
			if(con != null ) try {con.close();}catch (Exception e2) {}
		}
	}

	@Override
	public void update(Singer singer) {
		Connection con = null;
		PreparedStatement stmt = null;
		try {
			con = dataSource.getConnection();
			stmt = con.prepareStatement("update singer\n"+
					"set first_name = ?,\n"+
					"last_name = ?,\n"+
					"birth_date = ?\n"+
					"where id = ?");
			stmt.setString(1, singer.getFirstName());
			stmt.setString(2, singer.getLastName());
			stmt.setDate(3, Date.valueOf(singer.getBirthDate()));
			stmt.setInt(4, singer.getId());
			stmt.executeUpdate();
		}catch (Exception e) {
			e.printStackTrace();
		}finally {
			if(stmt != null ) try {stmt.close();}catch (Exception e2) {}
			if(con != null ) try {con.close();}catch (Exception e2) {}
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
		Connection con = null;
		PreparedStatement stmt = null;
		ResultSet rs = null;
		String sql = "select s.id, s.first_name, s.last_name, s.birth_date,\n" + 
				"	a.id album_id, a.title, a.release_date\n" + 
				"from	singer s\n" + 
				"left outer join album a on s.id = a.singer_id";
		try {
			con = dataSource.getConnection();
			stmt = con.prepareStatement(sql);
			rs = stmt.executeQuery();

			Map<Integer, Singer> map = new HashMap<>();
			Singer singer;
			while(rs.next()) {
				Integer id = rs.getInt("id");
				singer = map.get(id);
				if(singer == null) {
					singer = Singer.builder()
							.id(rs.getInt("id"))
							.firstName(rs.getString("first_name"))
							.lastName(rs.getString("last_name"))
							.birthDate(rs.getDate("birth_date").toLocalDate())
							.build();
					Integer albumId = rs.getInt("album_id");
					if(albumId > 0) {
						singer.setAlbums(new HashSet<Album>());
					}
					map.put(id, singer);
				}
				Integer albumId = rs.getInt("album_id");
				if(albumId > 0) {
					Album album = Album.builder()
							.id(albumId)
							.singerId(id)
							.title(rs.getString("title"))
							.releaseDate(rs.getDate("release_date").toLocalDate())
							.build();
					singer.getAlbums().add(album);
				}
			}
			return new ArrayList<>(map.values());
		} catch (SQLException e) {
			e.printStackTrace();
			return null;
		}finally {
			if(rs != null ) try {rs.close();}catch (Exception e2) {}
			if(stmt != null ) try {stmt.close();}catch (Exception e2) {}
			if(con != null ) try {con.close();}catch (Exception e2) {}
		}
	}

	@Override
	public void insertWithAlbum(Singer singer) {
		
		Connection con = null;
		PreparedStatement stmt = null;
		String sql = "insert into album (singer_id, title, release_date)\n"+
				"values(?, ?, ?)";
		try {
			insert(singer);
			con = dataSource.getConnection();
			stmt = con.prepareStatement(sql);

			Set<Album> albums = singer.getAlbums();
			if(albums != null) {
				for(Album album:albums) {
					stmt.setInt(1, singer.getId());
					stmt.setString(2, album.getTitle());
					stmt.setDate(3, Date.valueOf(album.getReleaseDate()));
					stmt.addBatch();
				}
				stmt.executeBatch();
			}
		}catch (Exception e) {
			e.printStackTrace();
		}finally {
			if(stmt != null ) try {stmt.close();}catch (Exception e2) {}
			if(con != null ) try {con.close();}catch (Exception e2) {}
		}
	}

	@Override
	public List<Singer> findAllByNativeQuery() {
		return this.findAll();
	}

	@Override
	public List<Singer> findByFirstNameAndLastName(Singer singer) {
		Connection con = null;
		PreparedStatement stmt = null;
		ResultSet rs = null;
		try {
			con = dataSource.getConnection();
			stmt = con.prepareStatement(
					"select *  from singer\n"+
					"where first_name = ?\n"+
					 "and last_name = ?");
			stmt.setString(1, singer.getFirstName());
			stmt.setString(2, singer.getLastName());
			rs = stmt.executeQuery();
			List<Singer> singers = new ArrayList<Singer>();
			while(rs.next()) {
				Singer s = Singer.builder()
						.id(rs.getInt("id"))
						.firstName(rs.getString("first_name"))
						.lastName(rs.getString("last_name"))
						.birthDate(rs.getDate("birth_date").toLocalDate())
						.build();
				singers.add(s);
			}
			return singers;
		} catch (SQLException e) {
			e.printStackTrace();
			return null;
		}finally {
			if(rs != null ) try {rs.close();}catch (Exception e2) {}
			if(stmt != null ) try {stmt.close();}catch (Exception e2) {}
			if(con != null ) try {con.close();}catch (Exception e2) {}
		}
	}

	@Override
	public List<Album> findAlbumsBySinger(Singer singer) {
		Connection con = null;
		PreparedStatement stmt = null;
		ResultSet rs = null;
		try {
			con = dataSource.getConnection();
			stmt = con.prepareStatement(
					"select *  from album\n"+
					"where singer_id = ?");
			stmt.setInt(1, singer.getId());
			rs = stmt.executeQuery();
			List<Album> albums = new ArrayList<Album>();
			while(rs.next()) {
				Album a = Album.builder()
						.id(rs.getInt("id"))
						.singerId(rs.getInt("singer_id"))
						.title(rs.getString("title"))
						.releaseDate(rs.getDate("release_date").toLocalDate())
						.build();
				albums.add(a);
			}
			return albums;
		} catch (SQLException e) {
			e.printStackTrace();
			return null;
		}finally {
			if(rs != null ) try {rs.close();}catch (Exception e2) {}
			if(stmt != null ) try {stmt.close();}catch (Exception e2) {}
			if(con != null ) try {con.close();}catch (Exception e2) {}
		}
	}

	@Override
	public List<Album> findAlbumsByTitle(String title) {
		Connection con = null;
		PreparedStatement stmt = null;
		ResultSet rs = null;
		try {
			con = dataSource.getConnection();
			stmt = con.prepareStatement(
					"select *  from album\n"+
					"where title like ?||'%'");
			stmt.setString(1, title);
			rs = stmt.executeQuery();
			List<Album> albums = new ArrayList<Album>();
			while(rs.next()) {
				Singer singer = findById(rs.getInt("singer_id"));
				Album a = Album.builder()
						.id(rs.getInt("id"))
						.singerId(rs.getInt("singer_id"))
						.title(rs.getString("title"))
						.releaseDate(rs.getDate("release_date").toLocalDate())
						.singer(singer)
						.build();
				albums.add(a);
			}
			return albums;
		} catch (SQLException e) {
			e.printStackTrace();
			return null;
		}finally {
			if(rs != null ) try {rs.close();}catch (Exception e2) {}
			if(stmt != null ) try {stmt.close();}catch (Exception e2) {}
			if(con != null ) try {con.close();}catch (Exception e2) {}
		}	}

	@Override
	public List<SingerSummary> listAllSingersSummary() {
		Connection con = null;
		PreparedStatement stmt = null;
		ResultSet rs = null;
		try {
			con = dataSource.getConnection();
			stmt = con.prepareStatement(
					"	select s.id, s.first_name, s.last_name, a.title last_album\n" + 
					"	from	singer s\n" + 
					"	left outer join album a on a.singer_id = s.id\n" + 
					"	where a.release_date = (select max(a2.release_date) from album a2 where a2.singer_id = s.id)");
			rs = stmt.executeQuery();
			List<SingerSummary> summaries = new ArrayList<SingerSummary>();
			while(rs.next()) {
				SingerSummary sm = SingerSummary.builder()
						.firstName(rs.getString("first_name"))
						.lastName(rs.getString("last_name"))
						.lastAlbum(rs.getString("last_album"))
						.build();
				summaries.add(sm);
			}
			return summaries;
		} catch (SQLException e) {
			e.printStackTrace();
			return null;
		}finally {
			if(rs != null ) try {rs.close();}catch (Exception e2) {}
			if(stmt != null ) try {stmt.close();}catch (Exception e2) {}
			if(con != null ) try {con.close();}catch (Exception e2) {}
		}
	}

	@Override
	public void insertInstrument(Instrument instrument) {
		Connection con = null;
		PreparedStatement stmt = null;
		try {
			con = dataSource.getConnection();
			stmt = con.prepareStatement("insert into instrument (instrument_id)\n" + 
					"values (?)");
			stmt.setString(1, instrument.getId());
			stmt.execute();
		}catch (Exception e) {
			e.printStackTrace();
		}finally {
			if(stmt != null ) try {stmt.close();}catch (Exception e2) {}
			if(con != null ) try {con.close();}catch (Exception e2) {}
		}
	}

}
