var access_priority = 0;

function getPriority(name, usertype) {
    if (usertype == 'owner' && getCookie('session')){
        document.cookie = "group=owner;";
        document.cookie = "name=" + name+";";
        return 2;
    }else if (usertype == 'attendant' && getCookie('session')){
        document.cookie = "group=attendant;";
        document.cookie = "name=" + name+";";
        return 1;
    }else {
        return 0;
    }
}

function setUserName(paramName, paramType) {
    $('.inputForm').hide(0);

    var session = getCookie('session');
    if(session && paramName){
        userName = paramName;
        $('#usernameBtn').val(userName + " | Logout").attr('data-status','logged-in');
        $('.integrator').show(0);
        $('#showReservation').show(0);
    }
    else{
        $('#usernameBtn').val("Login").attr('data-status','logged-out');
        $('.integrator').hide(0);
        $('#showReservation').hide(0);
        userName = '';

        (function(){
            today   = new Date();
            today.setDate(today.getDate() - 1);
            document.cookie = "group=;";
            document.cookie = "name=;";
            document.cookie = "path=/; expires=" + today.toGMTString() + ";";
        })('session');
    }
    access_priority = getPriority(paramName, paramType);

    if(access_priority == 2 ) {
        $('#make_reservation').show(0);
        $('#confirm_reservation').show(0);
        $('#show_statistics').show(0);
        $('#add_new_garage').show(0);        
    } else if (access_priority == 1) {
        $('#make_reservation').show(0);
        $('#confirm_reservation').show(0);
    } else {
        $('#make_reservation').show(0);
    }
}

function getCookie(cname) {
    var name = cname + "=";
    var ca = document.cookie.split(';');
    for(var i=0; i<ca.length; i++) {
        var c = ca[i];
        while (c.charAt(0)==' ') c = c.substring(1);
        if (c.indexOf(name) === 0) return c.substring(name.length,c.length);
    }
    return "";
}

