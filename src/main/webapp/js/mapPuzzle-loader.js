function createMap(){
    var map = new google.maps.Map(document.getElementById('map'), {
        center: {lat: 38.5949, lng: -94.8923},
        zoom: 4
    });
    

    //adds a landmark at Washinton DC
    addLandmark(map, 38.9072, -77.0369, 'Washington DC', "this is my puzzle");

    //adds a landmark at Googleplex
    addLandmark(map, 37.4220, -122.0841, 'GooglePlex',
        'google');

    //adds a landmark GrandCanyon
    addLandmark(map, 36.0544, -112.1401, 'Grand Canyon',
        'AZ');

            
 }

 /** Adds a marker that shows an info window when clicked. */
function addLandmark(map, lat, lng, title, description){
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
