var net = require('net'),
    JsonSocket = require('json-socket');


module.exports = function TransManager() {
	this.setGarage = function(data, callback) {
		var port = 3333; //The same port that the server is listening on
		var host = '127.0.0.1';
		var socket = new JsonSocket(new net.Socket()); //Decorate a standard net.Socket with JsonSocket
		

		console.log("sending data:" + data);
 
		JsonSocket.sendSingleMessageAndReceive(port, host, data, function(err, message) {
		    if (err) {
		        //Something went wrong 
		        //throw err;
		        return callback(err, null);
		    }
		    console.log('Server said: '+ message.type); //E.g. pong 
		    callback(err, message);
		});
/*

		socket.connect(port, host);
		
		socket.on('connect', function() { //Don't send until we're connected
	    	socket.sendMessage(data);
	       	
	       	socket.on('message', function(message) {
	   			console.log('The result is: ' + message.result);
    		});
		});
*/		
	};

	this.newReservation = function(data, callback) {
		var port = 3333; //The same port that the server is listening on
		var host = '127.0.0.1';
		var socket = new JsonSocket(new net.Socket()); //Decorate a standard net.Socket with JsonSocket
		

		console.log("sending data:" + data);

		JsonSocket.sendSingleMessageAndReceive(port, host, data, function(err, message) {
		    if (err) {
		        //Something went wrong 
		        //throw err;
		        return callback(err, null);
		    }
		    console.log('Server said: '+ message.type); //E.g. pong 
		    callback(err, message);
		});

/*
		socket.connect(port, host);
		
		socket.on('connect', function() { //Don't send until we're connected
	    	socket.sendMessage(data);
	       	
	       	socket.on('message', function(message) {
	   			console.log('The result is: ' + message.result);
    		});
		});
*/	
	};

	this.parkingCar = function(data, callback) {
		var port = 3333; //The same port that the server is listening on
		var host = '127.0.0.1';
		var socket = new JsonSocket(new net.Socket()); //Decorate a standard net.Socket with JsonSocket
		

		console.log("sending data:" + data);

		JsonSocket.sendSingleMessageAndReceive(port, host, data, function(err, message) {
		    if (err) {
		        //Something went wrong 
		        //throw err;
		        return callback(err, null);
		    }
			
			console.log('Server said: '+ message.type); //E.g. pong 
		    callback(err, message);
		    
		});

/*
		socket.connect(port, host);
		
		socket.on('connect', function() { //Don't send until we're connected
	    	socket.sendMessage(data);
	       	
	       	socket.on('message', function(message) {
	   			console.log('The result is: ' + message.result);
    		});
		});
*/	
	};

};
