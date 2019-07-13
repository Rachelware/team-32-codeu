function createLevel4Map(){
    fetch('/ufo-data').then(function(response) {
        return response.json();
    }).then((ufoSightings) => {
          
    const map = new google.maps.Map(document.getElementById('map'), {
        center: {lat: 35.78613674, lng: -119.4491591},
        zoom: 7
    });

    var title = "haystack";
    
    ufoSightings.forEach((ufoSighting) => {
        var description = "( " + ufoSighting.lat.toString() + ", " + ufoSighting.lng.toString() + ")";
        console.log(description);
        addLandmark(map, ufoSighting.lat, ufoSighting.lng, title, description); 
        });
    });
}

/** Adds a marker that shows an info window when clicked. */
function addLandmark(map, lat, lng, title, description){

    if(lat == 47.6038321 && lng == -122.3300623){
        title = "needle";
    }
    var marker = new google.maps.Marker({
        position: {lat: lat, lng: lng},
        map: map,
        title: title
    });
    var infoWindow = new google.maps.InfoWindow({
        content: description
    });
    marker.addListener('click', function() { //changed name from addEventListener to show description
        infoWindow.open(marker.get('map'), marker);
    });
}