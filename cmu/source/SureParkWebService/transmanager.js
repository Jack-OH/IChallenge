var net = require('net');

module.exports = function TransManager() {
	var port = 3333; //The same port that the server is listening on
	var host = '127.0.0.1';

	this.saveDatabase = function(data, callback) {
		
		var socket = new net.Socket(); 		

		console.log(JSON.stringify(data));

		socket.connect(port, host, function() {
			console.log('Connected');
			socket.write(JSON.stringify(data)+'\n');
		});

		socket.on('data', function(data) {
			console.log('Received: ' + data);
			socket.destroy(); // kill client after server's response
		});

		socket.on('close', function() {
			console.log('Connection closed');
		});
	};
};