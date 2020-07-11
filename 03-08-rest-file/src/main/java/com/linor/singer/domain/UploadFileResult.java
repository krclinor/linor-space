package com.linor.singer.domain;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class UploadFileResult {
	private String fileName;
	private String fileDownloadUri;
	private String fileType;
	private long size;
}
