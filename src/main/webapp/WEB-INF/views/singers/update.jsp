<%@ page language="java" contentType="text/html; charset=ISO-8859-1"
	pageEncoding="ISO-8859-1"%>

<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>
<%@ taglib prefix="spring" uri="http://www.springframework.org/tags"%>
<%@ page import="java.util.Base64"%>

<!DOCTYPE html>
<html>
<head>
<meta charset="ISO-8859-1">
<title>Insert title here</title>
<link
	href="https://cdn.jsdelivr.net/npm/bootstrap@5.1.3/dist/css/bootstrap.min.css"
	rel="stylesheet"
	integrity="sha384-1BmE4kWBq78iYhFldvKuhfTAU6auU8tT94WrHftjDbrCEXSU1oBoqyl2QvZ6jIW3"
	crossorigin="anonymous">
</head>
<body>
	<table class="table table-dark table-striped table-bordered align-middle table-sm">
		<!style="border: 1px solid black; margin-left: auto; margin-right: auto;">
		<tr><td align="center">
		<h1>${singer.id}</h1>
		<h2>${singer.name}</h2>
		<form method="POST" enctype="multipart/form-data">
			<p />
			New name: <input type="text" name="name" />
			<p />
			New image: <label for="file"></label> <input type="file" name="image" />
			<p />
			<p>
			Old image: <img src="data:image/jpg;base64,${singer.b64i}" alt="no image" width=30% height=30%/>
			</p>
			<input type="submit" value="Submit" /> <input type="reset"
				value="reset" />
		</form>
		</td></tr>
	</table>

</body>
</html>