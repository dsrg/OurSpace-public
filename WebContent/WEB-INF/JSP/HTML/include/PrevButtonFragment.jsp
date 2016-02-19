<%@ page contentType="text/html; charset=UTF-8" language="java"%>
<%@ page import="org.dsrg.ourSpace.wea.domLogic.*"%>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>
<c:if test="${!empty developerMode }">
	<form method="post">
		<input type="submit" name="Prev" value="Prev" ${disabled}> <input
			type="hidden" name="<%=Attr.cmd%>" value="<%=Page.PrevPage%>">
	</form>
</c:if>
