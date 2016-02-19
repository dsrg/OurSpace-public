<%@ page contentType="text/html; charset=UTF-8" language="java"%>
<%@ page import="org.dsrg.ourSpace.wea.domLogic.*"%>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>
<script>
	$(document).ready(function() {
		if ('${role}' != 'EXPERIMENTER') {
			// Game.start();
		}
	});
</script>
<h2>Expected Team Effort Level</h2>
<p>Average of individual expectations:</p>
<div id="teamNorm">
	<p>${experiment.teamNorm}</p>
</div>
