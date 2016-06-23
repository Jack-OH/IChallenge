var net = require('net');

module.exports = function TransManager() {
	var port = 3333; //The same port that the server is listening on
	var host = '127.0.0.1';

	this.saveDatabase = function(sendData, callback) {
		console.log("TransManager -> saveDatabase" + JSON.stringify(sendData));

		var socket_save = new net.Socket();
		socket_save.connect(port, host, function() {
			console.log('Connected');
			try {
				socket_save.write(JSON.stringify(sendData)+'\n');
			} catch (error) {
				console.log(error.message);
			}
		});

		//console.log(JSON.stringify(sendData));

		socket_save.on('data', function(recvData) {
			console.log('Received!!: ' + recvData);

			try {
				var temp = JSON.parse(recvData);
		        if(temp.setGarage !== undefined  || 
		        	temp.parkingCar !== undefined || 
		        	temp.newReservation !== undefined ||
		        	temp.newGarage != undefined ||
		        	temp.wrongParking != undefined ) {

			        callback(null, JSON.stringify(recvData));
			    	socket_save.destroy();
		    	}
			} catch(error) {
				console.log(error.message);
			}

			
		});

		socket_save.on('close', function() {
			console.log('Connection closed!!');
			try {
				socket_save.destroy(); // kill client after server's response
			} catch (error) {
				console.log(error.message);
			}
		});

		socket_save.on('error', function(err) {
  			console.log("Error!!: " + err);
		});
	};

	this.makeConnection = function(sendData, callback) {
		console.log("TransManager -> makeConnection" + JSON.stringify(sendData));

		var socket = new net.Socket();
		socket.connect(port, host, function() {
			console.log('Connected');

			try {
				socket.write(JSON.stringify(sendData)+'\n');
			} catch (error) {
				console.log(error.message);
			}		
			
		});

		socket.on('data', function(recvData) {
			console.log('Received: ' + recvData);

			try {			
		        var temp = JSON.parse(recvData);
	        
		        if(temp.wrongParking !== undefined  || 
		        	temp.detectFailure !== undefined || 
		        	temp.updateSlotStatus !== undefined) {

		           	callback(null, recvData);
		            //socket.destroy(); // kill client after server's response
		        	
		        }
		    } catch (error) {
		    	console.log("socket.data()" + error.message);
		    }
		});

		socket.on('close', function() {
			console.log('Connection closed');

			try {
				socket.destroy(); // kill client after server's response
			} catch (error) {
				console.log("socket.close()" + error.message);
			}
			
		});

		socket.on('error', function(err) {
  			console.log("socket.error(): " + err);
		});
	};
};
