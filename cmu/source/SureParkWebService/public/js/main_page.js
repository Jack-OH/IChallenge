
//google.charts.load('current', {'packages':['bar', 'corechart', 'line', 'table' ]});
google.charts.load('visualization', '1', {'packages':['bar', 'corechart', 'line', 'table']});
//google.charts.setOnLoadCallback(drawChart);

var gGarages = [];

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
    //var date = $('#reserve_date').val();
    var date = $('#reserve_date').combodate('getValue');
    //var time = $('#reserve_time').val();
    var cardInfo = $('#card1').val() + "-" + $('#card2').val() + "-" + $('#card3').val() + "-" + $('#card4').val();
    var garageName = $('#reserve_garage').next().find('.current').html(); 
    var gracePeriod;
    var parkingFee;
    var newReservation;
    var userID;
    var confirmInformation = garageName + '-' + Math.floor((Math.random() * 10000) + 1);

    var query = {};

    if(userName === "" || date===undefined || cardInfo===undefined || garageName===undefined) {
        alert("To make reservation, you need to log-in first.");
        return;
    }

    date = date + ":00.000Z";
    console.log(date);

    $.get('getUsers', {'displayName':userName}, function(userdata) {
        userdata.forEach(function(useritem){
            if(useritem.displayName == userName) {
                userID = useritem.userID;
            }
        });

        //userID = userdata.userID;
        if(userID !== undefined) {
            $.get("getGarages", query, function(data) {
                data.forEach(function(item){
                    console.log(item);
                    if(item.garageName ==  garageName) {
                        gracePeriod = item.gracePeriod;
                        parkingFee = item.parkingFee;

                        newReservation = 
                            {"newReservation": {"userID":userID, "cardInfo":cardInfo, "confirmInformation":confirmInformation, 
                             "gracePeriod":gracePeriod, "parkingFee":parkingFee, "usingGarage":garageName }}; 

                        console.log(newReservation);
                        $.post("newReservation", newReservation, function(resdata){
                        console.log(resdata);                      

                            if(resdata.errorMsg){
                                alert("TCP/IP Error");
                            }
                            else {
                                console.log(resdata);
                                location.reload();
                                showPopup("Reservation Info.", "Your reservation id is " + 
                                                confirmInformation + "Don't forget it!!" );
                                
                            }
                        });
                    }            
                });

                

                if(newReservation===undefined) {
                    alert("Please check your reservation input ...");    
                }
                
            });
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
    var usingGarage = $('#confirmReservation_garage').next().find('.current').html(); // $('#confirmReservation_garage').text();
    var query = {"confirmInformation":confirmInfo, "userID":confirmUserID, "usingGarage": usingGarage };
    var parkingCar;

    //$('#myModal').show(0);
    
    

    $.post("checkReservation", query, function(data) {
        if(data.length===0) {
            alert("Please check your reservation information ...");
        } else {
            data.forEach(function(item){
                console.log(item);
                if(item.reservationStatus == "waiting") {
                    //usingGarage = item.usingGarage;
                    parkingCar = {"parkingCar": 
                        {"userID":confirmUserID, "usingGarage":usingGarage, "confirmInformation":confirmInfo}};

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
    var garageIP = $('#ip1').val() + "." + $('#ip2').val() + "." + $('#ip3').val() + "." + $('#ip4').val();
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
                if(item.garageID > garageID) {
                    garageID = item.garageID;
                }
            });
            garageID++; 
        }

        newGarage = {"newGarage": {"garageName":garageName, "garageID":garageID, "slotNumber":numberOfSlots, "slotStatus":slotStatus, 
                      "gracePeriod":gracePeriod, "parkingFee":parkingFee, "garageIP":garageIP, "isAvailable":true}};

        console.log(newGarage);

        $.post("setGarage", newGarage, function(resdata){
            if(resdata.errorMsg){
                alert("TCP/IP Error");
            }
            else {
                console.log(resdata);
                location.reload();
                showPopup("Adding new garage", "Successfully added your garage." );
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


        drawBarChart(document.getElementById('average_occupancy'));
        drawLineChart(document.getElementById('peak_usage_hours'));
        drawBarChart1(document.getElementById("parking_hours"));
        drawBarChart(document.getElementById("heavy_users"));
    }
});

function drawBarChart(elementID) {
    var data = google.visualization.arrayToDataTable([
            ['Year', 'Sales', 'Expenses', 'Profit'],
            ['2014', 1000, 400, 200],
            ['2015', 1170, 460, 250],
            ['2016', 660, 1120, 300],
            ['2017', 1030, 540, 350]
          ]);

          var options = {
            chart: {
              title: 'Company Performance',
                    subtitle: 'Sales, Expenses, and Profit: 2014-2017',
            }
          };

          var chart = new google.charts.Bar(elementID);
          chart.draw(data, options);
}

function drawLineChart(elementID) {
        var data = google.visualization.arrayToDataTable([
          ['Year', 'Sales', 'Expenses'],
          ['2004',  1000,      400],
          ['2005',  1170,      460],
          ['2006',  660,       1120],
          ['2007',  1030,      540]
        ]);

        var options = {
          title: 'Company Performance',
          curveType: 'function',
          legend: { position: 'bottom' }
        };

        var chart = new google.visualization.LineChart(elementID);

        chart.draw(data, options);
}

function drawBarChart1(elementID) {
      var data = google.visualization.arrayToDataTable([
        ["Element", "Density", { role: "style" } ],
        ["Copper", 8.94, "#b87333"],
        ["Silver", 10.49, "silver"],
        ["Gold", 19.30, "gold"],
        ["Platinum", 21.45, "color: #e5e4e2"]
      ]);

      var view = new google.visualization.DataView(data);
      view.setColumns([0, 1,
                       { calc: "stringify",
                         sourceColumn: 1,
                         type: "string",
                         role: "annotation" },
                       2]);

      var options = {
        title: "Density of Precious Metals, in g/cm^3",
        width: 600,
        height: 400,
        bar: {groupWidth: "95%"},
        legend: { position: "none" },
      };
      var chart = new google.visualization.BarChart(elementID);
      chart.draw(view, options);
}

$('#show_statistics').click(function(){ 

    var newGarage = {"newGarage": {"garageName":"AAAAA", "garageID":1001, "slotNumber":4, "slotStatus":["Open","Open","Open","Open"], 
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
    var tr1, tr2;
    var i = 0;
    var slotIndex = 0;
    if (garage_data.length === 0){
        tr1 = $('<tr></tr>').addClass('tableRow').appendTo(tbody);
        $('<td colspan=8> There is no garage information </td>').appendTo(tr1);
    } else {
        garage_data.forEach(function(arr){
                console.log(arr);

                gGarages.push(arr.garageName);

                //tr1 = $('<tr></tr>').addClass('tableRow').appendTo(tbody);

                if(access_priority=="attendant" || access_priority=="owner") {
                    tr1 = $('<tr></tr>').addClass('tableRow').appendTo(tbody);
                    tr2 = $('<tr></tr>').addClass('tableRow').appendTo(tbody);   
                    $('<td rowspan="2" valign="middle">' + arr.garageName + '</td>').appendTo(tr1); 
                } else {
                    tr1 = $('<tr></tr>').addClass('tableRow').appendTo(tbody);
                    $('<td>' + arr.garageName + '</td>').appendTo(tr1); 
                }
                
                //$('<td rowspan="2">' + arr.garageName + '</td>').appendTo(tr1);

                
                slotIndex = 0;
                while(slotIndex < arr.slotNumber) {
                    console.log(arr.slotStatus[slotIndex]);
                    if(arr.slotStatus[slotIndex] == "Open") {
                        $('<td>' + arr.slotStatus[slotIndex] + 
                            '</td>').css("background-color","LimeGreen").css("text-align", "-webkit-center").css("width", "150px").appendTo(tr1);
                    } else if(arr.slotStatus[slotIndex] == "Occupied") {
                        $('<td>' + arr.slotStatus[slotIndex] + 
                            '</td>').css("background-color","OrangeRed").css("text-align", "-webkit-center").css("width", "150px").appendTo(tr1);
                    } else if (arr.slotStatus[slotIndex] == "Reserved") {
                        $('<td>' + arr.slotStatus[slotIndex] + 
                            '</td>').css("background-color","LightYellow").css("text-align", "-webkit-center").css("width", "150px").appendTo(tr1);
                    } else {
                        $('<td>' + arr.slotStatus[slotIndex] + 
                            '</td>').css("background-color","DarkGray").css("text-align", "-webkit-center").css("width", "150px").appendTo(tr1);
                    } 

                    if(access_priority=="attendant" || access_priority=="owner") {
                        $('<td>' + calUpdateTime(arr.updateTime[slotIndex]) + 
                            '</td>').css("background-color","white").css("text-align", "-webkit-center").css("width", "150px").appendTo(tr2);
                    }

                    slotIndex++;                   
                }
        });
    }

    i = 0;
    gGarages.forEach(function(garage) {
        var option = '<option value=' + i + '>' + garage + '</option>';
        i++;

        $(option).appendTo('#reserve_garage');
        $(option).appendTo('#confirmReservation_garage');
    });

    $('#reserve_garage').niceSelect();    
    $('#confirmReservation_garage').niceSelect();
}

function updatePageList(arg, callback){
    setUserName(userName, userType);
    setGarageStatus();
    setUserReservation();

    if(typeof callback !== 'undefined')
        callback();
}

function calUpdateTime(updateTime) {
    var passedTime="";
    console.log(updateTime);

    var lastDate = new Date(updateTime);
    var currDate = new Date();
    console.log("current:" + currDate.getTime());
    console.log("last:" + lastDate.getTime());
    console.log("current - last = " + (currDate.getTime() - lastDate.getTime()) );

    passedTime = (currDate.getTime() - lastDate.getTime())/(1000*60);
    passedTime = Math.round(passedTime) + " min";

    return passedTime;

}

function showPopup(popupHeader, popupContent) {
    console.log(popupHeader);
    console.log(popupContent);

    $('#modal_title').text(popupHeader);
    $('#modal_content').text(popupContent);

    //('#myModal').find('h1').innerText = popupHeader;
    //('#myModal').find('p').innerText = popupContent;

    $('#myModal').modal('show');    
}



updatePageList();


$(function(){
    $('#reserve_date').combodate();
});