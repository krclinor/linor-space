package com.linor.singer;


import static org.junit.Assert.assertEquals;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.batch.core.BatchStatus;
import org.springframework.batch.core.JobExecution;
import org.springframework.batch.test.JobLauncherTestUtils;
import org.springframework.batch.test.context.SpringBatchTest;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;

import lombok.extern.slf4j.Slf4j;

@SpringBatchTest
@RunWith(SpringRunner.class)
@SpringBootTest
@Slf4j
public class ApplicationTests {
	@Autowired
	private JobLauncherTestUtils jobLauncherTestUtils;
	
	@Test
	public void launchJob() throws Exception{
		JobExecution jobExecution = jobLauncherTestUtils.launchJob();
		log.info("처리결과: " + jobExecution.getStatus());
		assertEquals(BatchStatus.COMPLETED, jobExecution.getStatus());
	}
}
