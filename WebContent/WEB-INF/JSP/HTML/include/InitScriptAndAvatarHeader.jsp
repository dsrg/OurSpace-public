<%@ page contentType="text/html; charset=UTF-8" language="java"%>
<%@ page import="org.dsrg.ourSpace.wea.domLogic.*"%>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>
<%-- Init script must be here if we want it to be executed when the HTML fragment
     in this file is inserted into the main page DOM via an AJAX call. --%>
<%@include file="../include/SubjectFormInit.jsp"%>
<div class="avatarHeader">
  <c:if test="${experiment.kind == 'HCM'}">
	  <img src="image/${subject.attr.avatarImage}.png" alt="Avatar"
	    width="448" height="448" class="avatar">
  </c:if>
	<p>
		<span class="avatarName">${param.showRealName ? subject.attr.firstName : subject.avatarName }</span>
		<c:if test="${role == 'EXPERIMENTER'}">
			(<span class="realName">${subject.attr.firstName}</span>)
		</c:if>
	</p>
</div>
