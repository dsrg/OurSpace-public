<%@ page contentType="text/html; charset=UTF-8" language="java"%>
<%@ page import="org.dsrg.ourSpace.wea.domLogic.*"%>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>
<p>
  
	Congratulations<c:if test="${!empty subject.attr.teamName}"> <strong>${subject.attr.teamName}</strong></c:if>! You are
	almost ready for the next series of exercises. At this time, please return to the survey and fill out the second page.</p>
<%@include file="../include/PrevButtonFragment.jsp"%>
