<%@ page language="java" contentType="text/html; charset=UTF-8"
    pageEncoding="UTF-8"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>
<!DOCTYPE html>
<html>
<head>
<meta charset="UTF-8">
<title>업로드 결과</title>
</head>
<body>
	<table>
		<tr>
			<td>파일명:</td>
			<td>${file.originalFilename}</td>
		</tr>
		<tr>
			<td>타입:</td>
			<td>${file.contentType}</td>
		</tr>
		<tr>
			<td>길이:</td>
			<td>${file.size}</td>
		</tr>
	</table>
</body>
</html>