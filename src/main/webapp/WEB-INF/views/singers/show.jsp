<%@ page language="java" contentType="text/html; charset=ISO-8859-1"
	pageEncoding="ISO-8859-1"%>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>
<%@ taglib prefix="spring" uri="http://www.springframework.org/tags"%>
<!DOCTYPE html>
<html>
<head>
<meta charset="ISO-8859-1">
<title>Singer</title>
<link
	href="https://cdn.jsdelivr.net/npm/bootstrap@5.1.3/dist/css/bootstrap.min.css"
	rel="stylesheet"
	integrity="sha384-1BmE4kWBq78iYhFldvKuhfTAU6auU8tT94WrHftjDbrCEXSU1oBoqyl2QvZ6jIW3"
	crossorigin="anonymous">
</head>
<spring:url value="/singers" var="showSingerUrl" />
<body bgcolor="#808080">
	<table
		class="table table-dark table-striped table-bordered align-middle">
		<!style="border: 1px solid black; margin-left: auto; margin-right: auto;">
		<tr>
			<td>Singer id:</td>
			<td>${singer.id}.</td>
			<td title="Singer image: "
				style="width: 700px; height: 700px; background-color: white; text-align: center; vertical-align: middle"><img
				src="data:image/jpg;base64,${singer.b64i}" alt="no image"
				style="max-height: 100%; max-width: 100%"></td>
			<td>Singer name:</td>
			<td>${singer.name}.</td>
	</table>
	<table
		class="table table-dark table-striped table-bordered align-middle">
		<tr>
			<td style="text-align: center; vertical-align: middle">				
					<a href="${showSingerUrl}" class="link-info">return to all list</a>
				<form action="update/${singer.id}">
					<input type="submit" value="edit data" class="btn btn-warning"/>
				</form>
				<form action="delete/${singer.id}" method="post">
					<input type="submit" value="delete user" class="btn btn-danger"/>
				</form>
			</td>
		</tr>
	</table>
</body>
</html>