var map;
var markers = [];
var clicks = 0;

function createMap(){
    const map = new google.maps.Map(document.getElementById('map'), {
        center: {lat: 37.422, lng: -122.084},
        zoom: 2,
    });

    map.addListener('click', function(event) {
        clicks++;
        if (clicks <= 3) {
            addMarker(event.latLng);
        } else {
            //TODO: Add popup
        }
    });
}

// Adds a marker to the map and push to the array.
function addMarker(location) {
    //creates marker
    var marker = new google.maps.Marker({
        position: location,
        map: map
    });
    //creates info window
    var infoWindow = new google.maps.InfoWindow({
        content: 'Answer #' + clicks
    });
    //defines when to show info window
    marker.addListener('mouseover', function() {
        infoWindow.open(marker.get('map'), marker);
    });
    markers.push(marker);
    setMapOnAll(map);
}

// Sets the map on all markers in the array.
function setMapOnAll(map) {
    for (var i = 0; i < markers.length; i++) {
        markers[i].setMap(map);
    }
}

// Deletes all markers in the array by removing references to them.
function deleteMarkers() {
    setMapOnAll(null);
    markers = [];
}