<!DOCTYPE html PUBLIC "-//W3C//DTD XHTML 1.1//EN"
"http://www.w3.org/TR/xhtml11/DTD/xhtml11.dtd">
<html xmlns="http://www.w3.org/1999/xhtml"
	xmlns:c="http://xmlns.jcp.org/jsp/jstl/core"
	xmlns:spring="http://www.springframework.org/tags"
	xmlns:h="http://xmlns.jcp.org/jsf/html">
<head>
<meta charset="ISO-8859-1" />
<title>Insert title here</title>
<link
	href="https://cdn.jsdelivr.net/npm/bootstrap@5.1.3/dist/css/bootstrap.min.css"
	rel="stylesheet"
	integrity="sha384-1BmE4kWBq78iYhFldvKuhfTAU6auU8tT94WrHftjDbrCEXSU1oBoqyl2QvZ6jIW3"
	crossorigin="anonymous" />
</head>
<body>
	<table
		class="table table-dark table-striped table-bordered align-middle table-sm">
		<!--style="border: 1px solid black; margin-left: auto; margin-right: auto;"-->
		<tr>
			<td align="center" place-content="center">
				<h1>${singer.id}</h1>
				<h2>${singer.name}</h2>
				<form method="post" enctype="multipart/form-data">
					<!--input type="hidden" id="id" name = "id" value="${singer.id}" />
					<input type="hidden" id="oldName" name ="oldName" value="${singer.name}" />
					<input type="hidden" id="oldIimage" name="oldImage" value="${singer.image}" /-->
					<h:inputHidden id="oldImage" name="oldImage" value="${singer.image}"></h:inputHidden>
					New name:
					<input id="name" type="text" name="name" value="${singer.name}"/> New image:
					<label for="file"></label>
					<input type="file" name="miltipartImage"/> Old image:
					<img src="data:image/jpg;base64,${singer.image}" alt="no image"
						width="30%" height="30%" />
					<input type="submit" value="Submit" />
					<input type="reset" value="reset" />
				</form>
			</td>
		</tr>
	</table>

</body>
</html>