<%@ page contentType="text/html; charset=UTF-8" language="java"%>
<%@ page import="org.dsrg.ourSpace.wea.domLogic.*"%>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>
<!DOCTYPE html>
<html>
<head>
<meta charset="utf-8">
<title>Experimenter Console</title>
<link rel="stylesheet" href="style/main.css">
<link rel="stylesheet" href="style/s2.css">
<%-- http://code.jquery.com/ui/1.10.3/themes/smoothness/ --%>
<link rel="stylesheet" href="style/jquery-ui.css">
<script src="js/jquery-2.js"></script>
<script src="js/jquery-ui.js"></script>
<script src="js/main.js"></script>
<script src="js/experimenter.js"></script>
<script src="js/chat.js"></script>
<script src="js/refresh.js"></script>
<%!final String NUM_MSG = "Enter whole or decimal number like 999 or 99.999";%>
</head>
<body>
	<div id="all" data-role="${role}" data-experiment-id="${experiment.id}"
		data-num-col="${subpages.size()}">
		<div id="header">
			<p>
				<strong>${experiment.kind} Experiment</strong> ${experiment.id} started <em>${experiment.start}</em>
			</p>
		</div>
		<div id="chat" class="tableContainerForChat">
			<div class="tableRow">
				<div class="left-subject-area">
					<div class="chat">
						<form autocomplete="off" method="post">
							<fieldset>
								<legend>Subject 1 Chat</legend>
								<div class="chatHistory" id="chatHistory1">
									<c:forEach var="line" items="${subject.chatHistory}">
										<p
											class="${line.role == 'EXPERIMENTER' ? 'fromMe' : 'fromOther'}">
											${line.text}</p>
									</c:forEach>
								</div>
								<input class="fill" name="chatInput1" type="text"
									id="chatInput1" maxlength="256"
									placeholder="type here and press enter to chat">
							</fieldset>
						</form>
					</div>
				</div>
				<div class="common-subject-area">
					<c:if test="${!empty developerMode }">
						<form method="post">
							<input type="hidden" name="<%=Attr.cmd%>" value="<%=Page.Page%>">
							<%-- --%>
							<input type="submit" id="refreshButton" value="Refresh">
						</form>
					</c:if>
					<form method="post">
						<input type="hidden" name="<%=Attr.cmd%>"
							value="<%=Page.CancelOrEndExperiment%>">
						<%-- --%>
						<input type="submit" name="<%=Page.CancelOrEndExperiment%>"
							value="<%=Page.CancelOrEndExperiment.title()%>">
					</form>
					<br />
					<form id="plankDurationAverageForm" autocomplete="off"
						method="post">
						<fieldset>
							<legend>Plank duration averages</legend>
							<label for="<%=Attr.plankDurationAverage1%>">Subject 1
								(id=<span class="id">${subject.id}</span>)
							</label>
							<%-- --%>
							<input class="fill" type="text"
								name="<%=Attr.plankDurationAverage1%>"
								id="<%=Attr.plankDurationAverage1%>" required
								placeholder="<%=NUM_MSG%>" title="<%=NUM_MSG%>"
								value="${subject.attr.plankDurationAverage}"
								pattern="\d+(\.\d+)?"> <br>
							<%-- --%>
							<label for="<%=Attr.plankDurationAverage2%>">Subject 2
								(id=<span class="id">${otherSubject.id}</span>)
							</label>
							<%-- --%>
							<input class="fill" type="text"
								name="<%=Attr.plankDurationAverage2%>"
								id="<%=Attr.plankDurationAverage2%>" required
								placeholder="<%=NUM_MSG%>" title="<%=NUM_MSG%>"
								value="${otherSubject.attr.plankDurationAverage}"
								pattern="\d+(\.\d+)?"> <br>
							<%-- --%>
							<input type="hidden" name="<%=Attr.cmd%>"
								value="<%=Page.SetPlankAverages%>">
							<%-- --%>
							<input type="submit" name="<%=Page.SetPlankAverages%>"
								value="Submit">
						</fieldset>
					</form>
					<div>
						<p id="message-area" style="font-size: x-small;"></p>
					</div>
				</div>
				<div class="right-subject-area">
					<div class="chat">
						<form method="post" autocomplete="off">
							<fieldset>
								<legend>Subject 2 Chat</legend>
								<div class="chatHistory" id="chatHistory2">
									<c:forEach var="line" items="${otherSubject.chatHistory}">
										<p
											class="${line.role == 'EXPERIMENTER' ? 'fromMe' : 'fromOther'}">
											${line.text}</p>
									</c:forEach>
								</div>
								<input class="fill" name="chatInput2" type="text"
									id="chatInput2" maxlength="256"
									placeholder="type here and press enter to chat">
								<%-- <input type="submit"> --%>
							</fieldset>
						</form>
					</div>
				</div>
			</div>
		</div>
		<!-- <div id="team-identity"></div> -->
		<div id="main" class="tableContainer">
			<div class="tableRow">
				<div class="left-subject-area">
					<c:set var="disabled" value="disabled" scope="request" />
					<jsp:include page="/WEB-INF/JSP/HTML/subject/${subpages[0]}.jsp" />
				</div>
				<div class="common-subject-area">
					<c:set var="disabled" value="disabled" scope="request" />
					<jsp:include
						page="/WEB-INF/JSP/HTML/subject/common/${subpages[1]}.jsp" />
				</div>
				<div class="right-subject-area">
					<div id="dialog${otherSubject.id}"></div>
					<c:set var="disabled" value="disabled" scope="request" />
					<c:set var="savedSubject" value="${subject}" scope="request" />
					<c:set var="subject" value="${otherSubject}" scope="request" />
					<c:set var="otherSubject" value="${savedSubject}" scope="request" />
					<jsp:include page="/WEB-INF/JSP/HTML/subject/${subpages[2]}.jsp" />
					<%-- Restoring request attributes: not currently necessary, but
					just in case something gets added to, say, the footer, that requires
					the original subject and otherSubject values. --%>
					<c:set var="otherSubject" value="${subject}" scope="request" />
					<c:set var="subject" value="${savedSubject}" scope="request" />
					<c:set var="savedSubject" value="" scope="request" />
				</div>
			</div>
		</div>
		<div id="footer"></div>
	</div>
</body>
</html>