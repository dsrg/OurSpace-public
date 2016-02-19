/* File: subject.js */

function refreshHandler(responseHtml, status) {
	// $(responseBody) is the list of elements in the body, since
	// jQuery strips out the header and the body tag. Hence use filter.
	// var respNumCol = $(responseBody).filter("#all").data('num-col');
	var numCol = $("#all").data('num-col');
	var title = $(document).attr('title');
	var respTitle = $(responseHtml).filter('title').text();
	if (title != respTitle) {
		// Replace entire page with response since the page
		// layout has changed (when numCol != respNumCol).
		stopTimer();
		turnOffRefresh();
		var circumventSeleniumBug = true;
		$(circumventSeleniumBug ? "body" : "html").html(responseHtml);
		if (circumventSeleniumBug) {
			document.title = respTitle;
			// $("body").html(responseHtml);
		} else {
			// Set the entire HTML element since we want to update
			// the title too!
			// $("html").html(responseHtml);
		}
	} else {
		findAndReplate(responseHtml, "#chat .chatHistory");
		if (numCol <= 1) {
			// User may be actively editing in the common area
			// so do not replace it. Only update the Next button.
			findAndUpdateNextButtonAttr(responseHtml,
					"#main .common-subject-area input[name='Next']");
		} else {
			if (title != "Teamwork")
				findAndReplate(responseHtml, "#main .common-subject-area");
			findAndReplate(responseHtml, "#main .right-subject-area");
			// User may be actively editing in the common area
			// so do not replace it. Only update the Next button.
			findAndUpdateNextButtonAttr(responseHtml,
					"#main .left-subject-area input[name='Next']");
		}
	}

	var now = new Date();
	setMessage("Last refreshed " + now);
}

function refreshPage() {
	// var url = window.location.pathname;
	var inputs = {
		'cmd' : 'Page'
	};
	$.ajax({
		type : 'POST',
		// url: url, // default: current page
		data : inputs,
		success : refreshHandler,
		error : refreshErrorHandler,
		complete : scheduleRefresh,
	// dataType: 'xml' // default is reasonable guess
	});
}

function subjectInit() {
	// $("form[id^='subjectForm']").each(initFormIfPresent);
	turnOnRefresh();
}

$(document).ready(subjectInit);
