<%@ page contentType="text/html; charset=UTF-8" language="java"%>
<%@ page import="org.dsrg.ourSpace.wea.domLogic.*"%>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>
<input type="hidden" name="<%=Attr.cmd%>" value="<%=Page.NextPage%>">
<input type="submit" name="Next"
	value="${empty subject.attr.subjectIsReadyAndWaiting ? 'Next' : 'Waiting for your partner' }"
	${empty nextShouldBeDisabled && empty disabled ? '' : 'disabled' }>
