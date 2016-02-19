/**
 * Support for web sockets (ports) for the game.
 */

function gameInit() {
	var sel = "cr-stage";
	var gameMgr = new GameMgr(sel);
	var role = $('#all').data('role') || 'SUBJECT1';
	var experimentId = $('#all').data('experiment-id') || 0;

	// The map values of {@code keys} are not currenlty used.
	var keys = {};
	if (role == 'SUBJECT1') {
		keys = {
			/*LEFT_ARROW*/37 : 180,
			/*RIGHT_ARROW*/39 : 0,
		};
	} else if (role == 'SUBJECT2') {
		keys = {
			/*UP_ARROW*/38 : -90,
			/*DOWN_ARROW*/40 : 90,
		};
	}
	gameMgr.initialize(role, experimentId, keys);
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
		console.error('Error: WebSocket is not supported by this browser.');
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
 * GameMgr
 */

function GameMgr(inputSelector) {
	this.inputSelector = inputSelector;
	this.socket = new MySocket(this);
	this.keys = {};
}

GameMgr.prototype.initialize = function(role, experimentId, keys) {
	this.keys = keys;	
	// E.g., Assume that window.location is:
	// http://example.com:999/OurSpaceRXX/app
	// window.location.pathname is '/OurSpaceRXX/app'.
	// var path will be all of the path except for the last part after the
	// last '/'; i.e., path would be '/OurSpaceRXX/'.
	var path = window.location.pathname.match(/.*\//);
	var urlPart = window.location.host + path + 'ChatServlet'
			+ "?experimentId=" + experimentId + "&role=" + role;
	if (window.location.protocol == 'http:') {
		this.socket.connect('ws://' + urlPart);
	} else {
		this.socket.connect('wss://' + urlPart);
	}
};

GameMgr.prototype.sendMessage = function(event) {
	var message = event.keyCode;
	if (message != '') {
		this.socket.send(message);
		// document.getElementById(this.inputSelector).value = '';
		// this.logger.log(message, true);
		// console.log("sendMessage: sending " + message);
	}
};

GameMgr.prototype.onopen = function() {
	var thisGameMgr = this;
	// this.logger.log('(GameMgr channel ready).', true);
	document.getElementById(this.inputSelector).onkeydown = function(event) {
		if (event.keyCode in thisGameMgr.keys) {
			thisGameMgr.sendMessage(event);
			event.preventDefault();
		} else {
			event.preventDefault();
			event.stopPropagation();
			return false;
		}
	};
};

GameMgr.prototype.onclose = function() {
	document.getElementById(this.inputSelector).onkeydown = null;
	// console.log('onclose.');

};

GameMgr.prototype.onmessage = function(message) {
	// onsole.log('onmessage: received ' + message.data);
	var position = message.data.split(' ');
	if (position.length != 2)
		return;
	var player = Crafty('PlayerCharacter');
	player.at(position[0], position[1]);
	// Crafty.keyboardDispatch(keyEv);
	// keyEv['type'] = "keyup";
	// Crafty.keyboardDispatch(keyEv);
};

GameMgr.prototype.onmessageForKey = function(message) {
	// console.log('received: ' + message.data);
	var key = parseInt(message.data);
	if (key != 74 && key != 75)
		return;
	var cc = key == 74 ? 37 : 39;
	var keyEv = {
		type : 'keydown',
		charCode : cc,
		which : cc,
		key : cc,
	};
	var player = Crafty('PlayerCharacter');
	player._keydown(keyEv);
	// Crafty.keyboardDispatch(keyEv);
	// keyEv['type'] = "keyup";
	// Crafty.keyboardDispatch(keyEv);
};
