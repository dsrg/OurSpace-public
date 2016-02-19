<%@ page contentType="text/html; charset=UTF-8" language="java"%>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>
<!DOCTYPE html>
<html>
<head>
<meta charset="UTF-8">
<title>Login</title>
<link rel="stylesheet" href="style/main.css">
</head>
<body>
	<h1>Welcome to the Irwin-Chalin Experiment Portal</h1>
	<form name="LoginForm" method="post" action="">
		<fieldset>
			<legend>Login</legend>
			<label for="userId">User ID</label>
			<%-- --%>
			<input type="text" name="userId" id="userId" autofocus> <br>
			<%-- --%>
			<label for="password">Password</label>
			<%-- --%>
			<input type="password" name="password" id="password"> <br>
			<%-- --%>
			<label for="role">Role</label>
			<%-- --%>
			<select name="role" id="role">
				<%@include file="include/ChooseOption.jsp"%>
				<option value="Experimenter">Experimenter</option>
				<option value="Subject1">Subject 1</option>
				<option value="Subject2">Subject 2</option>
			</select> <br>
			<div>
				<p>
					<span class="loginLabelAligned">Experiment</span>&nbsp;
          <c:if test="${experiment == null}">New experiment will be created on login; <strong>see note below</strong>.</c:if>
          <c:if test="${experiment != null}">Active experiment in progress.</c:if>
				</p>
			</div>
			<%-- --%>
			<label for="kind">Module</label>
			<%-- --%>
			<select name="kind" id="kind" required ${experiment == null ? '' : 'disabled'}>
				<%@include file="include/ChooseOption.jsp"%>
				<option value="HCM" ${experiment != null && experiment.kind == 'HCM' ? 'selected' : ''}>
				High cohesion condition module</option>
				<option value="LCM" ${experiment != null && experiment.kind == 'LCM' ? 'selected' : ''}>
				Low cohesion condition module</option>
			</select> <br>
			<%-- --%>
			<input name="cmd" type="hidden" value="Authenticate">
			<%-- --%>
			<input class="loginPostLableAligned" type="submit">
			<hr style="margin-top: 15px;" />
			<p>
				<strong>Note</strong>: when creating a new experiment, 
				the <em><strong>first</strong></em> role to login (regardless of the role)
				determines the experiment module kind.
			</p>
		</fieldset>
	</form>
	<c:if test="${!empty errorMessage}">
		<h3>Error: ${errorMessage}</h3>
	</c:if>
	<c:if test="${!empty warningMessage}">
		<h3>Warning: ${warningMessage}</h3>
	</c:if>
	<p class="appVersion">
		v.<%@include file="include/version.txt"%></p>
</body>
</html>