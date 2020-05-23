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

		<h2>여러 파일 업로드</h2>
		<form action="/uploadMyFiles" 
			method="post" enctype="multipart/form-data">
			<input type="file" name="myFiles" multiple/>
			<button type="submit">전송</button>
		</form>

		<c:if test="${msg != null }">
			${msg}
		</c:if>
	</div>
</body>
</html>