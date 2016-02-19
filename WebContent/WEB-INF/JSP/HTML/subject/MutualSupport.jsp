<%@ page contentType="text/html; charset=UTF-8" language="java"%>
<%@ page import="org.dsrg.ourSpace.wea.domLogic.*"%>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>
<jsp:include page="../include/InitScriptAndAvatarHeader.jsp" />
<div>
	<form id="subjectForm${subject.id}" method="post">
		<fieldset>
			<label for="iStruggledWith">My biggest challenge was</label>
			<div>
				<textarea name="iStruggledWith" rows="4" class="fill" ${disabled}
					required>${subject.attr.iStruggledWith}</textarea>
			</div>
			<%-- --%>
			<br>
			<%-- --%>
			<label for="howCanPartnerImprove">How might your partner
				overcome this challenge?</label>
			<div>
				<textarea name="howCanPartnerImprove" rows="4" class="fill"
					${disabled} required>${subject.attr.howCanPartnerImprove}</textarea>
			</div>
			<br>
			<%-- --%>
			<label for="<%=Attr.iReadPartnersSuggestion%>">I have read my
				partner's suggestion for how I might improve.</label>
			<%-- --%>
			<input type="checkbox" name="<%=Attr.iReadPartnersSuggestion%>" value="true"
				${subject.attr.iReadPartnersSuggestion ? 'checked' : ''}
				${disabled} required>
		</fieldset>
		<%-- --%>
		<%@include file="../include/NextButtonFragment.jsp"%>
	</form>
</div>
<%@include file="../include/PrevButtonFragment.jsp"%>
