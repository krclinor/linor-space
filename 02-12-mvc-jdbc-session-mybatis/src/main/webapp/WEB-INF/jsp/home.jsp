<%@ page language="java" contentType="text/html; charset=UTF-8"
    pageEncoding="UTF-8"%>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="sec" uri="http://www.springframework.org/security/tags" %>
<!DOCTYPE html>
<head>
	<meta charset="UTF-8">
	<title>스프링 부트 보안</title>
</head>
<body>
	<p>환영합니다 <sec:authentication property="name"/>님!!</p>
	<p><a href="<c:url value="/logout"/>">로그아웃</a></p>
	<sec:authorize access="hasRole('ROLE_ADMIN')">
		<h3>ADMIN 권한만 이 메시지가 표시됩니다.</h3>
		<p><a href="<c:url value='/admin/home'/>">관리자 홈</a></p>
	</sec:authorize>
	<p><a href="<c:url value='/admin/home'/>">관리자 홈</a></p>
</body>
