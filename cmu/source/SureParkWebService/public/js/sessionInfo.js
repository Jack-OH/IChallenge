var access_priority = "not_login";

function getPriority(name, usertype) {
    if (usertype == 'owner' && getCookie('session')){
        document.cookie = "group=owner;";
        document.cookie = "name=" + name+";";
        return "owner";
    }else if (usertype == 'attendant' && getCookie('session')){
        document.cookie = "group=attendant;";
        document.cookie = "name=" + name+";";
        return "attendant";
    }else if (usertype == 'driver' && getCookie('session')){
        document.cookie = "group=driver;";
        document.cookie = "name=" + name+";";
        return "driver";
    }else {
        return "not_login";
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

    if(access_priority == "owner" ) {
        $('.owner').show(0);
    } else if (access_priority == "attendant") {
        $('.attendant').show(0);
    } else if (access_priority == "driver") {
        $('.driver').show(0);
    } else {
        $('not_login').show(0);
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

