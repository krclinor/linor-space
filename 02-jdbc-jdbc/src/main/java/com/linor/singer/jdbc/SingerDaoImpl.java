package com.linor.singer.jdbc;

import java.sql.Connection;
import java.sql.Date;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.sql.DataSource;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.object.BatchSqlUpdate;
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
	
	@Autowired
	private DataSource dataSource;
	

	@Override
	@Transactional(readOnly=true)
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
				Singer singer = new Singer();
				singer.setId(rs.getInt("id"));
				singer.setFirstName(rs.getString("first_name"));
				singer.setLastName(rs.getString("last_name"));
				singer.setBirthDate(rs.getDate("birth_date").toLocalDate());
				singers.add(singer);
			}
			return singers;
		} catch (SQLException e) {
			e.printStackTrace();
		}finally {
			if(rs != null ) try {rs.close();}catch (Exception e2) {}
			if(stmt != null ) try {stmt.close();}catch (Exception e2) {}
			if(con != null ) try {con.close();}catch (Exception e2) {}
		}
		
		return null;
	}

	@Override
	@Transactional(readOnly=true)
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
				Singer singer = new Singer();
				singer.setId(rs.getInt("id"));
				singer.setFirstName(rs.getString("first_name"));
				singer.setLastName(rs.getString("last_name"));
				singer.setBirthDate(rs.getDate("birth_date").toLocalDate());
				singers.add(singer);
			}
			return singers;
		} catch (SQLException e) {
			e.printStackTrace();
		}finally {
			if(rs != null ) try {rs.close();}catch (Exception e2) {}
			if(stmt != null ) try {stmt.close();}catch (Exception e2) {}
			if(con != null ) try {con.close();}catch (Exception e2) {}
		}
		return null;
	}

	@Override
	@Transactional(readOnly=true)
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
		}finally {
			if(rs != null ) try {rs.close();}catch (Exception e2) {}
			if(stmt != null ) try {stmt.close();}catch (Exception e2) {}
			if(con != null ) try {con.close();}catch (Exception e2) {}
		}
		return null;
	}

	@Override
	@Transactional(readOnly=true)
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
				Singer singer = new Singer();
				singer.setId(rs.getInt("id"));
				singer.setFirstName(rs.getString("first_name"));
				singer.setLastName(rs.getString("last_name"));
				singer.setBirthDate(rs.getDate("birth_date").toLocalDate());
				return singer;
			}
		} catch (SQLException e) {
			e.printStackTrace();
		}finally {
			if(rs != null ) try {rs.close();}catch (Exception e2) {}
			if(stmt != null ) try {stmt.close();}catch (Exception e2) {}
			if(con != null ) try {con.close();}catch (Exception e2) {}
		}
		return null;
	}

	@Override
	@Transactional(readOnly=true)
	public String findFirstNameById(Integer id) {
		Connection con = null;
		PreparedStatement stmt = null;
		ResultSet rs = null;
		try {
			con = dataSource.getConnection();
			stmt = con.prepareStatement("select first_name  from singer\n"+
					"where id = ?");
			stmt.setInt(1, id);
			rs = stmt.executeQuery();
			rs.next();
			return rs.getString("first_name");
		} catch (SQLException e) {
			e.printStackTrace();
		}finally {
			if(rs != null ) try {rs.close();}catch (Exception e2) {}
			if(stmt != null ) try {stmt.close();}catch (Exception e2) {}
			if(con != null ) try {con.close();}catch (Exception e2) {}
		}
		return null;
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
		} catch (SQLException e) {
			e.printStackTrace();
		}finally {
			if(rs != null ) try {rs.close();}catch (Exception e2) {}
			if(stmt != null ) try {stmt.close();}catch (Exception e2) {}
			if(con != null ) try {con.close();}catch (Exception e2) {}
		}
		
		return null;
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

			List<Album> albums = singer.getAlbums();
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

}