package com.linor.singer.dao;

import org.apache.ibatis.annotations.Delete;
import org.apache.ibatis.annotations.Insert;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Options;
import org.apache.ibatis.annotations.ResultType;
import org.apache.ibatis.annotations.Select;
import org.apache.ibatis.annotations.Update;
import org.apache.ibatis.session.ResultHandler;

import com.linor.singer.domain.Singer;

@Mapper
public interface SingerDao {
	@Select("select * from singer")
	@ResultType(Singer.class)
	void findAll(ResultHandler<Singer> handler);

	@Insert("insert into singer(id, first_name, last_name, birth_date)\n" + 
			"values\n" + 
			"(default, #{firstName},#{lastName},#{birthDate})")
	@Options(useGeneratedKeys = true)
	void insert(Singer singer);
	
	@Update("update singer set first_name = #{firstName},\n"
			+ "	last_name =  #{lastName},\n"
			+ " birth_date = #{birthDate}\n"
			+ "where id = #{id}")
	void update(Singer singer);
	
	@Select("select * from singer where id = #{id}")
	Singer findById(Integer id);
	
	@Delete("delete from singer where id = #{id}")
	void deleteById(Integer id);
}
