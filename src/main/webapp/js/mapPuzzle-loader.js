function createMap(){
    var map = new google.maps.Map(document.getElementById('map'), {
        center: {lat: 38.5949, lng: -94.8923},
        zoom: 4
    });
    

    //adds a landmark at Washinton DC
    addLandmark(map, 38.9072, -77.0369, 'Washington DC', 'Fox');

    //adds a landmark at Niagara Falls
    addLandmark(map, 43.0962, -79.0377, 'Niagara Falls','Quick');

    //adds a landmark GrandCanyon
    addLandmark(map, 36.0544, -112.1401, 'Grand Canyon','The');

    //adds a landmark at Yellowstone
    addLandmark(map, 44.4280, -110.5855, 'Yellowstone', 'Brown' );

    //adds a landmark at Statue of Liberty
    addLandmark(map, 40.6892, -74.0445, 'Statue of Liberty', 'Jumps' );

    //adds a landmark at Disney World
    addLandmark(map, 28.3852, -81.5639, 'Disney World', 'Over');

    //adds a landmark at St.Louis Arch
    addLandmark(map, 38.6247, -90.1854, 'The Gateway Arch', 'The');

    //adds a landmark at Mount Rushmore
    addLandmark(map, 43.8791, -103.4591, 'Mount Rushmore', 'Lazy' );

    //adds a landmark at the Alamo
    addLandmark(map, 29.4260, -98.4861, 'The Alamo', 'Dog');

            
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
