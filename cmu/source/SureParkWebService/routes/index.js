var ErrorHandler = require('./error'),
    SessionHandler = require('./session');

var ModuleDB = require('../moduleDB'), 
    TransManager = require('../transmanager');
var fs = require('fs');

module.exports = function(app, db) {
    var sessionHandler = new SessionHandler(db);
    var contentHandler = new ContentHandler(db);

    app.use(sessionHandler.isLoggedInMiddleware);

    app.get('/', contentHandler.mainPage);
    app.post('/login', sessionHandler.handleLoginRequest);
    app.get('/logout', sessionHandler.handleLogoutRequest);

    app.get('/getGarages', contentHandler.getGarages);
    app.post('/setGarage', contentHandler.setGarage);

    app.get('/getUsers', contentHandler.getUsers);

    app.get('/getReservations', contentHandler.getReservations);
    app.post('/checkReservation', contentHandler.checkReservation);
    app.post('/newReservation', contentHandler.newReservation);

    app.get('/parkingCar', contentHandler.parkingCar);

    app.use(ErrorHandler);
};

function ContentHandler(db) {
    var moduleDB = new ModuleDB(db);
    var transManager = new TransManager();
	
    this.mainPage = function(req, res, next) {
        if(req.user == null || req.user._id == null){
            res.render('index',{userName:"", userType:""});
        }else {
            res.render('index',{userName:req.user.displayName, userType:req.user.userType});
        }
    };

    this.getUsers = function(req, res, next){
        var query = req.query;
        moduleDB.getUsers(query, function(err, result){

        if(err)
            return next(err);
    
        res.writeHead(200, { 'Content-Type': 'application/json'});
        res.end(JSON.stringify(result));
        });
    };

    this.getGarages = function(req, res, next){
        var query = req.query;
        moduleDB.getGarages(query, function(err, result){

        if(err)
            return next(err);
    
        res.writeHead(200, { 'Content-Type': 'application/json'});
        res.end(JSON.stringify(result));
        });
    };

    this.setGarage = function(req, res, next){
        var data = req.query;
        transManager.setGarage(data, function(err, result){

        if(err)
            return next(err);
    
        res.writeHead(200, { 'Content-Type': 'application/json'});
        res.end(JSON.stringify(result));
        });
    };

    this.newReservation = function(req, res, next){
        var data = req.query;
        transManager.newReservation(data, function(err, result){

        if(err)
            return next(err);
    
        res.writeHead(200, { 'Content-Type': 'application/json'});
        res.end(JSON.stringify(result));
        });
    };

    this.getReservations = function(req, res, next){
        var query = req.query;
        console.log(query);

        moduleDB.getReservations(query, function(err, result){

        if(err)
            return next(err);
    
        res.writeHead(200, { 'Content-Type': 'application/json'});
        res.end(JSON.stringify(result));
        });
    };

    this.checkReservation = function(req, res, next){
        var query = req.query;
        moduleDB.checkReservation(query, function(err, result){

        if(err)
            return next(err);
    
        res.writeHead(200, { 'Content-Type': 'application/json'});
        res.end(JSON.stringify(result));
        });
    };

    this.parkingCar = function(req, res, next){
        var data = req.query;
        transManager.parkingCar(data, function(err, result){

        if(err)
            return next(err);
    
        res.writeHead(200, { 'Content-Type': 'application/json'});
        res.end(JSON.stringify(result));
        });
    };

}
