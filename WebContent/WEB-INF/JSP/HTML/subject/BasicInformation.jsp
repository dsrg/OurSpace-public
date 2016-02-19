<%@ page contentType="text/html; charset=UTF-8" language="java"%>
<%@ page import="org.dsrg.ourSpace.wea.domLogic.*"%>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>
<%-- Init script must be here if we want it to be executed when the HTML fragment
     in this file is inserted into the main page DOM via an AJAX call. --%>
<%@include file="../include/SubjectFormInit.jsp"%>
<form id="subjectForm${subject.id}" method="post">
	<fieldset>
		<legend>Basic information about you</legend>
		<label for="<%=Attr.firstName%>">First name</label>
		<%-- --%>
		<input name="<%=Attr.firstName%>" type="text" size="32" maxlength="64"
			required ${disabled}
			value="${subject.attr.firstName}" autofocus> <br>
		<%-- --%>
		<label for="<%=Attr.gender%>">Gender</label>
		<%-- --%>
		<select name="<%=Attr.gender%>"
			data-init-value="${subject.attr.gender}" ${disabled} required>
			<%@include file="../include/ChooseOption.jsp"%>
			<option value="<%=Attr.Male%>">Male</option>
			<option value="<%=Attr.Female%>">Female</option>
		</select>
	</fieldset>
	<%-- --%>
	<%@include file="../include/NextButtonFragment.jsp"%>
</form>
<%@include file="../include/PrevButtonFragment.jsp"%>
