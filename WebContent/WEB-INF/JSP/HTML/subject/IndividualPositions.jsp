<%@ page contentType="text/html; charset=UTF-8" language="java"%>
<%@ page import="org.dsrg.ourSpace.wea.domLogic.*"%>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>
<jsp:include page="../include/InitScriptAndAvatarHeader.jsp" />
<form id="subjectForm${subject.id}" method="post">
	<fieldset>
		<legend>Individual Positions</legend>
		<p>Recall the series of plank exercises that you just performed.
			Below is the number of seconds, on average, that you held each plank.
			Your partner is shown his/her results as well.</p>
		<p>
			<label for="myPlankAverage">Your average plank duration
				(sec):</label>
			<%-- --%>
			<input type="text" name="plankDurationAverage" size="4"
				value="${subject.attr.plankDurationAverage}" disabled/>
		</p>
		<p>
			<label for="whoPerformedBetter">Who performed better?</label>
			<%-- --%>
			<select name="whoPerformedBetter"
				data-init-value="${subject.attr.whoPerformedBetter}" ${disabled}
				required>
				<%@include file="../include/ChooseOption.jsp"%>
				<option value="<%=Attr.self%>">me</option>
				<option value="<%=Attr.partner%>">my partner</option>
				<option value="<%=Attr.both%>">both have same performance</option>
			</select>
		</p>
	</fieldset>
	<div class="clearBoth">
		<%@include file="../include/NextButtonFragment.jsp"%>
	</div>
</form>
<%@include file="../include/PrevButtonFragment.jsp"%>
