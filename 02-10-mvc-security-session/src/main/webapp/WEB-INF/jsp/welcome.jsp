<%@ page language="java" contentType="text/html; charset=UTF-8"
    pageEncoding="UTF-8"%>
<!DOCTYPE html>
<%@ taglib prefix="spring" uri="http://www.springframework.org/tags" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<html>
<head>
<meta charset="UTF-8">
<title>환영합니다!!</title>
	<link rel="stylesheet" type="text/css" href="webjars/bootstrap/4.5.0/css/bootstrap.min.css"/>
	<c:url value="/css/main.css" var="jstlCss" />
	<link href="${jstlCss}" rel="stylesheet"/> 
	<script src="/webjars/jquery/3.5.1/jquery.min.js"></script>
	<script src="/webjars/popper.js/1.16.0/umd/popper.min.js"></script>
	<script src="/webjars/bootstrap/4.5.0/js/bootstrap.min.js"></script>
</head>
	<nav class="navbar navbar-expand-lg navbar-light bg-light">
		<a class="navbar-brand" href="#">스프링 부트</a>
		<button class="navbar-toggler" type="button" 
				data-toggle="collapse" 
				data-target="#navbarSupportedContent" 
				aria-controls="navbarSupportedContent" 
				aria-expanded="false" aria-label="Toggle navigation">
			<span class="navbar-toggler-icon"></span>
		</button>
		<div class="collapse navbar-collapse" id="navbarSupportedContent">
			<ul class="navbar-nav mr-auto">
				<li class="nav-item active">
					<a class="nav-link" href="/home">홈 <span class="sr-only">(current)</a>
				</li>
				<li class="nav-item">
					<a class="nav-link" href="/admin/home">관리자 홈</a>
				</li>
				<li class="nav-item">
					<a class="nav-link" href="/user/linor/home">리노님 전용공간</a>
				</li>
			</ul>
		</div>
	</nav>
	<div class="container">
		<div class="starter-template">
			<h1>Spring Boot 웹 JSP 예제</h1>
			<h2>메시지 : ${message}</h2>
		</div>
	</div>
</body>
</html>