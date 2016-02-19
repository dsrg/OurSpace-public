// main.js

var page = 0;
var num_pages = 12;
var SEC_PER_MILLI = 1000;
var timerId = -1; // set below.
var pageInfo = {
	"Start" : {
		page : 1,
		durationInSec : 0
	},
	"Task Description" : {
		page : 2,
		durationInSec : 25
	},
	"Basic Information" : {
		page : 3,
		durationInSec : 20
	},
	"Avatar" : {
		page : 4,
		durationInSec : 20
	},
	"Mutual Introductions" : {
		page : 5,
		durationInSec : 60
	},
	"Mutual Support" : {
		page : 6,
		durationInSec : 60
	},
	"Team Insignia" : {
		page : 7,
		durationInSec : 45
	},
	"Team Name" : {
		page : 8,
		durationInSec : 45
	},
	"Teamwork" : {
		page : 9,
		durationInSec : 90
	},
	"Team Norm" : {
		page : 10,
		durationInSec : 45
	},
	"Individual Positions" : {
		page : 11,
		durationInSec : 20
	},
	"End" : {
		page : 12,
		durationInSec : 0
	},
};

function checkToEnableNext(event) {
	if (event.currentTarget.checked) {
		enableNext();
	} else {
		disableNext();
	}
}

/* Default share action. */

function shareButtonHandler(event) {
	enableNext();
	preventDefault(event);
}

function disableNext() {
	$("input[name='Next']").prop('disabled', true);
}
function enableNext() {
	$("input[name='Next']").prop('disabled', false);
}
function preventDefault(event) {
	event.preventDefault();
}

function setPageInfo() {
	if ($("#page-info").length == 0) {
		// $("#header").append("<span id='page-info'></span>");
		return;
	}
	$("#page-info").text("Page " + page + " / " + num_pages);
}

function tickTock(targetTimeInSec) {
	return function() {
		// console.log("TIMER TRIGGERED");

		var nowInSec = new Date().getTime() / SEC_PER_MILLI;
		var secLeft = (targetTimeInSec - nowInSec).toFixed(0);
		var s = -59 <= secLeft ? secLeft : "--";
		$("#timer").text("Time left: " + s + " sec");
		if (s == '--')
			stopTimer();
	};
}

function startTimer(durationInSec) {
	if ($("#timer").length == 0) {
		$("#header").append("<span id='timer'></span>");
	}
	var nowInSec = new Date().getTime() / SEC_PER_MILLI + 1;
	var targetTimeInSec = nowInSec + durationInSec;
	var handler = tickTock(targetTimeInSec);
	timerId = setInterval(handler, SEC_PER_MILLI);
	// console.log("TIMER START: " + timerId);
}

function stopTimer() {
	// console.log("TIMER STOP: " + timerId);
	clearTimeout(timerId);
}

function initPage() {
	// $("#header p").html(""); // Erase header for now.
	refreshInit();
	if (typeof pageSpecificInit == "function") {
		pageSpecificInit();
	}
	var title = $(this).attr('title');
	var info = pageInfo[title];
	if (page <= 0 && info) {
		page = info.page;
	}
	if (page > 0) {
		setPageInfo();
	}
	if (info && info.durationInSec > 0) {
		startTimer(info.durationInSec);
	}
	if (typeof initExperimenterPage == "function") {
		initExperimenterPage();
	}
	if (typeof pageInitForChat == "function") {
		pageInitForChat();
	}
}

function setMessage(text) {
	var msgArea = $("#message-area");
	msgArea.empty();
	msgArea.append($("<p></p>").text(text));
}

function findAndReplate(data, selector) {
	var newElt = $(data).find(selector);
	if ($(newElt).length > 0) {
		$(selector).replaceWith(newElt);
	}
}

function findAndUpdateNextButtonAttr(data, selector) {
	var button = $(data).find(selector);
	if ($(button).length > 0) {
		var value = button.val();
		var disabled = button.prop('disabled');
		$(selector).val(value);
		$(selector).prop('disabled', disabled);
	}
}

function handleDialogs() {
	return;
	// disable for now
	if ($("[id*=dialog]").length == 0)
		return;
	$("[id*=dialog]").dialog({
		modal : true,
		center : true,
		resizable : false,
		buttons : {
			Ok : function() {
				$(this).dialog("close");
			}
		},
	});
}

function selectOptionMatchingInitValue() {
	var select = this;
	var value = $(select).data('init-value');
	if (value == undefined)
		alert('Internal Error: select is missing data-init-value attribute.');
	var option = $(select).find('option[value="' + value + '"]');
	$(select).find('option').prop('selected', false);
	if ($(option).length > 0) {
		$(option).prop('selected', true);
	} else {
		// Set select elt to "unselected".
		$(select).prop('selectedIndex', -1);
	}
	handleDialogs();
}

function initCheckbox() {
	alert('INTERNAL ERROR: "initCheckbox" function should no longer be used');
	var checkbox = this;
	var value = $(checkbox).data('init-value');
	if (value == undefined) {
		alert('Internal Error: checkbox is missing data-init-value attribute.');
		return;
	}
	// value is treated as a boolean
	$(checkbox).prop('checked', value);
}

function handleTextInputOrCheckboxUpdate(event) {
	var attrName = $(this).attr('name');
	var value = $(this).val();
	if ($(this).prop('type') == 'checkbox') {
		value = $(this).prop('checked') ? "true" : "";
	}
	if ($(this).prop('type') == 'number') {
		console.log('Numeric value: ' + $(this).attr('value'));
		if(!this.checkValidity()) {
			console.log('Invalid numeric value: skipping it.');
			return;
		}
	}
	postSubjectAttr(attrName, value);
}

function postSubjectAttr(attrName, value) {
	console.log('postSubjectAttr: ' + attrName + ", " + value);
	var param = {};
	param[attrName] = value;
	postSubjectAttrs(param);
}

function postSubjectAttrsErrorHandler(jqXHR, textStatus, errorThrown) {
	console.log('postSubjectAttrsErrorHandler: status=' + textStatus
			+ ", errorThrown=" + errorThrown);
	this.myTryCount++;
	if(this.myTryCount <= this.myRetryLimit) {
		$.ajax(this);
	}
}

function postSubjectAttrs(param) {
	param['cmd'] = 'Page';
	$.ajax({
		type : 'POST',
		// url: url, // default: current page
		data : param,
		// success : ..., ..., complete : ...,
		myTryCount : 0,
		myRetryLimit : 5,
		error : postSubjectAttrsErrorHandler,
	// dataType: 'xml' // default is reasonable guess
	});
}

function initSubjectForm() {
	var form = $(this);

	var selectElt = $(form).find('select');
	$(selectElt).change(handleTextInputOrCheckboxUpdate);
	$(selectElt).each(selectOptionMatchingInitValue);

	var checkboxElt = $(form).find('input[type="checkbox"]');
	$(checkboxElt).change(handleTextInputOrCheckboxUpdate);
	// $(checkboxElt).each(initCheckbox);

	// var elt = $(form).find('input[type="number"]');
	// $(elt).change(handleTextInputOrCheckboxUpdate);

	var textInput = 'textarea, input[type="text"], input[type="number"]';
	$(form).find(textInput).on("input", null, null,
			handleTextInputOrCheckboxUpdate);
}

$(document).ready(initPage);
