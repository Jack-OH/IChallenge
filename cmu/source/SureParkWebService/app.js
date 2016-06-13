var express = require('express')
  , app = express() // Web framework to handle routing requests
  , bodyParser = require('body-parser')
  , cookieParser = require('cookie-parser')
  , cons = require('ejs') // Templating library adapter for Express
  , MongoClient = require('mongodb').MongoClient
  , routes = require('./routes'); // Routes for our application

//var favicon = require('serve-favicon');
var http = require('http').Server(app);

var port=3000;

for(var i in process.argv)
{
   if (process.argv[i] == '--release')
   {
       port=9900;
   }
}

MongoClient.connect("mongodb://127.0.0.1:27017/SurePark", function(err, db) {
    if(err){
        console.log(err);
    }

    app.set('view engine','ejs');
    app.use(bodyParser.urlencoded({ limit: '5mb',extended: true}));
    app.use(bodyParser.json({ limit: '5mb' }));
    app.use(cookieParser());
    //app.use(favicon(__dirname + '/public/favicon.ico'));
    app.use(express.static(__dirname + '/public'));

    routes(app, db);

    http.listen(port,function(){
      console.log('listening on port :' + port);
    });

});
