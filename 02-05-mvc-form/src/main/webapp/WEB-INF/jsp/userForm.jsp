<%@ page language="java" contentType="text/html; charset=UTF-8"
    pageEncoding="UTF-8"%>
<%@ taglib uri="http://www.springframework.org/tags/form" prefix="form" %>
<!DOCTYPE html>
<html>
<head>
<meta charset="UTF-8">
<title>MVC 폼처리 예제</title>
</head>
<body>
<h2>사용자 등록 폼</h2>
<form:form modelAttribute="user" action="result">
	<table>
		<tr>
			<td><form:label path="lastName">성</form:label></td>
			<td><form:input path="lastName"/></td>
		</tr>
		<tr>
			<td><form:label path="name">이름</form:label></td>
			<td><form:input path="name"/></td>
		</tr>
		<tr>
			<td><form:label path="email">이메일</form:label></td>
			<td><form:input path="email"/></td>
		</tr>
		<tr>
			<td><form:label path="password">비밀번호</form:label></td>
			<td><form:password path="password"/></td>
		</tr>
		<tr>
			<td><form:label path="detail">소개</form:label></td>
			<td><form:textarea path="detail"/></td>
		</tr>
		<tr>
			<td><form:label path="birthDate">생일</form:label></td>
			<td><form:input path="birthDate"/></td>
		</tr>
		<tr>
			<td><form:label path="gender">성별</form:label></td>
			<td>
				<form:radiobuttons path="gender" items="${genders}" itemValue="key" itemLabel="value"/>
			 </td>
		</tr>
		<tr>
			<td><form:label path="country">국가</form:label></td>
			<td><form:select path="country" items="${countries}"/></td>
		</tr>
		<tr>
			<td><form:label path="nonSmoking">금연여부</form:label></td>
			<td><form:checkbox path="nonSmoking"/></td>
		</tr>
		<tr>
			<td><form:label path="salary">월급여</form:label></td>
			<td><form:input path="salary"/></td>
		</tr>
		<tr>
			<td colspan="2"><input type="submit" value="전송"/>
		</tr>
	</table>
</form:form>	
</body>
</html>