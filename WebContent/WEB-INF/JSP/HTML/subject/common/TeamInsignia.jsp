<%@ page contentType="text/html; charset=UTF-8" language="java"%>
<%@ page import="org.dsrg.ourSpace.wea.domLogic.*"%>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>
<h2>Choosing an insignia (visual symbol) to represent your team.</h2>
<p>The highest ranking image, when taking both partner's choices
	into account, is:</p>
<div id="teamInsignia">
	<img src="image/team/${experiment.teamInsignia}.jpg" alt="${experiment.teamInsignia}">
</div>