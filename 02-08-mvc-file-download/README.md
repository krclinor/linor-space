# 파일 다운로드

## Spring Boot Starter를 이용한 프로젝트 생성
Spring boot Starter로 프로젝트 생성시 패키징은 war로 설정한다.
```xml
	<packaging>war</packaging>
```

### 의존성 라이브러리
Spring initializer로 생성시 추가할 dependency는 Web, DevTools, Lombok이다.  
프로젝트 생성 후 pom.xml에 JSP사용을 위해 tomcat-jasper를 추가한다.  
소스 : [pom.xml](pom.xml)
```xml
	<dependencies>
		<dependency>
			<groupId>org.springframework.boot</groupId>
			<artifactId>spring-boot-starter-web</artifactId>
		</dependency>
		<dependency>
			<groupId>org.springframework.boot</groupId>
			<artifactId>spring-boot-devtools</artifactId>
			<scope>runtime</scope>
		</dependency>
		<dependency>
			<groupId>org.projectlombok</groupId>
			<artifactId>lombok</artifactId>
			<optional>true</optional>
		</dependency>
		<dependency>
			<groupId>org.springframework.boot</groupId>
			<artifactId>spring-boot-starter-test</artifactId>
			<scope>test</scope>
		</dependency>
 		<!-- JSTL for JSP -->
		<dependency>
			<groupId>javax.servlet</groupId>
			<artifactId>jstl</artifactId>
		</dependency>
		<!-- Need this to compile JSP -->
		<dependency>
			<groupId>org.apache.tomcat.embed</groupId>
			<artifactId>tomcat-embed-jasper</artifactId>
			<scope>provided</scope>
		</dependency>
	</dependencies>
```

## 설정
### 어플리케이션 설정
소스 : [application.yml](src/main/resources/application.yml)  
```yml
spring:
  mvc:
    view:
      prefix: /WEB-INF/jsp/
      suffix: .jsp

myapp.download-folder: test
```

myapp.download-folder는 프로그램에서 사용하기 위해 만든 것으로 다운로드할 파일의 위치를 지정한다.  

테스트 파일은 간단하게 다음가 같이 생성해 놓는다.  
루트 폴더는 프로젝트 폴더이다.  
파일명 :test/test.txt  
```txt
안녕하세요..
연습용 파일 입니다.
```
### 컨트롤러 생성
소스 :[FileController.java](src/main/java/com/linor/singer/controller/FileController.java)  

```java
@Controller
@RequiredArgsConstructor
public class FileController {
	private final ServletContext servletContext;
	private MediaType getMediaTypeForFileName(String fileName) {
		String mimeType = servletContext.getMimeType(fileName);
		try {
			MediaType mediaType = MediaType.parseMediaType(mimeType);
			return mediaType;
		}catch(Exception e) {
			return MediaType.APPLICATION_OCTET_STREAM;
		}
	}
	
	@Value("${myapp.download-folder:test}")
	private String DOWNLOAD_FOLDER;
	
	private final String FILE_NAME = "test.txt";
	
	/**
	 * ResponseEntity<InputStreamResource>를 이용한 방법
	 * http://localhost:8080/download1?fileName=test.txt
	 * @return
	 * @throws IOException
	 */
	@RequestMapping("/download1")
	public ResponseEntity<InputStreamResource> downloadFile1(
			@RequestParam(defaultValue = FILE_NAME) String fileName) throws IOException{
		MediaType mediaType = getMediaTypeForFileName(fileName);
		File file = new File(DOWNLOAD_FOLDER + File.separator + fileName);
		InputStreamResource resource = new InputStreamResource(new FileInputStream(file));
		//한글파일명을 위한 설정
		String headerfileName = URLEncoder.encode(fileName, "UTF-8").replaceAll("\\+", "%20");
		
		return ResponseEntity.ok()
				// Content-Disposition
				.header(HttpHeaders.CONTENT_DISPOSITION, "attachement;filename=" + headerfileName)
				// Content-Type
				.contentType(mediaType)
				// Content-Length
				.contentLength(file.length())
				.body(resource);
	}
	
	/**
	 * ResponseEntity<ByteArrayResource>를 이용한 방법
	 * http://localhost:8080/download2?fileName=test.txt
	 * @param fileName
	 * @return
	 * @throws IOException
	 */
	@RequestMapping("/download2")
	public ResponseEntity<ByteArrayResource> downloadFile2(
			@RequestParam(defaultValue = FILE_NAME) String fileName) throws IOException{
		MediaType mediaType = getMediaTypeForFileName(fileName);
		Path path = Paths.get(DOWNLOAD_FOLDER + File.separator + fileName);
		byte[] data = Files.readAllBytes(path);
		ByteArrayResource resource = new ByteArrayResource(data);
		//한글파일명을 위한 설정
		String headerfileName = URLEncoder.encode(fileName, "UTF-8").replaceAll("\\+", "%20");
		
		return ResponseEntity.ok()
				// Content-Disposition
				.header(HttpHeaders.CONTENT_DISPOSITION, "attachement;filename=" + headerfileName)
				// Content-Type
				.contentType(mediaType)
				// Content-Length
				.contentLength(data.length)
				.body(resource);
	}
	
	/**
	 * HttpServletResponse를 이용한 방법
	 * http://localhost:8080/download3?fileName=test.txt
	 * @param response
	 * @param fileName
	 * @throws IOException
	 */
	@RequestMapping("/download3")
	public void downloadFile3(HttpServletResponse response, 
			@RequestParam(defaultValue = FILE_NAME) String fileName) throws IOException{
		MediaType mediaType = getMediaTypeForFileName(fileName);
		File file = new File(DOWNLOAD_FOLDER + File.separator + fileName);
		//한글파일명을 위한 설정
		String headerfileName = URLEncoder.encode(fileName, "UTF-8").replaceAll("\\+", "%20");
		response.setContentType(mediaType.getType());
		response.setHeader(HttpHeaders.CONTENT_DISPOSITION, "attachment;filename=" + headerfileName);
		response.setContentLength((int)file.length());
		
		BufferedInputStream inputStream = new BufferedInputStream(new FileInputStream(file));
		BufferedOutputStream outputStream = new BufferedOutputStream(response.getOutputStream());
		byte[] buffer = new byte[1024];
		int bytesRead = 0;
		while((bytesRead = inputStream.read(buffer)) != -1) {
			outputStream.write(buffer, 0, bytesRead);
		}
		outputStream.flush();
		inputStream.close();
	}
}
```
위에서는 3가지 방식으로 파일 다운로드를 구현하였다.  
첫 번째 방법은 ResponseEntity<InputStreamResource>를 이용한 방법이다.  
두 번째 방법은 ResponseEntity<ByteArrayResource>를 이용한 방법이다.  
세 번째 방법은 HttpServletResponse를 이용한 방법이다.  

## 결과 테스트
브라우저에서 다음 주소를 호출한다.  
http://localhost:8080/  
http://localhost:8080/download1?fileName=test.txt  
http://localhost:8080/download2?fileName=test.txt  
http://localhost:8080/download3?fileName=test.txt
 