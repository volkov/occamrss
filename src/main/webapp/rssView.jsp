<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<html>
<head>
<%@ page
language="java"
contentType="text/html; charset=UTF-8"
pageEncoding="UTF-8"
%>
<meta http-equiv="content-type" content="text/html; charset=UTF-8">
</head>
<body>
	<h1>Occam RSS aggregator</h1>
 	<table border=2>
	<c:forEach items="${entryList}" var="entry">
		<td>
		<tr>
        <td><a href="${entry.link}">"${entry.title}"</a></td>
        <td>${entry.publishedDate}</td>
        </td>
	</c:forEach>
 	</table>
 </body>
</html>