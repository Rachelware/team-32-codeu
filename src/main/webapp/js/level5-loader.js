function createLevel5Map(){
    fetch('/ufo-data').then(function(response) {
        return response.json();
    }).then((ufoSightings) => {
          
    const map = new google.maps.Map(document.getElementById('map'), {
        center: {lat: 39.8283, lng: -98.5795},
        zoom: 3
    });
    console.log("Hello");

    var title = "haystack";
    
    ufoSightings.forEach((ufoSighting) => {
        var description = "(" + ufoSighting.lat.toString() + "," + ufoSighting.lng.toString() + ")";
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

//working on adding a hint button
/*var coll = document.getElementsByClassName("collapsible");
var i;

for (i = 0; i < coll.length; i++) {
  coll[i].addEventListener("click", function() {
    this.classList.toggle("active");
    var content = this.nextElementSibling;
    if (content.style.display === "block") {
      content.style.display = "none";
    } else {
      content.style.display = "block";
    }
  });
}*/