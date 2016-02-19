<%@page contentType="text/html; charset=UTF-8" language="java"%>
<%@page isErrorPage="true"%>
<%@taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>
<!doctype html>
<html>
<head>
<meta charset="UTF-8">
<title>Error</title>
</head>
<body>
	<h1>An internal error has occurred.</h1>
	<p>Communicate the following information to your system
		administrator:</p>
	<c:if test="${!empty errorMessage}">
		<h3>Error</h3>
		<pre>
		<c:out value='${errorMessage}' />
		</pre>
	</c:if>
	<c:if test="${!empty warningMessage}">
		<h3>Warning</h3>
		<pre>
		<c:out value='${warningMessage}' />
		</pre>
	</c:if>
	<h3>${pageContext.exception.message}</h3>
	<pre>
 	<c:forEach var="frame" items="${pageContext.exception.stackTrace}">
			<c:out value='${frame}' />
	</c:forEach>
</pre>
</body>
</html>
