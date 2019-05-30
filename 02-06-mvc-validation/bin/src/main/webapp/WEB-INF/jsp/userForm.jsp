<%@ page language="java" contentType="text/html; charset=UTF-8"
    pageEncoding="UTF-8"%>
<%@ taglib uri="http://www.springframework.org/tags/form" prefix="form" %>
<!DOCTYPE html>
<html>
<head>
<meta charset="UTF-8">
<title>MVC 폼처리 예제</title>
<style type="text/css">
	.formFieldError {background-color: #ffffcc;}
</style>
</head>
<body>
<h2>사용자 등록 폼</h2>
<form:form modelAttribute="user" action="result">
	<table>
		<tr>
			<td><form:label path="lastName">성</form:label></td>
			<td><form:input path="lastName" cssErrorClass="formFieldError"/></td>
			<td><form:errors path="lastName"/></td>
		</tr>
		<tr>
			<td><form:label path="name">이름</form:label></td>
			<td><form:input path="name" cssErrorClass="formFieldError"/></td>
			<td><form:errors path="name"/></td>
		</tr>
		<tr>
			<td><form:label path="email">이메일</form:label></td>
			<td><form:input path="email"/></td>
			<td><form:errors path="email"/></td>
		</tr>
		<tr>
			<td><form:label path="password">비밀번호</form:label></td>
			<td><form:password path="password" cssErrorClass="formFieldError"/></td>
			<td><form:errors path="password"/></td>
		</tr>
		<tr>
			<td><form:label path="detail">소개</form:label></td>
			<td colspan="2"><form:textarea path="detail"/></td>
		</tr>
		<tr>
			<td><form:label path="birthDate">생일</form:label></td>
			<td><form:input path="birthDate" cssErrorClass="formFieldError"/></td>
			<td><form:errors path="birthDate"/></td>
		</tr>
		<tr>
			<td><form:label path="gender">성별</form:label></td>
			<td colspan="2"><form:select path="gender" items="${genders}"/></td>
		</tr>
		<tr>
			<td><form:label path="country">국가</form:label></td>
			<td colspan="2"><form:select path="country" items="${countries}"/></td>
		</tr>
		<tr>
			<td><form:label path="nonSmoking">금연여부</form:label></td>
			<td colspan="2"><form:checkbox path="nonSmoking"/></td>
		</tr>
		<tr>
			<td><form:label path="salary">월급여</form:label></td>
			<td><form:input path="salary"/></td>
			<td><form:errors path="salary"/></td>
		</tr>
		<tr>
			<td colspan="3"><input type="submit" value="전송"/>
		</tr>
	</table>
</form:form>	
</body>
</html>