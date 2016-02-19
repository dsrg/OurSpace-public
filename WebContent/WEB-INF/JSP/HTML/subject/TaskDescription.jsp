<%@ page contentType="text/html; charset=UTF-8" language="java"%>
<%@ page import="org.dsrg.ourSpace.wea.domLogic.*"%>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>
<p>On the next series of exercises, you will perform the same series
	of exercises again, except now you will perform along with another
	person. The other person is in another lab and has just completed the
	same series of exercises as you. The two of you will be performing
	together as a team. Your team’s task is to hold the exercise for as
	long as possible. Your team’s time will be the total number of seconds
	that your team holds the exercises:</p>
<p>
	<b>your team's time = your time + your partners time</b>
</p>
<p>On each page, including this one, there will be a timer counting
	down. This timer is only meant to be indicative of the amount of time
	you are expected to spend on a page. If you spend more time, nothing
	bad will happen.</p>
<p>Before you begin, there are a few brief activities you need to
	complete.</p>
<form method="post" action="">
	<input type="submit" name="Next" value="Next" ${disabled}> <input
		type="hidden" name="<%=Attr.cmd%>" value="<%=Page.NextPage%>">
</form>
<%@include file="../include/PrevButtonFragment.jsp"%>
