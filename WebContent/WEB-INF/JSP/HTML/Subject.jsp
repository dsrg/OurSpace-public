<%@ page contentType="text/html; charset=UTF-8" language="java"%>
<%@ page import="org.dsrg.ourSpace.wea.domLogic.*"%>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>
<!DOCTYPE html>
<html>
<head>
<meta charset="utf-8">
<title>${pageTitle}</title>
<link rel="stylesheet" href="style/main.css">
<c:if test="${subpages.size() > 1}">
	<link rel="stylesheet" href="style/s2.css">
</c:if>
<%-- http://code.jquery.com/ui/1.10.3/themes/smoothness/ --%>
<link rel="stylesheet" href="style/jquery-ui.css">
<script src="js/jquery-2.js"></script>
<script src="js/jquery-ui.js"></script>
<script src="js/main.js"></script>
<script src="js/chat.js"></script>
<script src="js/refresh.js"></script>
<script src="js/subject.js"></script>
</head>
<body>
	<div id="all" data-role="${role}" data-experiment-id="${experiment.id}" data-num-col="${subpages.size()}">
		<div id="header">
			<div class="teamInsignia">
				<c:if test="${showTeamInsignia}">
					<img src="image/team/${subject.attr.teamInsignia}.jpg"
						class="teamInsignia">
				</c:if>
				<p>
					<c:if test="${showTeamName}">
						<span class="teamName">${subject.attr.teamName}</span>
					</c:if>
				</p>
			</div>
			<p>
				<strong>Subject ${role.subjectIndex() + 1} page for
					experiment </strong>${experiment.id} started <em>${experiment.start}</em>
			</p>
			<p>
			  <c:if test="experiment.kind == 'HCM'">
				  <span id='page-info'>[Page]</span>
			  </c:if>
				<%-- --%>
				<span id='timer'>Time left: -- sec</span>
			</p>
		</div>
		<div id="main" class="tableContainer">
			<div class="tableRow">
				<div class="left-subject-area">
					<c:if test="${subpages.size() > 1}">
						<c:if test="${! empty alertMessage}">
							<div id="dialog${subject.id}" title="Alert">
								<p>${alertMessage}</p>
							</div>
						</c:if>
						<%-- subject.avatarName --%>
						<jsp:include page="/WEB-INF/JSP/HTML/subject/${subpages[0]}.jsp">
							<jsp:param name="showRealName" value="true" />
						</jsp:include>
					</c:if>
				</div>
				<div class="common-subject-area">
					<c:choose>
						<c:when test="${subpages.size() > 1}">
							<jsp:include
								page="/WEB-INF/JSP/HTML/subject/common/${subpages[1]}.jsp" />
						</c:when>
						<c:otherwise>
							<c:if test="${! empty alertMessage}">
								<div id="dialog${subject.id}" title="Alert">
									<p>${alertMessage}</p>
								</div>
							</c:if>
							<jsp:include page="/WEB-INF/JSP/HTML/subject/${subpages[0]}.jsp" />
						</c:otherwise>
					</c:choose>
				</div>
				<div class="right-subject-area">
					<c:if test="${subpages.size() > 1}">
						<c:set var="disabled" value="disabled" scope="request" />
						<c:set var="savedSubject" value="${subject}" scope="request" />
						<c:set var="subject" value="${otherSubject}" scope="request" />
						<c:set var="otherSubject" value="${savedSubject}" scope="request" />
						<jsp:include page="/WEB-INF/JSP/HTML/subject/${subpages[2]}.jsp" />
						<c:set var="otherSubject" value="${subject}" scope="request" />
						<c:set var="subject" value="${savedSubject}" scope="request" />
						<c:set var="savedSubject" value="" scope="request" />
					</c:if>
				</div>
			</div>
		</div>
		<div class="tableContainerForChat">
			<div class="tableRow">
				<div class="left-subject-area">
					<c:if test="${!empty developerMode }">
						<form method="post">
							<input type="hidden" name="<%=Attr.cmd%>" value="<%=Page.Page%>">
							<%-- --%>
							<input type="submit" id="refreshButton" value="Refresh">
						</form>
					</c:if>
					<p id="message-area"></p>
				</div>
				<div class="common-subject-area">
					<div id="chat">
						<form method="post" autocomplete="off">
							<fieldset>
								<legend>Chat</legend>
								<div class="chatHistory" id="chatHistory">
									<c:forEach var="line" items="${subject.chatHistory}">
										<p
											class="${line.role == 'EXPERIMENTER' ? 'fromOther' : 'fromMe'}">
											${line.text}</p>
									</c:forEach>

								</div>
								<input class="fill" name="chatInput" type="text" id="chatInput"
									maxlength="256" placeholder="type here and press enter to chat">
								<%-- <input type="submit"> --%>
							</fieldset>
						</form>
					</div>
				</div>
				<div class="right-subject-area"></div>
			</div>
		</div>
		<div id="footer"></div>
	</div>
</body>
</html>