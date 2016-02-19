/* File: experimenter.js */

function refreshHandler(data, status) {
	findAndReplate(data, "#main .left-subject-area");
	findAndReplate(data, "#chat .left-subject-area .chatHistory");

	findAndReplate(data, "#main .common-subject-area");

	findAndReplate(data, "#main .right-subject-area");
	findAndReplate(data, "#chat .right-subject-area .chatHistory");

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

function setPlankDurationStatus(role, outcome) {
	return function() {
		alert("Plank duration average for " + role + " " + outcome);
	};
}

// function setPlankDuration(index) {
// var elt = this;
// if (!elt.checkValidity()) {
// // This should not have occurred.
// alert('input ' + elt.name + 'checkValidity is false!');
// return;
// }
// var role = $(this).attr('name').match(/1$/) ? 'SUBJECT1' : 'SUBJECT2';
// var inputs = {
// 'cmd' : 'SetSubjectAttr',
// 'role' : role,
// 'plankDurationAverage' : $(this).val()
// };
// $
// .ajax({
// type : 'POST',
// // url: url, // default: current page
// data : inputs,
// success : setPlankDurationStatus(role, "saved successfully"),
// error : setPlankDurationStatus(role,
// "failed to save, please try again"),
// dataType : 'html' // default is reasonable guess
// });
// }
//
// function plankDurationAveragesSubmit(event) {
// $('#plankDurationAverage1, #plankDurationAverage2').each(setPlankDuration);
// event.preventDefault();
// }

function initExperimenterPage() {
	// $("#plankDurationAverageForm").submit(plankDurationAveragesSubmit);
	turnOnRefresh();
}
