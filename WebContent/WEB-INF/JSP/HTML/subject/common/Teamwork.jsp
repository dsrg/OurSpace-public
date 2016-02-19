<%@ page contentType="text/html; charset=UTF-8" language="java"%>
<%@ page import="org.dsrg.ourSpace.wea.domLogic.*"%>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>
<!-- Game specific init -->
<%-- gameMgr init will not be atomic but that should not be a problem,
 since the last value will simply be the one used. --%>
<c:if test="${empty applicationScope.gameMgr}">
	<c:set var="gameMgr" scope="application" value="<%=new GameMgr()%>" />
</c:if>
<script src="js/game/lib/crafty.js"></script>
<script src="js/game/src/game.js"></script>
<script src="js/game/src/components.js"></script>
<script src="js/game/src/scenes.js"></script>
<script src="js/game-port.js"></script>
<style type="text/css" media="screen">
#cr-stage {
	margin: 20px auto 0;
}
</style>
<script>
	function sceneInit() {
		var arr = [ // <c:forEach var="sceneRowAsString" items="${applicationScope.gameMgr.scene}">
		"${sceneRowAsString}", // </c:forEach>
		];
		var i;
		for (i = 0; i < arr.length; i++) {
			arr[i] = arr[i].split('');
		}
		return arr;
	}
	function customVictoryHandler() {
		var form = $("#subjectForm${subject.id}");
		var next = $(form).find("[name='Next']");
		$(next).prop('disabled', false);
		return 'Congratulations, all villages visited!<br>Please proceed to the next page.';
		// Remove following line in production system
		// $("#Next").prop('disabled', false);
	}
	$(document).ready(function() {
		//if ('${role}' != 'EXPERIMENTER') {
		Game.start();
		gameInit();
		// }
	});
</script>
<h2>Visit all houses in the village.</h2>
<p>Click anywhere on the game scene to start, then use the arrow
	keys to move around.</p>
<div id="cr-stage" tabindex="${role == 'EXPERIMENTER' ? '' : '1'}">
</div>