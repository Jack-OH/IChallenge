var net = require('net');

module.exports = function TransManager() {
	var port = 3333; //The same port that the server is listening on
	var host = '127.0.0.1';

	this.saveDatabase = function(sendData, callback) {
		console.log("TransManager -> saveDatabase" + JSON.stringify(sendData));

		var socket = new net.Socket();
		socket.connect(port, host, function() {
			console.log('Connected');
			socket.write(JSON.stringify(sendData)+'\n');
		});

		console.log(JSON.stringify(sendData));

		socket.on('data', function(recvData) {
			console.log('Received!!: ' + recvData);

			var temp = JSON.parse(recvData);
	        if(temp.setGarage !== undefined  || 
	        	temp.parkingCar !== undefined || 
	        	temp.newReservation !== undefined ) {

				socket.destroy();
		        callback(null, JSON.stringify(recvData));
	    	}
		});

		socket.on('close', function() {
			console.log('Connection closed');
		});

		socket.on('error', function(err) {
  			console.log("Error!!: " + err);
  			//callback(null, err);
		});
	};

	this.makeConnection = function(sendData, callback) {
		console.log("TransManager -> makeConnection" + JSON.stringify(sendData));

		var socket = new net.Socket();
		socket.connect(port, host, function() {
			console.log('Connected');
			socket.write(JSON.stringify(sendData)+'\n');
		});

		socket.on('data', function(recvData) {
			console.log('Received: ' + recvData);
			//console.log('Received: ' + err);
			//socket.destroy(); // kill client after server's response

	        //callback(null, JSON.stringify(recvData));
	        var temp = JSON.parse(recvData);
	        if(temp.wrongParking !== undefined  || 
	        	temp.detectFailure !== undefined || 
	        	temp.updateSlotStatus !== undefined ) {

	        	console.log("!!!!!!!!!!!!!!!");
	        	callback(null, recvData);
	        	socket.destroy();
	        }        
		});

		socket.on('close', function() {
			console.log('Connection closed');
			socket.destroy(); // kill client after server's response
		});

		socket.on('error', function(err) {
  			console.log("Error: " + err);
  			//callback(null, err);
		});
	};
};
