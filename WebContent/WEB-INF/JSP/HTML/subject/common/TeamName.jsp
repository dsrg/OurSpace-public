<%@ page contentType="text/html; charset=UTF-8" language="java"%>
<%@ page import="org.dsrg.ourSpace.wea.domLogic.*"%>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>
<script>
	function transferOthersChoice(subjectForm, otherSubjectForm) {
		var ossli = $(subjectForm).find(".partener-suggestion");
		var input = $(otherSubjectForm).find("[name='suggestedTeamName']");
		if ($(input).length == 0)
			return; // in case other subject is not on this page.
		var val = $(input).val();
		val = $.trim(val);
		// console.log('transferOthersChoice called: "' + val + '"');
		if (val) {
			$(ossli).text(val);
			if (!$(subjectForm).find("[name='okWithTeamName']").is(':checked'))
				$(ossli).removeClass("ui-state-disabled");
		} else {
			val = $(input).prop("placeholder");
			val = val.replace("Your", "Your partner's")
			$(ossli).html('<span class="not-filled-in">' + val + '</span>');
			$(ossli).addClass("ui-state-disabled");
			$(subjectForm).find('ul').append(ossli);
			$(ossli).each(setTeamChoiceHandler);
		}
	}
	$(document).ready(
			function() {
				if ('${role}' != 'EXPERIMENTER') {
					transferOthersChoice($("#subjectForm${subject.id}"),
							$("#subjectForm${otherSubject.id}"));
				}
			});
</script>
<h2>Choosing a name to represent your team.</h2>
<p>The highest ranking name, when taking both partner's choices into
	account, is:</p>
<div id="teamName">
	<p>${experiment.teamName}</p>
</div>
