'use strict';

const SockJS = require('sockjs-client');
require('stompjs');

function register(registrations) {
  const socket = SockJS('/collectorcoin');
  const stompClient = Stomp.over(socket);

  stompClient.heartbeat.outgoing = 20000;
  stompClient.heartbeat.incoming = 20000;

	stompClient.connect({}, function(frame) {
		registrations.forEach(function (registration) {
			stompClient.subscribe(registration.route, registration.callback);
		});
	});

  socket.onclose = function() {
    console.log('Closing websocket connection...');
    stompClient.disconnect();
  };

  return stompClient;
}

module.exports = {
	register: register,
};

