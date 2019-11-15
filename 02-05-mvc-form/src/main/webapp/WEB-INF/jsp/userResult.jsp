<%@ page language="java" contentType="text/html; charset=UTF-8"
	pageEncoding="UTF-8"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/fmt" prefix="fmt"%>
<!DOCTYPE html>
<html>
<head>
<meta charset="UTF-8">
<title>MVC 폼처리 예제</title>
</head>
<body>
	<h2>사용자 등록 결과</h2>
	<table>
		<tr>
			<td>성</td>
			<td>${u.lastName}</td>
		</tr>
		<tr>
			<td>이름</td>
			<td>${u.name}</td>
		</tr>
		<tr>
			<td>비밀번호</td>
			<td>${u.password}</td>
		</tr>
		<tr>
			<td>소개</td>
			<td>${u.detail}</td>
		</tr>
		<tr>
			<td>생일</td>
			<fmt:parseDate value="${u.birthDate}" pattern="yyyy-MM-dd"
				var="parsedDate" type="date" />
			<td><fmt:formatDate value="${parsedDate}" pattern="yyyy.MM.dd" /></td>
		</tr>
		<tr>
			<td>성별</td>
			<td>${u.gender}(${u.gender.value})</td>
		</tr>
		<tr>
			<td>국가</td>
			<td>${u.country}</td>
		</tr>
		<tr>
			<td>금연여부</td>
			<td>${u.nonSmoking}</td>
		</tr>
		<tr>
			<td>월급여</td>
			<td><fmt:formatNumber value="${u.salary}" pattern="#,##0" /></td>
		</tr>
	</table>
</body>
</html>