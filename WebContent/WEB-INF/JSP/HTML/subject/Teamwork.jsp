<%@ page contentType="text/html; charset=UTF-8" language="java"%>
<%@ page import="org.dsrg.ourSpace.wea.domLogic.*"%>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>
<jsp:include page="../include/InitScriptAndAvatarHeader.jsp" />
<form id="subjectForm${subject.id}" method="post"
	data-disabled="${disabled}">
	<fieldset>
		<legend>Teamwork</legend>
		<p>
			On this page, you will help an animated character <strong>visit
				all the houses in a village,</strong> <em>collecting funds</em> so that your
			team can participate in the <em>village Olympics</em>!
		</p>
		<p>
			Use the <strong>arrow keys</strong> to move the character. You will
			need to <strong>cooperate</strong> on this task since:
		</p>
		<ul style="margin: 5mm; padding: 0;">
			<li><strong>one partner will control the left/right
					movement</strong></li>
			<li><strong>the other partner will control the up/down
					movement.</strong></li>
		</ul>
	</fieldset>
	<br>
	<div class="clearBoth">
		<%@include file="../include/NextButtonFragment.jsp"%>
	</div>
</form>
<%@include file="../include/PrevButtonFragment.jsp"%>
