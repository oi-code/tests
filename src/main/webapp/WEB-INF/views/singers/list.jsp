<%@ page language="java" contentType="text/html; charset=ISO-8859-1"
	pageEncoding="ISO-8859-1"%>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>
<%@ taglib prefix="spring" uri="http://www.springframework.org/tags"%>
<!DOCTYPE html>
<html>
<head>
<meta charset="ISO-8859-1">
<title>Singer List</title>
<link
	href="https://cdn.jsdelivr.net/npm/bootstrap@5.1.3/dist/css/bootstrap.min.css"
	rel="stylesheet"
	integrity="sha384-1BmE4kWBq78iYhFldvKuhfTAU6auU8tT94WrHftjDbrCEXSU1oBoqyl2QvZ6jIW3"
	crossorigin="anonymous">
</head>
<body>
	<table
		class="table table-dark table-striped table-bordered align-middle">
		<!style="border: 1px solid black; margin-left: auto; margin-right: auto;">
		<tr>
			<td></td>
			<td></td>
			<td></td>
			<td></td>
			<td>
				<form action="singers/new" method="get">
					<input type="submit" value="create new user" />					
				</form>
				<form action="singers/websocket" method="get">
					<input type="submit" value="websocket test"/>
				</form> 
			</td>
			<td></td>
			<td></td>
			<td></td>
		</tr>
		<c:forEach items="${list}" var="it">
			<tr>
				<td>Singer id:</td>
				<td>${it.id}.</td>
				<td>Singer image</td>
				<td
					style="width: 250px; height: 250px; background-color: transient; text-align: center; vertical-align: middle"><img
					src="data:image/jpg;base64,${it.b64i}" alt="no image"
					style="max-height: 100%; max-width: 100%"></td>
				<td>Singer name:</td>
				<td>${it.name}.</td>
				<td><spring:url value="/singers" var="showSingerUrl" /> <a
					href="${showSingerUrl}/${it.id}">show this on an individual
						page</a></td>

				<td><form action="singers/delete/${it.id}" method="post">
						<input type="submit" value="delete user: ${it.name}, id: ${it.id}" class="btn btn-danger" />
					</form></td>

			</tr>
		</c:forEach>
	</table>
</body>
</html>