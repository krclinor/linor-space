package com.linor.singer.schedule;

import org.springframework.stereotype.Service;

import lombok.extern.slf4j.Slf4j;

@Slf4j
@Service
public class ScheduleService {
	public void logService1() {
		log.info("작업 스케줄 테스트");
	}
}