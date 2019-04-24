<%@ page language="java" contentType="text/html; charset=UTF-8"
    pageEncoding="UTF-8"%>
<!DOCTYPE html>
<%@ taglib prefix="spring" uri="http://www.springframework.org/tags" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<html>
<head>
<meta charset="UTF-8">
<title>환영합니다!!</title>
	<link rel="stylesheet" type="text/css" href="webjars/bootstrap/4.3.1/css/bootstrap.min.css"/>
</head>
<body>
	<div class="alert alert-primary" role="alert">
		좋아하는 새깔 목록<br>
		세션ID : ${sessionId}
	</div>
	<table class="table">
	  <thead>
	    <tr>
      		<th scope="col">색깔</th>
      	</tr>
     </thead>
      <tbody>
      	<c:forEach var="color" items="${favoriteColors}">
      	<tr>
      		<td>
      			${color}
      		</td>
      	</tr>
      	</c:forEach>
      </tbody>
	</table>	
	<form action="<c:url value="/saveColor"/>" method="post">
	  <div class="form-group">
	    <label for="color">좋아하는 색깔</label>
	    <input type="text" class="form-control" name="color" placeholder="색깔 입력">
	  </div>
	  <button type="submit" class="btn btn-primary">세션에 저장</button>
	  <a class="btn btn-secondary" href="<c:url value="/closeSession"/>">세션 종료</a>
	</form>
	<script type="text/javascript" src="webjar/bootstrap/4.3.1/js/bootstrap.min.js"></script>
</body>
</html>