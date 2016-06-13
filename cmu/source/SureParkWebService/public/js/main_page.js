
var win;
$('#slideShowStartBtn').click(function(){

    $('#slideShowStartBtn').hide(0);
    $('#slideShowEndBtn').show(0);

    /*

    var query = {};
    query.pl = true;

    $.get('page_list',query ,function(data){
        
        data.forEach(function(item){
            var tmp = new structPage();
            tmp.url=item.url;
            window.open(tmp.url, '');
        });
    });
*/

    var time = 30;

    win = window.open('http://integrate.lge.com:9911/', 'newWindow');

    setTimeout(function(){
        window.open('http://integrate.lge.com:9998/', 'newWindow');
    }, time*1000);
    setTimeout(function(){
        window.open('http://integrate.lge.com:9996/', 'newWindow');
    }, time*2000);
    setTimeout(function(){
        window.open('http://integrate.lge.com:9921/', 'newWindow');
    }, time*3000);
    setTimeout(function(){
        window.open('http://integrate.lge.com:9919/', 'newWindow');
    }, time*4000);

    setInterval(function(){
        setTimeout(function(){
            window.open('http://integrate.lge.com:9911/', 'newWindow');
        }, time*1000);
        setTimeout(function(){
            window.open('http://integrate.lge.com:9998/', 'newWindow');
        }, time*2000);
        setTimeout(function(){
            window.open('http://integrate.lge.com:9996/', 'newWindow');
        }, time*3000);
        setTimeout(function(){
            window.open('http://integrate.lge.com:9921/', 'newWindow');
        }, time*4000);
        setTimeout(function(){
            window.open('http://integrate.lge.com:9919/', 'newWindow');
        }, time*5000);
    }, time*5000);
});

$('#usernameBtn').click(function(){
    var status = $('#usernameBtn').attr('data-status');
    if(status === 'logged-in'){
        $.getJSON("logout", function(data){
            setUserName('', '');
            
            location.reload();
        });   
    }else{
        if($('#loginInput').is(':visible')){
            $('#loginInput').fadeOut(100);
        }else{
            $('#loginInput').fadeIn(100);
            $('#login_id').focus();
        }
    }
});

$('#loginBtn').click(function(){
    var arg = {};
    arg.username = $('#login_id').val();
    arg.password = $('#login_passwd').val();

    $('#login_errorMsg').html("");

    $.post("login", arg, function(data){
        if(data.errorMsg){
            $('#login_errorMsg').html(data.errorMsg);
            $('#login_id').val("");
            $('#login_passwd').val("");
        }
        else {
            /****************************
            $('#loginInput').slideUp(100);
            setUserName(data.description);
            ****************************/
            access_priority = getPriority(data.description);
            
            location.reload();
        }
    });
});

$('#makeReserveDoneBtn').click(function(){
    var date = $('#reserve_date').val();
    var time = $('#reserve_time').val();
    var cardInfo = $('#card_information').val();
    var garageName = $('#reserve_garage').val();
    var gracePeriod;
    var parkingFee;
    var newReservation;
    var confirmInformation = garageName + '-' + Math.floor((Math.random() * 10000) + 1);

    if(userName === "") {
        alert("To make reservation, you need to log-in first.");
        return;
    }

    
    var query = {};

    $.get("getGarages", query, function(data) {
        data.forEach(function(item){
            console.log(item);
            if(item.garageName ==  garageName) {
                gracePeriod = item.gracePeriod;
                parkingFee = item.parkingFee;

                newReservation = 
                    {"newReservation": {"userID":userName, "cardInfo":cardInfo, "confirmInformation":confirmInformation, 
                     "gracePeriod":gracePeriod, "parkingFee":parkingFee, "usingGarage":garageName}}; 

                 $.post("newReservation", newReservation, function(resdata){
                    if(resdata.errorMsg){
                        alert("TCP/IP Error");
                    }
                    else {
                        console.log(resdata);
                        location.reload();
                    }
                });

            }            
        });

        if(newReservation===undefined) {
            alert("Please check your reservation input ...");    
        }
        
    });
});

$('#makeReserveCancelBtn').click(function(){
    if($('#newReservation').is(':visible')){
        $('#newReservation').slideUp('slow');
    }
});

$('#make_reservation').click(function(){
    if($('#newReservation').is(':visible')){
        $('#newReservation').slideUp('slow');
    }
    else{
        $('.inputForm').hide(0);
        $('#newReservation').slideDown('slow');
        $('#newReservation').focus();
    }
});




$('#confirmReserveDoneBtn').click(function(){
    var confirmInfo = $('#confirmReservation_info').val();
    var confirmUserID = $('#confirmReservation_name').val();
    var query = {"confirmInformation":confirmInfo, "userID":confirmUserID};
    var parkingCar;
    var usingGarage;

    $.post("checkReservation", query, function(data) {
        data.forEach(function(item){
            console.log(item);
            if(item.reservationStatus == "waiting") {
                usingGarage = item.usingGarage;
                parkingCar = {"parkingCar": {"userID":confirmUserID, "usingGarage":usingGarage, "confirmInformation":confirmInfo}};

                 $.get("parkingCar", parkingCar, function(resdata){
                    if(resdata.errorMsg){
                        alert("TCP/IP Error");
                    }
                    else {
                        console.log(resdata);
                        location.reload();
                    }
                });

            }            
        });

        if(parkingCar===undefined) {
            alert("Please check your reservation information ...");    
        }
        
    });
});

$('#confirmReserveCancelBtn').click(function(){
    if($('#confirmReservation').is(':visible')){
        $('#confirmReservation').slideUp('slow');
    }
});



$('#confirm_reservation').click(function(){
    if($('#confirmReservation').is(':visible')){
        $('#confirmReservation').slideUp('slow');
        $('.inputForm').hide(0);
    }
    else{
        $('.inputForm').hide(0);
        $('#confirmReservation').slideDown('slow');
        $('#confirmReservation').focus();
    }
});


$('#addGarageDoneBtn').click(function(){
    var garageName = $('#add_garage_name').val();
    var garageID = 1000; 
    var numberOfSlots = parseInt($('#add_number_of_slot').val());
    var gracePeriod = parseInt($('#add_grace_period').val());
    var parkingFee = parseInt($('#add_parking_fee').val());
    var garageIP = $('#add_garage_ip').val();
    var slotStatus = [];
    var i = 0;
    var newGarage;

    for(i=0; i < numberOfSlots; i++) {
        slotStatus[i] = "Open";
    }    

    $.get("getGarages", {}, function(data) {
        if(data.length > 0) {
            data.forEach(function(item){
                console.log(item);
                if(item.garageNumber > garageID) {
                    garageID = item.garageNumber;
                }
            });
            garageID++; 
        }

        newGarage = {"newGarage": {"garageName":garageName, "garageNumber":garageID, "slotNumber":numberOfSlots, "slotStatus":slotStatus, 
                      "gracePeriod":gracePeriod, "parkingFee":parkingFee, "garageIP":garageIP, "isAvailable":true}};

        console.log(newGarage);

        $.post("setGarage", newGarage, function(resdata){
            if(resdata.errorMsg){
                alert("TCP/IP Error");
            }
            else {
                console.log(resdata);
                location.reload();
            }
        });
       
    });     

});

$('#addGarageCancelBtn').click(function(){
    if($('#addNewGarage').is(':visible')){
        $('#addNewGarage').slideUp('slow');
    }
});


$('#add_new_garage').click(function(){
    if($('#addNewGarage').is(':visible')){
        $('#addNewGarage').slideUp('slow');
    }
    else{
        $('.inputForm').hide(0);
        $('#addNewGarage').slideDown('slow');
        $('#addNewGarage').focus();
    }
});

$('#show_statistics').click(function() {
    if($('#showStatistics').is(':visible')){
        $('#showStatistics').slideUp('slow');
    }
    else{
        $('.inputForm').hide(0);
        $('#showStatistics').slideDown('slow');
        $('#showStatistics').focus();
    }
});


$('#show_statistics').click(function(){ 

    var newGarage = {"newGarage": {"garageName":"AAAAA", "garageNumber":1001, "slotNumber":4, "slotStatus":["Open","Open","Open","Open"], 
                      "gracePeriod":90, "parkingFee":30, "garageIP":"127.0.0.1", "isAvailable":true}};

    var newReservation = {"newReservation": {"userID":"jack", "cardInfo":"1111-2222-3333-4444", "confirmInformation":"A1234", 
                    "gracePeriod":90, "parkingFee":30, "usingGarage":"Sure-Park"}};

    var cancelReservation = {"cancelReservation": {"userID":"jack", "usingGarage":"Sure-Park", "confirmInformation":"A1234"}};

    var newUser = {"newUser": {"userID":"jack", "userPassword":"jack", "userType":"attendant", 
                    "userName":"Jack OH", "userEmail":"jungkyun98@gmail.com", "displayName":"JUNGKYUN"}};

    var parkingCar = {"parkingCar": {"userID":"jack", "usingGarage":"Sure-Park", "confirmInformation":"A1234"}};


    

    $.get("setGarage", newGarage, function(data){
        if(data.errorMsg){
            alert("TCP/IP Error");
        }
        else {
            console.log(data);
        }
    });
});


function showUserReservation(reservation_data) {
    $('#showReservation').empty();

    var div = $('<div class="well well-sm">Your reservation history </div>').appendTo('#showReservation');
    var table = $('<table class="table table-bordered" width:100%></table>').appendTo('#showReservation');

    var thead = $('<thead data-header="true"></thead>').appendTo(table);
    var tr = $('<tr></tr>').addClass('menuThOption').appendTo(thead);
    $('<th  data-sort-column="true">' + 'User ID' + '</th>').appendTo(tr);
    $('<th  data-sort-column="true">' + 'Garage Name' + '</th>').appendTo(tr);
    $('<th  data-sort-column="true">' + 'Reservation Time' + '</th>').appendTo(tr);
    $('<th  data-sort-column="true">' + 'Reservation ID' + '</th>').appendTo(tr);
    $('<th  data-sort-column="true">' + 'Card Info' + '</th>').appendTo(tr);
    $('<th  data-sort-column="true">' + 'Reservation Status' + '</th>').appendTo(tr);
    $('<th  data-sort-column="true">' + 'Parking Time' + '</th>').appendTo(tr);
    $('<th  data-sort-column="true">' + 'Leaving Time' + '</th>').appendTo(tr);
    $('<th  data-sort-column="true">' + 'Charge Fee' + '</th>').appendTo(tr);

    var tbody = $('<tbody data-body="true"></tbody>').appendTo(table);
    showReservationTableList(reservation_data, tbody) ;
}

function showReservationTableList(reservation_data, tbody){
    var tr;
    var i = 0;
    if (reservation_data.length === 0){
        tr = $('<tr></tr>').addClass('tableRow').appendTo(tbody);
        $('<td colspan="9"> You don\'t have any reservation info. </td>').appendTo(tr);
    } else {
        i = 0;
        reservation_data.forEach(function(arr){
                console.log(arr);
                tr = $('<tr></tr>').addClass('tableRow').appendTo(tbody);
                $('<td>' + arr.userID + '</td>').appendTo(tr);
                $('<td>' + arr.usingGarage + '</td>').appendTo(tr);
                $('<td>' + arr.reservationTime + '</td>').appendTo(tr);
                $('<td>' + arr.confirmInformation + '</td>').appendTo(tr);
                $('<td>' + arr.cardInfo + '</td>').appendTo(tr);
                $('<td>' + arr.reservationStatus + '</td>').appendTo(tr);
                $('<td>' + arr.parkingTime + '</td>').appendTo(tr);
                $('<td>' + arr.leaveTime + '</td>').appendTo(tr);
                $('<td>' + arr.chargingFee + '</td>').appendTo(tr);

                // Max dislay number of reservation is 3. 
                if(i < 3) i++;
                else return;
        });
    }
    
}


function setUserReservation() {
    var query = {'displayName':userName};
    var reservation_data = {};
    //var query = {};
    var userID;

    console.log(query);

    if(userName === "") {
        console.log("don't need to update reservation info.");
        return;
    }

    $.get('getUsers', query, function(userdata) {
        userdata.forEach(function(useritem){
            if(useritem.displayName == userName) {
                userID = useritem.userID;
            }
        });

        //userID = userdata.userID;
        if(userID !== undefined) {
            $.get('getReservations', {'userID': userID}, function(data){
                showUserReservation(data);
            });
        }

    });

}

function setGarageStatus() {
    var query = {};

    $.get('getGarages', query, function(data){
        makeGarageTable(data);
    });
}


function makeGarageTable(garage_data) {
    $('#showGarageStatus').empty();

    var div = $('<div class="well well-sm"> Current garage status </div>').appendTo('#showGarageStatus');
    var table = $('<table class="table table-bordered" width:100%></table>').appendTo('#showGarageStatus');

    var thead = $('<thead data-header="true"></thead>').appendTo(table);
    var tr = $('<tr></tr>').addClass('menuThOption').appendTo(thead);
    $('<th  data-sort-column="true">' + 'Garage Name' + '</th>').appendTo(tr);
    $('<th  data-sort-column="true">' + 'Slot status' + '</th>').appendTo(tr);


    var tbody = $('<tbody data-body="true"></tbody>').appendTo(table);
    makeGarageTableList(garage_data, tbody) ;
}

function makeGarageTableList(garage_data, tbody){
    var tr;
    var i = 0;
    var slotIndex = 0;
    if (garage_data.length === 0){
        tr = $('<tr></tr>').addClass('tableRow').appendTo(tbody);
        $('<td colspan=8> There is no garage information </td>').appendTo(tr);
    } else {
        i = 0;
        garage_data.forEach(function(arr){
                console.log(arr);
                tr = $('<tr></tr>').addClass('tableRow').appendTo(tbody);
                $('<td>' + arr.garageName + '</td>').appendTo(tr);
                
                slotIndex = 0;
                while(slotIndex < arr.slotNumber) {
                    console.log(arr.slotStatus[slotIndex]);
                    if(arr.slotStatus[slotIndex] == "Open") {
                        $('<td>' + arr.slotStatus[slotIndex] + 
                            '</td>').css("background-color","LimeGreen").css("text-align", "center").css("width", "120px").appendTo(tr);
                    } else if(arr.slotStatus[slotIndex] == "Occupied") {
                        $('<td>' + arr.slotStatus[slotIndex] + 
                            '</td>').css("background-color","OrangeRed").css("text-align", "center").css("width", "120px").appendTo(tr);
                    } else if (arr.slotStatus[slotIndex] == "Reserved") {
                        $('<td>' + arr.slotStatus[slotIndex] + 
                            '</td>').css("background-color","LightYellow").css("text-align", "bkit-center").css("width", "120px").appendTo(tr);
                    } else {
                        $('<td>' + arr.slotStatus[slotIndex] + 
                            '</td>').css("background-color","DarkGray").css("text-align", "-webkit-center").css("width", "120px").appendTo(tr);
                    } 

                    slotIndex++;                   
                }
        });
    }
    
}

function updatePageList(arg, callback){
    setUserName(userName, userType);
    setGarageStatus();
    setUserReservation();
    

    if(typeof callback !== 'undefined')
        callback();
}

updatePageList();
