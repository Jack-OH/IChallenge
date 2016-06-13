var crypto = require('crypto');

/* The SessionsDAO must be constructed with a connected database object */
module.exports = function SessionsDAO(db) {
    "use strict";

    var sessions = db.collection("sessions");
    //var allowedGroups = db.collection("allowedGroups");
    //var allowedUsers = db.collection("allowedUsers");
    var allowedUsers = db.collection("users");

    this.startSession = function(username, callback) {
        "use strict";

        // Generate session id
        var current_date = (new Date()).valueOf().toString();
        var random = Math.random().toString();
        var session_id = crypto.createHash('sha1').update(current_date + random).digest('hex');

        // Create session document
        var session = {'username': username, '_id': session_id};

        // Insert session document
        sessions.insert(session, function (err, result) {
            "use strict";
            callback(err, session_id);
        });
    };

    this.checkAllowedUser = function(name, callback){
        allowedUsers.findOne({'name' : name }, function(err, result) {
            if (err) return callback(err, null);

             callback(null, result);
        });
    };
/*
    this.checkAllowedGroup = function(name, callback){
        allowedGroups.findOne({'name' : name }, function(err, result) {
            if (err) return callback(err, null);

             callback(null, result);
        });
    };
*/
    this.deleteUser = function(session_id, callback) {
        "use strict";
        // Remove session document
        sessions.remove({ '_id' : session_id }, function (err, numRemoved) {
            "use strict";
            callback(err);
        });
    };

    this.addUser = function(user, callback){
        sessions.save(user,function(err, result){

            if(err){
                return callback(err, null);
            }
            callback(null, user);
        });
    };

    this.getUser = function(session_id, callback) {
        "use strict";

        if (!session_id) {
            callback(Error("Session not set"), null);
            return;
        }

        sessions.findOne({'session_id' : session_id }, function(err, result) {
            "use strict";

            if (err) return callback(err, null);

            if (!result) {
                callback(new Error("User : " + result + " does not exist"), null);
                return;
            }

            callback(null, result);
        });
    };
};
