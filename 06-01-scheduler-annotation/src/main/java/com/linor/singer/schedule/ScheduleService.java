package com.linor.singer.schedule;

import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

import lombok.extern.slf4j.Slf4j;

@Slf4j
@Service
public class ScheduleService {
	@Scheduled(fixedDelay=1000)
	public void logService1() {
		log.info("fixedDelay 작업 스케줄 테스트");
	}

	@Scheduled(fixedRate=1000)
	public void logService2() {
		log.info("fixedRate 작업 스케줄 테스트");
	}
	
	@Scheduled(cron="0/3 * * * * ?")
	public void logService3() {
		log.info("cron 스케줄 테스트");
	}
}