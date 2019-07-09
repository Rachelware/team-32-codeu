function createMap(){
    const map = new google.maps.Map(document.getElementById('map'), {

    //Google Plex office
    center: {lat: 37.422, lng: -122.084},
    zoom: 14,

    //Styles map in Night Mode
    styles: [
        {elementType: 'geometry', stylers: [{color: '#242f3e'}]},
        {elementType: 'labels.text.stroke', stylers: [{color: '#242f3e'}]},
        {elementType: 'labels.text.fill', stylers: [{color: '#746855'}]},
    {
        featureType: 'administrative.locality',
        elementType: 'labels.text.fill',
        stylers: [{color: '#d59563'}]
    },
    {
        featureType: 'poi',
        elementType: 'labels.text.fill',
        stylers: [{color: '#d59563'}]
    },
    {
        featureType: 'poi.park',
        elementType: 'geometry',
        stylers: [{color: '#263c3f'}]
    },
    {
        featureType: 'poi.park',
        elementType: 'labels.text.fill',
        stylers: [{color: '#6b9a76'}]
    },
    {
        featureType: 'road',
        elementType: 'geometry',
        stylers: [{color: '#38414e'}]
    },
    {
        featureType: 'road',
        elementType: 'geometry.stroke',
        stylers: [{color: '#212a37'}]
    },
    {
        featureType: 'road',
        elementType: 'labels.text.fill',
        stylers: [{color: '#9ca5b3'}]
    },
    {
        featureType: 'road.highway',
        elementType: 'geometry',
        stylers: [{color: '#746855'}]
    },
    {
        featureType: 'road.highway',
        elementType: 'geometry.stroke',
        stylers: [{color: '#1f2835'}]
    },
    {
        featureType: 'road.highway',
        elementType: 'labels.text.fill',
        stylers: [{color: '#f3d19c'}]
    },
    {
        featureType: 'transit',
        elementType: 'geometry',
        stylers: [{color: '#2f3948'}]
    },
    {
        featureType: 'transit.station',
        elementType: 'labels.text.fill',
        stylers: [{color: '#d59563'}]
    },
    {
        featureType: 'water',
        elementType: 'geometry',
        stylers: [{color: '#17263c'}]
    },
    {
        featureType: 'water',
        elementType: 'labels.text.fill',
        stylers: [{color: '#515c6d'}]
    },
    {
        featureType: 'water',
        elementType: 'labels.text.stroke',
        stylers: [{color: '#17263c'}]
    }]
});

//adds a landmark at Google West Campus
addLandmark(map, 37.423829, -122.092154, 'Google West Campus',
    'Google West Campus is home to YouTube and Maps.')

//adds a landmark at Stan the T-Rex 
addLandmark(map, 37.421903, -122.084674, 'Stan the T-Rex',
    'This is Stan, the T-Rex statue.')

//adds a landmark Permanente Creek Trail
addLandmark(map, 37.420919, -122.086619, 'Permanente Creek Trail',
    'Permanente Creek Trail connects Google to a system of bike trails.');

//event listener listens for click then places marker
map.addListener('click', function(e) {
    placeMarkerAndPanTo(e.latLng, map);
    });
}

/** Adds a marker that shows an info window when clicked. */
function addLandmark(map, lat, lng, title, description){
    const marker = new google.maps.Marker({
        position: {lat: lat, lng: lng},
        map: map,
        title: title
    });
    const infoWindow = new google.maps.InfoWindow({
        content: description
    });
    marker.addListener('click', function() {
        infoWindow.open(map, marker);
    });
}

/*Places a marker at the location*/
function placeMarkerAndPanTo(latLng, map) {
    var marker = new google.maps.Marker({
        position: latLng,
        map: map
    });
    map.panTo(latLng);
}
    