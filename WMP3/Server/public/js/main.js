var serverResult = "Connecting...";

var phoneXYZ;
var nextZ = 0;

function PingTest() {
    var xmlHttp = new XMLHttpRequest();
    xmlHttp.open( "GET", "ping", true); // false for synchronous request
    xmlHttp.timeout = 500; // time in milliseconds
    xmlHttp.onload = function () {
        serverResult = xmlHttp.responseText;
    };
    xmlHttp.ontimeout = function (e) {
        serverResult = "ERROR";
    };
    xmlHttp.send(null);
}

// Given a floor number, make all floors transparent except current one
function RevealFloor(z) {
    // First, bring target floor to front z-index
    document.getElementById("floor"+z).style.zIndex = nextZ++;
    // Then reveal it
}

function UpdateCoords() {
    var xhttp = new XMLHttpRequest();
    xhttp.onreadystatechange = function() {
        if (this.readyState == 4 && this.status == 200) {
           // Typical action to be performed when the document is ready:
           console.log("Got: " + xhttp.responseText);
           phoneXYZ = JSON.parse(xhttp.responseText)[0];
           document.getElementById("coord").innerHTML = "Coord: x:" + phoneXYZ.x + " y:" + phoneXYZ.y;
           document.getElementById("level").innerHTML = "Floor: " + phoneXYZ.z;

           MovePhoneIcon(phoneXYZ.x, phoneXYZ.y, phoneXYZ.z)
        }
    };
    xhttp.open("GET", "GetCoord?ID=0", true);
    xhttp.send();
}

function MovePhoneIcon(x, y, z) {
    // Need to scale this unit approprietly
    // First find where coord 0 is in px
    var xStart = 110;
    var yStart = 110;
    // Then get the maximum coord value
    var xMax = 25;
    var yMax = 13;
    // Then get the coord for the maximum value
    var xEnd = 1710;
    var yEnd = 960;
    // Then create the scalar
    var xScalar = (xEnd - xStart) / xMax;
    var yScalar = (yEnd - yStart) / yMax;


    // Then create the final page pixel value
    var xFinal = xStart + (xScalar * x);
    var yFinal = yStart + (yScalar * y);

    document.getElementById("ph").style.left = xFinal + 'px';
    document.getElementById("ph").style.top = yFinal + 'px';
    RevealFloor(z);
}

function ping() {
    PingTest();
    if(serverResult == "OK") {
        document.getElementById("ServerStatus").innerHTML = "Status: <span style=\"color:Green;\">OK</span>";
    } else {
        document.getElementById("ServerStatus").innerHTML = "Status: <span style=\"color:Red;\">" + serverResult + "</span>";
    }

    UpdateCoords();
}

document.addEventListener("DOMContentLoaded", function(){
   setInterval(ping, 1000);
});

