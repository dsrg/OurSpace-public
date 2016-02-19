<%@ page contentType="text/html; charset=UTF-8" language="java"%>
<%@ page import="org.dsrg.ourSpace.wea.domLogic.*"%>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>
<script>
	function updateSuggestedTeamName(event) {
		var form = $(this).closest('form');
		var ssli = $(form).find(".subject-suggestion");
		var val = $(this).val();
		val = $.trim(val);
		console.log('updateSuggestedTeamName called: "' + val + '"');
		if (val) {
			$(ssli).text(val);
			$(ssli).removeClass("ui-state-disabled");
		} else {
			var input = $(form).find("[name='suggestedTeamName']");
			val = $(input).prop("placeholder");
			$(ssli).html('<span class="not-filled-in">' + val + '</span>');
			$(ssli).addClass("ui-state-disabled");
			$(form).find('ul').append(ssli);
			$(ssli).each(setTeamChoiceHandler);
		}
	}
	// =========================================================================
	/* This handler only deals with enabling/disabling the #sortable.
	 * The transmission of the checkbox value is handled by another
	 * handler set by the subjectInit() method.
	 */
	function okWithTeamChoiceCheckboxHandler() {
		// Setting of subject teamInsignia will be done on the server
		// when there is a page transition.
		var form = $(this).closest('form');
		enableOrDisableTeamList($(form));
	}
	function enableOrDisableTeamList(form) {
		var checkboxName = "okWithTeamName"; // *****************************
		var checkbox = $(form).find("[name='" + checkboxName + "']");
		var teamChoiceList = $(form).find("#sortable");
		var disabled = $(teamChoiceList).data('disabled');
		var items = $(teamChoiceList).find("li");
		$(items).filter(':not([data-disabled="disabled"])').removeClass(
				"ui-state-disabled"); // *****************************
		if (disabled || $(checkbox).is(':checked')) {
			$(items).addClass("ui-state-disabled");
			$(form).find("[name='suggestedTeamName']").prop('disabled', true);
		} //****
		else {
			$(form).find("[name='suggestedTeamName']").prop('disabled', false);
		}
	}
	function setTeamChoiceHandler(event, ui) {
		var choiceListName = "teamNameList"; // *****************************
		var form = $(this).closest('form');
		var names = getChoiceNamesAsArray(form);
		var teamChoiceList = names.join(',');
		$(form).find("input[name='" + choiceListName + "']")
				.val(teamChoiceList);
		// console.log('setTeamChoiceHandler: ' + teamChoiceList);
		postSubjectAttr(choiceListName, teamChoiceList);
	}
	function getChoiceNamesAsArray(form) { // *****************************
		var names = [];
		$(form).find("ul.team-name li").each(function() {
			names.push($(this).data('name'));
		});
		return names;
	}
	function teamChoiceInit(form) {
		// **************************
		var input = $(form).find("[name='suggestedTeamName']");
		$(input).on("input", null, null, updateSuggestedTeamName);
		// **************************
		var checkboxName = "okWithTeamName"; // *****************************
		var checkbox = $(form).find("[name='" + checkboxName + "']");
		$(checkbox).change(okWithTeamChoiceCheckboxHandler);
		enableOrDisableTeamList(form);
		var teamChoiceList = $(form).find("#sortable");
		$(teamChoiceList).sortable({
			items : "li:not(.ui-state-disabled)",
			update : setTeamChoiceHandler,
		/*cancel : ".ui-state-disabled"*/
		});
	}
	$(document).ready(function() {
		teamChoiceInit($("#subjectForm${subject.id}"));
	});
</script>
<jsp:include page="../include/InitScriptAndAvatarHeader.jsp" />
<form id="subjectForm${subject.id}" method="post">
	<fieldset>
		<legend>Choose a team name</legend>
		<p>Drag the suggested team names below to reorder them. Your
			preferred name should be the first (top-most), in the list, with name
			preference decreasing as you go down.</p>
		<p>
			If you would like to suggest a team name (which will be added to the
			list) enter it here:
			<%-- --%>
			<input name="<%=Attr.suggestedTeamName%>" type="text" class="fill"
				placeholder="Your suggested team name (optional)" autofocus
				value="${subject.attr.suggestedTeamName}"
				${subject.attr.okWithTeamName ? 'disabled' : disabled}>
		</p>
		<p>
			You do not need to reorder them all, but at least place your <b>top
				three</b> choices first.
		</p>
		<c:set var="ystn" value="<%=Attr.YourSuggestedTeamName%>"
			scope="request" />
		<c:set var="pstn" value="<%=Attr.PartnersSuggestedTeamName%>"
			scope="request" />
		<ul id="sortable" class="team-name" data-disabled="${disabled}">
			<c:set var="teamAttr" value="<%=Attr.teamName%>" scope="request" />
			<c:forEach var="teamName"
				items="${subject.getTeamAttrList(teamAttr)}">
				<c:choose>
					<c:when test="${teamName == ystn}">
						<c:choose>
							<c:when test="${!empty subject.attr.suggestedTeamName}">
								<li data-name="${teamName}"
									class="ui-state-default subject-suggestion">
									${subject.attr.suggestedTeamName}</li>
							</c:when>
							<c:otherwise>
								<li data-name="${teamName}" data-disabled="disabled"
									class="ui-state-default ui-state-disabled subject-suggestion">
									<span class="not-filled-in">Your suggested name (if any)</span>
								</li>
							</c:otherwise>
						</c:choose>
					</c:when>
					<c:when test="${teamName == pstn}">
						<c:choose>
							<c:when test="${!empty otherSubject.attr.suggestedTeamName}">
								<li data-name="${teamName}"
									class="ui-state-default partener-suggestion">
									${otherSubject.attr.suggestedTeamName}</li>
							</c:when>
							<c:otherwise>
								<li data-name="${teamName}" data-disabled="disabled"
									class="ui-state-default ui-state-disabled partener-suggestion">
									<span class="not-filled-in">Your partner's suggested
										name (if any)</span>
								</li>
							</c:otherwise>
						</c:choose>
					</c:when>
					<c:otherwise>
						<li class="ui-state-default" data-name="${teamName}">${teamName.title()}</li>
					</c:otherwise>
				</c:choose>
			</c:forEach>
			<%--
			<li class="ui-state-default">The "A" Team</li>
			<li class="ui-state-default">Wildcats</li>
			<li class="ui-state-default">Team America</li>
			<li class="ui-state-default">The Planksters</li>
			<c:if test="${teamName == YourTeamName}">
				<c:if test="${teamName == YourTeamName}">
				</c:if>
			</c:if>
			<li
				class="ui-state-default ui-state-disabled subject-suggestion not-filled-in">Your
				suggested name (if any)</li>
			<li
				class="ui-state-default ui-state-disabled partener-suggestion not-filled-in">Your
				partner's suggested name (if any)</li>
			 --%>
		</ul>
	</fieldset>
	<div class="clearBoth">
		<br>
		<%-- --%>
		<label for="<%=Attr.okWithTeamName%>">I agree with the <strong>team
				name</strong> we have selected (uncheck to continue editing).
		</label>
		<%-- --%>
		<input type="checkbox" name="<%=Attr.okWithTeamName%>" value="true"
			${subject.attr.okWithTeamName ? 'checked' : ''} ${disabled} required>
		<%-- --%>
		<br> <br>
		<%-- --%>
		<input type="hidden" name="<%=Attr.teamNameList%>"
			value="${subject.attr.teamNameList}">
		<%-- --%>
		<%@include file="../include/NextButtonFragment.jsp"%>
	</div>
</form>
<%@include file="../include/PrevButtonFragment.jsp"%>
