package com.linor.singer;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.Assert.assertEquals;

import java.io.ByteArrayInputStream;
import java.io.ObjectInput;
import java.io.ObjectInputStream;
import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;

import javax.sql.DataSource;

import org.junit.FixMethodOrder;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.MethodSorters;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.context.SpringBootTest.WebEnvironment;
import org.springframework.boot.test.web.client.TestRestTemplate;
import org.springframework.boot.web.server.LocalServerPort;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;

@RunWith(SpringRunner.class)
@SpringBootTest(webEnvironment = WebEnvironment.RANDOM_PORT)
@FixMethodOrder(MethodSorters.NAME_ASCENDING)
public class ApplicationTests {
	@LocalServerPort
	private int port;
	
	@Autowired
	private TestRestTemplate testRestTemplate;
	
	@Autowired
	private DataSource dataSource;
	
	private List<String> getSessionIdsFromDatabase()
			throws SQLException{
		List<String> result = new ArrayList<>();
		ResultSet rs = getResultSet("SELECT * FROM SPRING_SESSION");
		while(rs.next()) {
			result.add(rs.getString("SESSION_ID"));
		}
		return result;
	}
	
	private List<byte[]> getSessionAttributeBytesFromDb() throws SQLException {
		List<byte[]> result = new ArrayList<>();
		ResultSet rs = getResultSet("SELECT * FROM SPRING_SESSION_ATTRIBUTES");
		while(rs.next()) {
			result.add(rs.getBytes("ATTRIBUTE_BYTES"));
		}
		
		return result;
	}
	
	private ResultSet getResultSet(String sql) throws SQLException {
		Connection con = dataSource.getConnection();
		Statement stat = con.createStatement();
		return stat.executeQuery(sql);
	}
	
	@Test
	public void whenDbIsQueried_then1SessionInfoIsEmpty() throws SQLException{
		assertEquals(0,  getSessionIdsFromDatabase().size());
		assertEquals(0, getSessionAttributeBytesFromDb().size());
	}
	
	@Test
	public void whenDbIsQueried_then2OneSessionCreated() throws SQLException{
		assertThat(this.testRestTemplate.getForObject(
				"http://localhost:"+port+"/", String.class))
				.isNotEmpty();
		assertEquals(1, getSessionIdsFromDatabase().size());
	}
	
	@Test
	public void whenDbisQueried_then3SessionAttributeIsRetrieved() throws Exception{
		MultiValueMap<String, String> map = new LinkedMultiValueMap<>();
		map.add("color", "red");
		this.testRestTemplate.postForObject("http://localhost:" + port + "/saveColor", map, String.class);
		List<byte[]> queryResponse = getSessionAttributeBytesFromDb();
		
		assertEquals(1, queryResponse.size());
		
		ObjectInput in = new ObjectInputStream(new ByteArrayInputStream(queryResponse.get(0)));
		List<String> obj = (List<String>)in.readObject();
		assertEquals("red", obj.get(0));
	}

}
