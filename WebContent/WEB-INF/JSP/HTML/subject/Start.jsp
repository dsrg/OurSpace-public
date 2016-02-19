<%@ page contentType="text/html; charset=UTF-8" language="java"%>
<%@ page import="org.dsrg.ourSpace.wea.domLogic.*"%>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>
<p>Press the Start button to begin. Use the chat area below at any
	time during this study to ask questions to the experimenter or raise
	any concerns that you might have.</p>
<form method="post" action="">
	<input type="submit" name="Next" value="Start" ${disabled}> 
	<input type="hidden" name="<%=Attr.cmd%>" value="<%=Page.NextPage%>">
</form>
