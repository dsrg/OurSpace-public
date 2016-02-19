<%@ page contentType="text/html; charset=UTF-8" language="java"%>
<%@ page import="org.dsrg.ourSpace.wea.domLogic.*"%>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>
<script>
	
<%-- 
	function updateTeamNorm() {
		if (!$.isNumeric(this.value)) {
			alert("The value '"
					+ value
					+ "' is not recognized as a number. Please provide a numeric value.");
			return;
		}
		var myEEL = parseInt(this.value, 10);
		var partnersEEL = 5; // Hardcoded as a bogus value for now.
		var average = (partnersEEL + myEEL) / 2;
		$('#teamNorm p').text(
		// this.value + " " + myEEL + " " + partnersEEL + " " + average + " = " +
		average.toFixed(1));
	}
--%>
	function okWithExpectedTeamEffortHandler(form) {
		return function(event) {
			var disable = $(form).find("[name='<%=Attr.okWithExpectedTeamEffort%>']").is(
					':checked');
			$(form).find("[name='<%=Attr.myExpectedEffortLevel%>']").prop('disabled',
					disable);
		};
	}
	$(document).ready(function() {
		var form = $("#subjectForm${subject.id}");
		var input = $(form).find("[name='<%=Attr.myExpectedEffortLevel%>']");
		if ($(input).filter(':not([data-disabled="disabled"])').length == 0)
			return;
		var elt = $(form).find('[name="<%=Attr.okWithExpectedTeamEffort%>"]');
		$(elt).off();
		$(elt).change(okWithExpectedTeamEffortHandler(form));
	});
</script>
<jsp:include page="../include/InitScriptAndAvatarHeader.jsp" />
<form id="subjectForm${subject.id}" method="post">
	<fieldset>
		<legend>Team Norm</legend>
		<p>On a scale of 0-10 (with 0 being no effort and 10 being the
			maximum effort possible), what level of effort do you expect from the
			members of your team (including yourself)?</p>
		<label for="effort">Expected effort:</label>
		<%--  --%>
		<input name="<%=Attr.myExpectedEffortLevel%>" type="number" min="0"
			max="10" value="${subject.attr.myExpectedEffortLevel}"
			data-disabled="${disabled}" autofocus
			required ${subject.attr.okWithExpectedTeamEffort ? 'disabled' : disabled} />
		<%-- --%>
		<p>Based on individual expectations an expected “team effort
			level” will be computed (by averaging).</p>
		<%-- --%>
		<label for="okWithExpectedTeamEffort">I agree with the team's
			computed expected effort.</label>
		<%-- --%>
		<input type="checkbox" name="<%=Attr.okWithExpectedTeamEffort%>" value="true"
			${subject.attr.okWithExpectedTeamEffort ? 'checked' : ''} ${disabled}
			required>
	</fieldset>
	<div class="clearBoth">
		<%@include file="../include/NextButtonFragment.jsp"%>
	</div>
</form>
<%@include file="../include/PrevButtonFragment.jsp"%>
