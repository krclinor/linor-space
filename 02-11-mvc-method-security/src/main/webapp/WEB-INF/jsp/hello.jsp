<%@ page language="java" contentType="text/html; charset=UTF-8"
    pageEncoding="UTF-8"%>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<!DOCTYPE html>
<html>
<head>
<meta charset="UTF-8">
<title>인사메시지</title>
</head>
<body>
	${message}
	<p/>
	<a href="<c:url value="/logout"/>">로그아웃</a>
</body>
</html>