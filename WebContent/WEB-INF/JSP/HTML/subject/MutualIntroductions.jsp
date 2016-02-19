<%@ page contentType="text/html; charset=UTF-8" language="java"%>
<%@ page import="org.dsrg.ourSpace.wea.domLogic.*"%>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>
<jsp:include page="../include/InitScriptAndAvatarHeader.jsp" />
<div>
	<form id="subjectForm${subject.id}" method="post" action="">
		<fieldset>
			<legend>More about you</legend>
			<p>
				<span class="fill"> <label for="yearInSchool">Year in
						school</label> 
						 <select name="yearInSchool"
					data-init-value="${subject.attr.yearInSchool}" ${disabled}>
						<%@include file="../include/ChooseOption.jsp"%>
						<option value="<%=Attr.Freshman%>">Freshman</option>
						<option value="<%=Attr.Sophomore%>">Sophomore</option>
						<option value="<%=Attr.Junior%>">Junior</option>
						<option value="<%=Attr.Senior%>">Senior</option>
						<option value="<%=Attr.Graduate%>">Graduate</option>
						<option value="<%=Attr.NotInSchool%>">Not in school</option>
				</select>
				</span>
				<%-- --%>
				<br>
				<%-- --%>
				<label for="major">Major</label>
				<%-- --%>
				<span class="fill"> <input name="major" type="text"
					class="fill" ${disabled} required value="${subject.attr.major}">
				</span>
				<%-- --%>
				<br>
				<%-- --%>
				<label for="careerPlans">Career Plans</label> <span class="fill">
					<input name="careerPlans" type="text" class="fill" ${disabled}
					required value="${subject.attr.careerPlans}">
				</span>
				<%-- --%>
				<br>
				<%-- --%>
				<label for="favoriteTvShows">Favorite TV show(s)</label> <span
					class="fill"> <input name="favoriteTvShows" type="text"
					class="fill" ${disabled} required
					value="${subject.attr.favoriteTvShows}">
				</span>
				<%-- --%>
				<br>
				<%-- --%>
				<label for="somethingAboutYou">Something interesting about
					you</label>
			</p>
			<div>
				<textarea class="fill" name="somethingAboutYou" ${disabled} required>${subject.attr.somethingAboutYou}</textarea>
			</div>
			<br>
			<%-- --%>
			<label for="<%=Attr.iReadPartnersInfo%>">I have read my
				partner's introductory information</label>
			<%-- --%>
			<input type="checkbox" name="<%=Attr.iReadPartnersInfo%>" value="true"
				${subject.attr.iReadPartnersInfo ? 'checked' : ''}
				${disabled} required>
		</fieldset>
		<%-- --%>
		<%@include file="../include/NextButtonFragment.jsp"%>
	</form>
</div>
<%@include file="../include/PrevButtonFragment.jsp"%>
