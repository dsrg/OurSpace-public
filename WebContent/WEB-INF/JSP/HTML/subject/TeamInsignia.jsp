<%@ page contentType="text/html; charset=UTF-8" language="java"%>
<%@ page import="org.dsrg.ourSpace.wea.domLogic.*"%>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>
<script>
	/* 	function TeamChoiceMgr(checkboxName, choiceListName) {
	 }
	 var teamInsigniaManager = new TeamChoiceMgr();
	 */
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
		var checkboxName = "okWithTeamInsignia";
		var checkbox = $(form).find("[name='" + checkboxName + "']");
		var teamChoiceList = $(form).find("#sortable");
		var disabled = $(teamChoiceList).data('disabled');
		var items = $(teamChoiceList).find("li");
		$(items).removeClass("ui-state-disabled");
		if (disabled || $(checkbox).is(':checked')) {
			$(items).addClass("ui-state-disabled");
		}
	}
	function setTeamChoiceHandler(event, ui) {
		var choiceListName = "teamInsigniaList";
		var form = $(this).closest('form');
		var names = getChoiceNamesAsArray(form);
		var teamChoiceList = names.join(',');
		$(form).find("input[name='" + choiceListName + "']")
				.val(teamChoiceList);
		postSubjectAttr(choiceListName, teamChoiceList);
	}
	function getChoiceNamesAsArray(form) {
		var names = [];
		$(form).find("ul.team-insignia li img").each(function() {
			names.push($(this).attr('alt'));
		});
		return names;
	}
	function teamChoiceInit(form) {
		var checkboxName = "okWithTeamInsignia";
		var checkbox = $(form).find("[name='" + checkboxName + "']");
		$(checkbox).change(okWithTeamChoiceCheckboxHandler);
		enableOrDisableTeamList(form);
		var teamChoiceList = $(form).find("#sortable");
		$(teamChoiceList).sortable({
			update : setTeamChoiceHandler,
			cancel : ".ui-state-disabled"
		});
	}
	$(document).ready(function() {
		teamChoiceInit($("#subjectForm${subject.id}"));
	});
</script>
<jsp:include page="../include/InitScriptAndAvatarHeader.jsp" />
<form id="subjectForm${subject.id}" method="post">
	<fieldset>
		<legend>Choose a team insignia (visual symbol)</legend>
		<p>Drag the images to reorder them. Your preferred image should be
			the first (top-left), in the grid, with image preference decreasing
			as you go to the right and then down.</p>
		<p>
			You do not need to reorder them all, but at least place your <b>top
				three</b> choices first.
		</p>

		<ul id="sortable" class="team-insignia" data-disabled="${disabled}">
			<c:set var="teamAttr" value="<%=Attr.teamInsignia%>" scope="request" />
			<c:forEach var="teamInsigniaName" items="${subject.getTeamAttrList(teamAttr)}">
				<li class="ui-state-default"><img
					src="image/team/${teamInsigniaName}.jpg" alt="${teamInsigniaName}">
				</li>
			</c:forEach>
		</ul>
	</fieldset>
	<div class="clearBoth">
		<br>
		<%-- --%>
		<label for="<%=Attr.okWithTeamInsignia%>">I agree with the <strong>team
				insignia</strong> we have selected (uncheck to continue editing).
		</label>
		<%-- --%>
		<input type="checkbox" name="<%=Attr.okWithTeamInsignia%>"
			value="true" ${subject.attr.okWithTeamInsignia ? 'checked' : ''}
			${disabled} required>
		<%-- --%>
		<br> <br>
		<%-- --%>
		<input type="hidden" name="<%=Attr.teamInsigniaList%>"
			value="${subject.attr.teamInsigniaList}">
		<%-- --%>
		<%@include file="../include/NextButtonFragment.jsp"%>
	</div>
</form>
<%@include file="../include/PrevButtonFragment.jsp"%>
