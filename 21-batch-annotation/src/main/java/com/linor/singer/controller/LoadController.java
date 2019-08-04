package com.linor.singer.controller;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.springframework.batch.core.BatchStatus;
import org.springframework.batch.core.Job;
import org.springframework.batch.core.JobExecution;
import org.springframework.batch.core.JobParameter;
import org.springframework.batch.core.JobParameters;
import org.springframework.batch.core.JobParametersInvalidException;
import org.springframework.batch.core.launch.JobLauncher;
import org.springframework.batch.core.repository.JobExecutionAlreadyRunningException;
import org.springframework.batch.core.repository.JobInstanceAlreadyCompleteException;
import org.springframework.batch.core.repository.JobRestartException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

import com.linor.singer.dao.SingerDao;
import com.linor.singer.domain.Singer;

import lombok.extern.slf4j.Slf4j;

@RestController
@Slf4j
public class LoadController {
	
	@Autowired
	SingerDao dao;
	
	@Autowired
	JobLauncher jobLauncher;
	
	@Autowired 
	@Qualifier("job2")
	Job job2;
	
	@GetMapping("/load")
	public BatchStatus load() throws JobExecutionAlreadyRunningException,
		JobRestartException, JobInstanceAlreadyCompleteException, JobParametersInvalidException{
		Map<String, JobParameter> maps = new HashMap<String, JobParameter>();
		maps.put("time", new JobParameter(System.currentTimeMillis()));
		JobParameters parameters = new JobParameters(maps);
		JobExecution jobExecution = jobLauncher.run(job2, parameters);
		
		log.info("JobExecuteion: " + jobExecution.getStatus());
		log.info("Batch is Running...");
		while(jobExecution.isRunning()) {
			log.info(".......");
		}
		return jobExecution.getStatus();
	}
	
	@GetMapping("/list")
	public List<Singer> list(){
		return dao.findAll();
	}
}
