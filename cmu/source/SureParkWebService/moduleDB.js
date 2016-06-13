var ObjectID = require('mongodb').ObjectID;

module.exports = function(db) {

    var garages = db.collection("garages");
    var reservations = db.collection("reservations");
    var users = db.collection("users");

    this.getGarages = function(query, callback) {
        garages.find(query).toArray(function(err, result){
            if (err)
                return callback(err, null);

            callback(err, result);
        });
    };

    this.getReservations = function(query, callback) {
        reservations.find(query).sort({'reservationTime':-1}).toArray(function(err, result){
            if (err)
                return callback(err, null);
        
            callback(err, result);

        });
    };

    this.checkReservation = function(query, callback) {
        reservations.find(query).toArray(function(err, result){
            if (err)
                return callback(err, null);

            callback(err, result);
        });
    };

    this.getUsers = function(query, callback) {
        users.find(query).toArray(function(err, result){
            if (err) {
                console.log("cant find user");
                return callback(err, null);
            }

            callback(err, result);
        });
    };

    this.checkLogin = function(query, callback) {
            users.find(query).toArray(function(err, result){
            if (err) return callback(err, null);

             callback(null, result);
        });
    };

};
