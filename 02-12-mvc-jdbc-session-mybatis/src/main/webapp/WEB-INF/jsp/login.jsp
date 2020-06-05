<%@ page language="java" contentType="text/html; charset=UTF-8"
    pageEncoding="UTF-8"%>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="sec" uri="http://www.springframework.org/security/tags" %>
<!DOCTYPE html>
<html>
<head>
	<meta charset="UTF-8">
	<title>스프링 부트 보안</title>
</head>
<body>
	<form action="<c:url value="/login"/>" method="POST">
		<font color="red">
			${SPRING_SECURITY_LAST_EXCEPTION.message}
		</font><br/>
		<input type="text" name="username" placeholder="사용자ID"/>
		<input type="password" name="password" placeholder="비밀번호"/>
		<sec:csrfInput /> 
		<button type="submit">로그인</button>
	</form>
</body>
</html>