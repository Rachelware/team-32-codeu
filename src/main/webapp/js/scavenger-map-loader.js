var map;
var markers = [];
var clicks = 0;

function initMap() {
    var haightAshbury = {lat: 37.769, lng: -122.446};

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
        var infoWindow = new google.maps.InfoWindow({
            content: 'Answer to Clue #' + clicks
        });
        marker.addListener('mouseover', function() { //changed name from addEventListener to show description
            infoWindow.open(marker.get('map'), marker);
        });
    } else {
        window.alert("You have already created 3 markers. Please delete your existing markers to change your answers!");
    }
}

// Sets the map on all markers in the array.
function setMapOnAll(map) {
    for (var i = 0; i < markers.length; i++) {
        markers[i].setMap(map);
    }
}

// Removes the markers from the map, but keeps them in the array.
function clearMarkers() {
    clicks = 0;
    setMapOnAll(null);
}

// Shows any markers currently in the array.
function showMarkers() {
    setMapOnAll(map);
}

// Deletes all markers in the array by removing references to them.
function deleteMarkers() {
    clearMarkers();
    markers = [];
}