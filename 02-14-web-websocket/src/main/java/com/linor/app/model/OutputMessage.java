package com.linor.app.model;

import java.time.LocalDateTime;

import com.fasterxml.jackson.annotation.JsonFormat;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class OutputMessage {
	private String from;
	private String text;
	@JsonFormat(pattern = "yyyy.MM.dd HH:mm:ss SSS", timezone = "Asia/Seoul")
	private LocalDateTime time;
}
