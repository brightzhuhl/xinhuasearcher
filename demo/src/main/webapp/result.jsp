<%@ page language="java" contentType="text/html; utf-8"
    pageEncoding="utf-8"%>
<%@taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>  
<!DOCTYPE html PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN" "http://www.w3.org/TR/html4/loose.dtd">
<html>
<head>
<meta http-equiv="Content-Type" content="text/html; utf-8">
<title>新华日报搜索工具</title>
</head>
<body>
	<ul>
	<c:forEach var="ite" items="${result}">
		<li onclick="javascript:window.open('${ite.url}')">
			<h3>${ite.title}</h3>
			<p>${ite.content}</p>
			<span>${ite.date}</span>
			<span>${ite.score}</span>
		</li>
	</c:forEach>
	</ul>
</body>
</html>