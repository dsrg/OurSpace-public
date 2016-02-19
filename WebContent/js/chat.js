/**
 * This file contains the functions used to implement chat. The current
 * implementation uses long polling to handle chat exchange. A previous
 * prototype used sockets and this file still contains that draft socket
 * implementation.
 */

var CHAT_HISTORY_SEL_PREFIX = "#chatHistory";
var CHAT_INPUT_SEL_PREFIX = "#chatInput";

function pageInitForChat() {
	if ($(CHAT_HISTORY_SEL_PREFIX).length > 0) {
		// We are in a subject page.
		$(CHAT_INPUT_SEL_PREFIX).keydown(handleChatInput);
	} else {
		// We are in the experimenter page.
		$(CHAT_INPUT_SEL_PREFIX + "1").keydown(handleChatInput);
		$(CHAT_INPUT_SEL_PREFIX + "2").keydown(handleChatInput);
	}
}

function handleChatInput(event) {
	if (event.keyCode == 13) {
		sendChatLine(this);
		event.preventDefault();
	}
}

function sendChatLine(input) {
	var role = $('#all').data('role');
	if (role == 'EXPERIMENTER') {
		role = $(input).attr('name').match(/1$/) ? 'SUBJECT1' : 'SUBJECT2';
	}
	var inputs = {
		'cmd' : 'SetSubjectAttr',
		'role' : role,
		'chatText' : $(input).val()
	};
	$.ajax({
		type : 'POST',
		// url: url, // default: current page
		data : inputs,
		success : chatLineSentSuccessfully(input),
		error : setChatLineStatus(role, "failed to save, please try again"),
		dataType : 'html' // default is reasonable guess
	});

}

function chatLineSentSuccessfully(input) {
	return function() {
		var out = $(input).closest("form").find(".chatHistory");
		var line = $('<p></p>'); // message line to be added
		line.attr('class', 'fromMe');
		line.text($(input).val());
		$(input).val('');
		out.append(line);
		out.scrollTop(out[0].scrollHeight);
	};
}

function setChatLineStatus(role, outcome) {
	return function() {
		alert("Chat for " + role + " " + outcome);
	};
}

/**
 * Socket-based implementation below here.
 */

function pageSpecificInitUsingWebSockets() {
	if ($(CHAT_HISTORY_SEL_PREFIX).length > 0) {
		// We are in a subject page.
		chatInit("");
	} else {
		// We are in the experimenter page.
		chatInit(1);
		chatInit(2);
	}
}

function chatInit(selectorSuffix) {
	var logger = new MyLogger(CHAT_HISTORY_SEL_PREFIX + selectorSuffix);
	var chat = new Chat("chatInput" + selectorSuffix, logger);
	chat.initialize();
}

/**
 * MySocket
 */

function MySocket(observer) {
	this.socket = null;
	this.observer = observer;
}

MySocket.prototype.connect = function(host) {
	if ('WebSocket' in window) {
		this.socket = new WebSocket(host);
	} else if ('MozWebSocket' in window) {
		this.socket = new MozWebSocket(host);
	} else {
		console.log('Error: WebSocket is not supported by this browser.');
		return;
	}

	var observer = this.observer;

	this.socket.onopen = function() {
		observer.onopen();
	};
	this.socket.onclose = function() {
		observer.onclose();
	};
	this.socket.onmessage = function(message) {
		observer.onmessage(message);
	};

};

MySocket.prototype.send = function(message) {
	this.socket.send(message);
};

/**
 * Chat
 */

function Chat(inputSelector, logger) {
	this.inputSelector = inputSelector;
	this.logger = logger;
	this.socket = new MySocket(this);
}

Chat.prototype.initialize = function() {
	var role = $('#all').data('role');
	var experimentId = $('#all').data('experiment-id');
	var urlPart = window.location.host + '/OurSpaceR01/ChatServlet'
			+ "?experimentId=" + experimentId + "&role=" + role;
	if (window.location.protocol == 'http:') {
		this.socket.connect('ws://' + urlPart);
	} else {
		this.socket.connect('wss://' + urlPart);
	}
};

Chat.prototype.sendMessage = function() {
	var message = document.getElementById(this.inputSelector).value;
	if (message != '') {
		this.socket.send(message);
		document.getElementById(this.inputSelector).value = '';
		this.logger.log(message, true);
	}
};

Chat.prototype.onopen = function() {
	var _this = this;
	this.logger.log('(Chat channel ready).', true);
	document.getElementById(this.inputSelector).onkeydown = function(event) {
		if (event.keyCode == 13) {
			_this.sendMessage();
			event.preventDefault();
		}
	};
};

Chat.prototype.onclose = function() {
	document.getElementById(this.inputSelector).onkeydown = null;
	this.logger.log('Info: WebSocket closed.', true);
};

Chat.prototype.onmessage = function(message) {
	this.logger.log(message.data, false);
};

/**
 * MyLogger
 */

// FIXME: rename this to, e.g., a chat manager since this
// is not a logger.
function MyLogger(loggerSelector) {
	this.loggerSelector = loggerSelector;
}

MyLogger.prototype.MAX_NUM_LINES = 10;
MyLogger.prototype.EOL = "\n";

MyLogger.prototype.log = function(message, fromMe) {
	// fromMe is a boolean used to differentiate messages
	// either "from me" or "from the other" in the chat.

	// var out = document.getElementById(this.loggerSelector);
	// var p = document.createElement('p');
	// p.style.wordWrap = 'break-word';
	// p.innerHTML = message;
	// out.appendChild(p);
	// out.appendChild(message);
	// while (out.childNodes.length > 25) {
	// out.removeChild(out.firstChild);
	// }
	// out.scrollTop = out.scrollHeight;
	var out = $(this.loggerSelector);
	var line = $('<p></p>'); // message line to be added
	line.attr('class', fromMe ? 'fromMe' : 'fromOther');
	line.text(message);
	out.append(line);
	// var lines = $(out, "p");

	// TBC: trim lines
	out.scrollTop(out[0].scrollHeight);
};

MyLogger.prototype.logAsTest = function(message) {
	// var out = document.getElementById(this.loggerSelector);
	// var p = document.createElement('p');
	// p.style.wordWrap = 'break-word';
	// p.innerHTML = message;
	// out.appendChild(p);
	// out.appendChild(message);
	// while (out.childNodes.length > 25) {
	// out.removeChild(out.firstChild);
	// }
	// out.scrollTop = out.scrollHeight;
	var out = $(this.loggerSelector);
	var text = out.text();
	if (text != "")
		text += this.EOL;
	text += message;
	var lines = text.split(this.EOL);
	if (lines.length > this.MAX_NUM_LINES) {
		lines = lines.slice(lines.length - this.MAX_NUM_LINES);
		text = lines.join(this.EOL);
	}
	out.text(text);
	out.scrollTop(out[0].scrollHeight);
};
