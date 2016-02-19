<%@ page contentType="text/html; charset=UTF-8" language="java"%>
<%@ page import="org.dsrg.ourSpace.wea.domLogic.*"%>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>
<%-- Init script must be here if we want it to be executed when the HTML fragment
     in this file is inserted into the main page DOM via an AJAX call. --%>
<script>
	function avatarButtonInit(form) {
		$(form).find("button.avatar").click(avatarButtonHighlight);
		selectAvatarButton(form);
	}

	function avatarButtonHighlight(event) {
		var form = $(this).closest('form');
		selectAvatarButton(form, this);
		event.preventDefault();
	}

	function selectAvatarButton(form, button) {
		var formButtons = $(form).find("button.avatar");
		var avatarImageElt = $(form).find("input[name='avatarImage']");

		// Clear currently selected button(s)
		$(formButtons).removeClass("selected");

		if (button == undefined) {
			var buttonName = $(avatarImageElt).val();
			button = $(formButtons).filter("[name='" + buttonName + "']");
			if ($(button).length == 0)
				return;
		}
		$(button).addClass("selected");
		var avatarImage = $(button).attr("name");
		$(avatarImageElt).val(avatarImage);
		postSubjectAttr('avatarImage', avatarImage);
	}

	$(document).ready(function() {
		avatarButtonInit($("#subjectForm${subject.id}"));
	});
</script>
<form id="subjectForm${subject.id}" method="post" action="">
	<fieldset>
		<legend>Select the avatar image that will represent you</legend>
		<p>
			<button class="avatar" name="${subject.attr.gender}1" ${disabled}>
				<img src="image/${subject.attr.gender}1.png" alt="Avatar 1"
					class="avatar">
			</button>
			<button class="avatar" name="${subject.attr.gender}2" ${disabled}>
				<img src="image/${subject.attr.gender}2.png" alt="Avatar 2"
					class="avatar">
			</button>
			<button class="avatar" name="${subject.attr.gender}3" ${disabled}>
				<img src="image/${subject.attr.gender}3.png" alt="Avatar 3"
					class="avatar">
			</button>
			<button class="avatar" name="${subject.attr.gender}4" ${disabled}>
				<img src="image/${subject.attr.gender}4.png" alt="Avatar 4"
					class="avatar">
			</button>
			<button class="avatar" name="${subject.attr.gender}5" ${disabled}>
				<img src="image/${subject.attr.gender}5.png" alt="Avatar 5"
					class="avatar">
			</button>
		</p>
	</fieldset>
	<input type="hidden" name="<%=Attr.avatarImage%>"
		value="${subject.attr.avatarImage}">
	<%-- --%>
	<%@include file="../include/NextButtonFragment.jsp"%>
	<c:if test="${!empty subject.attr.subjectIsReadyAndWaiting }">
		<p class="alert">
			<img alt="[ALERT]" src="image/alert-icon.png"> You will be
			automatically directed to the next page once your partner has caught
			up with you.
		</p>
	</c:if>
</form>
<%@include file="../include/PrevButtonFragment.jsp"%>
