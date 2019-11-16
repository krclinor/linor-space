# 파일 업로드하기

## Spring Boot Starter를 이용한 프로젝트 생성
Spring boot Starter로 프로젝트 생성시 패키징은 war로 설정한다.
```xml
	<packaging>war</packaging>
```

### 의존성 라이브러리
Spring initializer로 생성시 기본 dependency는 Web, DevTools, Lombok를 선택한다.
프로젝트 생성 후 pom.xml에 JSP사용을 위해 tomcat-jasper, jstl을 추가하고, CSS프레임워크인 bootstrap을 추가한다.
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
sspring:
  mvc:
    view:
      prefix: /WEB-INF/jsp/
      suffix: .jsp
  servlet:
    multipart: #파일 업로드 설정
      enabled: true
      max-file-size: 10MB
      max-request-size: 100MB
      file-size-threshold: 5MB

#업로드파일 저장 위치
myapp.upload-folder: /temp/
```
servlet.multipart를 이용하여 업로드를 설정할 수 있다
-  enabled : true로 설정하여 파일업로드를 사용하도록 한다.
-  max-file-size: 파일당 최대 크기를 지정한다.
-  max-request-size: 요청 처리당 여러파일 업로드 시 최대 크기를 지정한다.
-  file-size-threshold:  업로드하는 파일이 임시로 파일로 저장되지 않고 메모리에서 바로 스트림으로 전달되는 크기의 한계를 지정한다. 5MB로 지정하였으므로 5MB이상인 경우에만 임시파일로 저장된다.  

myapp.upload-folder는 프로그램에서 사용하기 위해 만든 것으로 업로드된 파일을 저장할 폴더 위치를 지정한다.  

```bash
linor@mylinor:~$ sudo mkdir /temp
linor@mylinor:~$ sudo chmod 777 /temp
```
업로드 파일을 저장할 수 있도록 /temp디렉토리를 생성하고 모드를 777로 설정하여 모든 사용자가 사용할 수 있도록 한다.  

### 컨트롤러 생성
소스 :[FileController.java](src/main/java/com/linor/singer/controller/FileController.java)  

```java
@Controller
@RequiredArgsConstructor
public class FileController {
	
	@Value("${myapp.upload-folder:/temp}")
	private String UPLOAD_FOLDER;
	
	@GetMapping("/")
	public String uploadForm() {
		return "fileUpload";
	}
	
	@PostMapping("/uploadMyFile")
	public String uploadFile(@RequestParam("myFile") MultipartFile file, ModelMap modelMap) {
		if(!file.isEmpty()) {
			try {
				file.transferTo(new File( UPLOAD_FOLDER + File.separator + file.getOriginalFilename()));
				modelMap.addAttribute("file", file);
				return "viewUploadFile";
			}catch(Exception e) {
				e.printStackTrace();
			}
		}
		return "redirect:/";
	}
}
```
uploadForm()메서드는 업로드 폼 뷰 화면으로 이동하도록 한다.  
업로드 화면에서 파일을 선택하고 전송버튼을 클릭하면 uploadFile메서드를 호출한다.  
파일을 /temp 폴더에 저장하고 결과를 화면에 보여주도록 viewUploadFile뷰를 호출한다.

### 업로드 Form 뷰 JSP
소스 [fileUpload.jsp](src/main/webapp/WEB-INF/jsp/fileUpload.jsp)
```jsp
<%@ page language="java" contentType="text/html; charset=UTF-8"
    pageEncoding="UTF-8"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>
<!DOCTYPE html>
<html>
<head>
<meta charset="UTF-8">
<title>파일 업로드</title>
</head>
<body>
	<div>
		<h2>파일 업로드</h2>
		<form action="/uploadMyFile" 
			method="post" enctype="multipart/form-data">
			<input type="file" name="myFile"/>
			<button type="submit">전송</button>
		</form>
		<c:if test="${msg != null }">
			${msg}
		</c:if>
	</div>
</body>
</html>
```

```jsp
		<form action="/uploadMyFile" 
			method="post" enctype="multipart/form-data">
			<input type="file" name="myFile"/>
			<button type="submit">전송</button>
		</form>
```
파일 업로드를 위해서는 enctype을 "multipart/form-data"로 지정하고 method를 "post"로 지정해야 한다.  

## 결과 테스트
브라우저에서 다음 주소를 호출한다.  
http://localhost:8080
 