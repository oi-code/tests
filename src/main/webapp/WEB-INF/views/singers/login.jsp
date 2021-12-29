<!DOCTYPE html PUBLIC "-//W3C//DTD XHTML 1.1//EN"
"http://www.w3.org/TR/xhtml11/DTD/xhtml11.dtd">
<html xmlns="http://www.w3.org/1999/xhtml"
	xmlns:sec="http://www.springframework.org/security/tags"
	xmlns:spring="http://www.springframework.org/tags"
	xmlns:c="http://xmlns.jcp.org/jsp/jstl/core"	
	xmlns:h="http://xmlns.jcp.org/jsf/html"
	xmlns:p="http://primefaces.org/ui">
<head>
<meta charset="ISO-8859-1" />
<title>Insert title here</title>
</head>
<body>
	<sec:authorize access="isAuthenticated()">
		<sec:authentication property="principal.username" />
	</sec:authorize>
	<h1 align="center">Login</h1>
	<form name='f' action="login" method="post">
		<table align="center">
			<tr>
				<td>User:</td>
				<td><input type='text' name='username' value='1' /></td>
			</tr>
			<tr>
				<td>Password:</td>
				<td><input type='password' name='password' value="1" /></td>
			</tr>
			<tr>
				<td><input name="submit" type="submit" value="submit" /></td>
			</tr>
		</table>
	</form>
</body>
</html>