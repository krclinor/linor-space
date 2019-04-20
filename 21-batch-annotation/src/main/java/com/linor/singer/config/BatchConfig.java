package com.linor.singer.config;

import java.time.format.DateTimeFormatter;

import org.apache.ibatis.session.SqlSessionFactory;
import org.mybatis.spring.batch.MyBatisBatchItemWriter;
import org.mybatis.spring.batch.MyBatisCursorItemReader;
import org.springframework.batch.core.Job;
import org.springframework.batch.core.Step;
import org.springframework.batch.core.configuration.annotation.EnableBatchProcessing;
import org.springframework.batch.core.configuration.annotation.JobBuilderFactory;
import org.springframework.batch.core.configuration.annotation.StepBuilderFactory;
import org.springframework.batch.core.launch.support.RunIdIncrementer;
import org.springframework.batch.item.file.FlatFileItemReader;
import org.springframework.batch.item.file.FlatFileItemWriter;
import org.springframework.batch.item.file.LineMapper;
import org.springframework.batch.item.file.mapping.DefaultLineMapper;
import org.springframework.batch.item.file.transform.DelimitedLineTokenizer;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.io.FileSystemResource;

import com.linor.singer.batch.SingerDbWriter;
import com.linor.singer.batch.SingerFieldSetMapper;
import com.linor.singer.batch.SingerProcessor;
import com.linor.singer.domain.Singer;

import lombok.RequiredArgsConstructor;

@Configuration
@EnableBatchProcessing
@RequiredArgsConstructor
public class BatchConfig {
	private final JobBuilderFactory jobBuilderFactory;
	private final StepBuilderFactory stepBuilderFactory;
	private final SqlSessionFactory sqlSessionFactory;
	private final SingerProcessor singerProcessor;
	private final SingerDbWriter singerDbWriter;
	
	@Bean
	public LineMapper<Singer> lineMapper(){
		DefaultLineMapper<Singer> defaultLineMapper = new DefaultLineMapper<>();
		DelimitedLineTokenizer lineTokenizer = new DelimitedLineTokenizer();
		lineTokenizer.setDelimiter(",");
		lineTokenizer.setStrict(false);
		defaultLineMapper.setLineTokenizer(lineTokenizer);
		
		SingerFieldSetMapper mapper = new SingerFieldSetMapper();
		defaultLineMapper.setFieldSetMapper(mapper);
		
		return defaultLineMapper;
	}
	
	@Bean
	public FlatFileItemReader<Singer> fileItemReader(@Value("${input}") String path){
		FlatFileItemReader<Singer> flatFileItemReader = new FlatFileItemReader<>();
		flatFileItemReader.setResource(new FileSystemResource(path));
		flatFileItemReader.setLinesToSkip(1);//첫 라인 건너뜀
		flatFileItemReader.setLineMapper(lineMapper());
		return flatFileItemReader;
	}
	
	private DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd");
	@Bean
	public FlatFileItemWriter<Singer> fileWriter(@Value("${output}") String path){
		final FlatFileItemWriter<Singer> writer = new FlatFileItemWriter<>();
		writer.setResource(new FileSystemResource(path));
		writer.setHeaderCallback(headerWriter -> headerWriter.append("id, firstName, lastName, birthDate"));
		writer.setLineAggregator(item -> item.getId() + ","
				+ item.getFirstName() + "," 
				+ item.getLastName() + ","
				+ item.getBirthDate().format(formatter));
		return writer;
	}
	
	@Bean
	public MyBatisBatchItemWriter<Singer> dbWriter(){
		final MyBatisBatchItemWriter<Singer> writer = new MyBatisBatchItemWriter<>();
		writer.setSqlSessionFactory(sqlSessionFactory);
		writer.setStatementId("com.linor.singer.dao.SingerDao.insert");
		return writer;
		
	}
	
	@Bean
	public MyBatisCursorItemReader<Singer> dbReader(){
		final MyBatisCursorItemReader<Singer> reader = new MyBatisCursorItemReader<>();
		reader.setSqlSessionFactory(sqlSessionFactory);
		reader.setQueryId("com.linor.singer.dao.SingerDao.findAll");
		return reader;
	}
	
	@Bean
	Step step1() {
		return stepBuilderFactory.get("ETL-file-load")
				.<Singer, Singer>chunk(100)
				.reader(fileItemReader(null))
				.processor(singerProcessor)
				//.writer(singerDbWriter)
				.writer(dbWriter())
				.build();
	}
	
	@Bean
	Step step2() {
		return stepBuilderFactory.get("ETL-file-write")
				.<Singer, Singer>chunk(10)
				.reader(dbReader())
				.writer(fileWriter(null))
				.build();
	}
	
	@Bean
	public Job job() {
		return jobBuilderFactory.get("ETL-Load")
				.incrementer(new RunIdIncrementer())
				.start(step1())
				.next(step2())
				.build();
	}
	
}
