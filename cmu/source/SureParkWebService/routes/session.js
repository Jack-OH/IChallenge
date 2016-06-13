var SessionDB = require('../sessionDB'),
    ModuleDB = require('../moduleDB'),
    ldapjs = require('ldapjs'),
    crypto = require('crypto');


function sendLoginOk(res,sessionDB,user){

    sessionDB.addUser(user, function(err, user) {

        if(err)
            sendLoginError(res, "db에 sesstion정보 기록중 에러 발생!!");

        res.cookie('session', user.session_id);
        res.writeHead(200, { 'Content-Type': 'application/json'});
        res.end(JSON.stringify({displayName:user.displayName, description:'설명을 넘기던 것'}));
        return;
    });
}

function sendLoginError(res, message){
    res.cookie('session', '');
    res.writeHead(200, { 'Content-Type': 'application/json'});
    res.end(JSON.stringify({errorMsg:message}));
}

function SessionHandler (db) {

    var sessionDB = new SessionDB(db);
    var moduleDB = new ModuleDB(db);

    this.isLoggedInMiddleware = function(req, res, next) {
        var session_id = req.cookies.session;

        sessionDB.getUser(session_id, function(err, user) {

            if (err === null && user !== null){
                req.user = user;
                //console.log(req.user._id);
            }
            return next();
        });
    };

    this.handleLogoutRequest = function(req, res, next){
        var session_id = req.cookies.session;
        //console.log("outout");

        sessionDB.deleteUser(session_id, function (err){
            res.cookie('session', '');
            //console.log("outout2");
            res.writeHead(200, { 'Content-Type': 'application/json'});
            res.end(JSON.stringify({displayName:""}));
            //res.sendStatus(200);
//            return res.redirect('/');
        });
    };

    this.handleLoginRequest = function(req, res, next){


        var username = req.body.username;
        var password = req.body.password;
 //       var searchId = req.body.searchId;

        moduleDB.checkLogin({'userID' : username }, function(err, result){

            var current_date = (new Date()).valueOf().toString();
            var random = Math.random().toString();
            var session_id = crypto.createHash('sha1').update(current_date + random).digest('hex');

            var user = {};
            
            if(err) {
                console.log("Login Error");
                return next(err);                
            }
           
            for (i = 0; i < result.length; i++) { 
                 if(result[i].userID == username && result[i].userPassword == password) {
                    // accessType = result[i].userType;
                    // userName = result[i].userID;
                    
                    user._id = result[i].userID;
                    user.displayName = result[i].displayName;
                    user.email = result[i].userEmail;
                    user.userType = result[i].userType;
                    user.session_id = session_id;
                    user.login_date = new Date();

                    sendLoginOk(res, sessionDB, user);
                    return;
                }            
            }
            
            sendLoginError(res, "Please check your ID and Password.");
        });
    };

}

module.exports = SessionHandler;
