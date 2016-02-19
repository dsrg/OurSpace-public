/* File: refresh.js 
 * 
 * Note: refreshPage() must be defined by the context (it is not defined in this file).
 * 
 */

var pageRefreshTimer;
var longPollingEnabled = false;

function refreshButtonHandler(event) {
	// var form = $(this).closest("form");
	// var inputs = $(":input", form);
	if (typeof refreshPage == "function") {
		refreshPage();
	} else {
		console.log("Error: refreshPage() is not defined.");
		longPollingEnabled = false;
	}
	if (longPollingEnabled) {
		turnOffRefresh();
	} else {
		turnOnRefresh();
	}
	event.preventDefault();
}

function turnOffRefresh() {
	clearTimeout(pageRefreshTimer);
	longPollingEnabled = false;
}

function turnOnRefresh() {
	longPollingEnabled = true;
	scheduleRefresh();
}

function scheduleRefresh() {
	if (longPollingEnabled) {
		clearTimeout(pageRefreshTimer);
		pageRefreshTimer = setTimeout(refreshPage, 1000);
	}
}

function refreshErrorHandler() {
	alert("Page refresh failed.");
}

function refreshInit() {
	$("#refreshButton").off();
	$("#refreshButton").click(refreshButtonHandler);
	clearTimeout(pageRefreshTimer);
	scheduleRefresh();
}