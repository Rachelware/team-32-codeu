var map;
var markers = [];
var locations = [];
var clicks = 0;

function initMap() {
    var haightAshbury = {lat: 37.769, lng: -122.446};
    clicks = 0;
    markers = [];
    locations = [];
    map = new google.maps.Map(document.getElementById('map'), {
        zoom: 2,
        center: haightAshbury,
        mapTypeId: 'terrain'
    });

    // This event listener will call addMarker() when the map is clicked.
    map.addListener('click', function(event) {
        addMarker(event.latLng);
    });

}

// Adds a marker to the map and push to the array.
function addMarker(location) {
    clicks++;
    if (clicks <= 3) {
        var marker = new google.maps.Marker({
            position: location,
            map: map
        });
        markers.push(marker);
        locations.push(location);
        var infoWindow = new google.maps.InfoWindow({
            content: 'Answer to Clue #' + clicks
        });
        marker.addListener('mouseover', function() { //changed name from addEventListener to show description
            infoWindow.open(marker.get('map'), marker);
        });
    } else {
        markers = [];
        locations = [];
        window.alert("You have already created 3 markers. Please delete your existing markers to change your answers!");
    }
}

// Sets the map on all markers in the array.
function setMapOnAll(map) {
    for (var i = 0; i < 3; i++) {
        markers[i].setMap(map);
    }
}

// Removes the markers from the map, but keeps them in the array.
function clearMarkers() {
    clicks = 0;
    setMapOnAll(null);
    markers = [];
    locations = [];
}

// Shows any markers currently in the array.
function showMarkers() {
    setMapOnAll(map);
}

// Deletes all markers in the array by removing references to them.
function deleteMarkers() {
    clearMarkers();
    markers = [];
    locations = [];
}

function saveMarkers() {
    const urlParams = new URLSearchParams(window.location.search);
    const parameterUsername = urlParams.get('user');
    if (clicks < 3) {
        window.alert("You must submit 3 answers! You only submitted " + clicks + " answers!");
        return false;
    } else {
        if (locations[0] == null || locations[1] == null || locations[2] == null) {
            window.alert("Something has gone wrong! Please submit again!");
            return false;
        } else {
            pos = locations[0] + "%" + locations[1] + "%" + locations[2];
            document.getElementById('answer-input').innerHTML = pos;
            return true;
        }
    }
}