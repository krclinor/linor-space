<%@ page language="java" contentType="text/html; charset=UTF-8"
    pageEncoding="UTF-8"%>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="sec" uri="http://www.springframework.org/security/tags" %>
<!DOCTYPE html>
<html>
<head>
	<meta charset="UTF-8">
	<title>스프링부트 보안</title>
</head>
<body>
	<p>여기는 리노님을 위한 페이지 입니다. 리노님(<sec:authentication property="name"/>)</p>
	<p><a href="<c:url value="/logout"/>">로그아웃</a></p>
</body>
</html>