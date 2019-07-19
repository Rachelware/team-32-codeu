
// Get ?user=XYZ parameter value
const urlParams = new URLSearchParams(window.location.search);
const parameterUsername = urlParams.get('user');

// URL must include ?user=XYZ parameter. If not, redirect to homepage.
if (!parameterUsername) {
    window.location.replace('/');
}

//fetch messages and add them to the page.
function fetchMessages(){
    const url = '/feed';
    fetch(url).then((response) => {
        return response.json();
}).then((messages) => {
        const messageContainer = document.getElementById('message-container');
    if(messages.length === 0){
        messageContainer.innerHTML = 'There are no posts yet.';
    }
    else{
        messageContainer.innerHTML = '';
    }
    messages.forEach((message) => {
        const messageDiv = buildMessageDiv(message);
    messageContainer.appendChild(messageDiv);
    messageContainer.scrollIntoView({ behavior: 'smooth', block: 'end' });
    messageContainer.scrollTop = messageContainer.scrollHeight;
});
});
}
/**
 * Creates an li element.
 * @param {Element} childElement
 * @return {Element} li element
 */
function createListItem(childElement) {
    const listItemElement = document.createElement('li');
    listItemElement.appendChild(childElement);
    return listItemElement;
}

/**
 * Creates an anchor element.
 * @param {string} url
 * @param {string} text
 * @return {Element} Anchor element
 */
function createLink(url, text) {
    const linkElement = document.createElement('a');
    linkElement.appendChild(document.createTextNode(text));
    linkElement.href = url;
    return linkElement;
}

/**
 * Shows the message form if the user is logged in and viewing their own page.
 */
function showMessageFormIfViewingSelf() {
    fetch('/login-status')
        .then((response) => {
        return response.json();
})
.then((loginStatus) => {
        if (loginStatus.isLoggedIn &&
            loginStatus.username == parameterUsername) {
        const messageForm = document.getElementById('message-form');
        messageForm.classList.remove('hidden');
        const answerForm = document.getElementById('answer-form');
        answerForm.classList.remove('hidden');
    }
});
}

/**
 * Builds an element that displays the message.
 * @param {Message} message
 * @return {Element}
 */
function buildMessageDiv(message) {
    const headerDiv = document.createElement('div');
    user_name = message.user;
    user_name = user_name.split("@");
    var currentDate = new Date(message.timestamp);
    var stringVersion  = currentDate.toLocaleString(undefined, {
        day: 'numeric',
        month: 'numeric',
        year: 'numeric',
        hour: '2-digit',
        minute: '2-digit',
    });
    headerDiv.classList.add('message-header');
    headerDiv.appendChild(document.createTextNode(
        user_name[0] + ' - ' + stringVersion));

    const bodyDiv = document.createElement('div');
    bodyDiv.classList.add('message-body');
    bodyDiv.innerHTML = message.text;

    const messageDiv = document.createElement('div');
    messageDiv.classList.add('message-div');
    messageDiv.classList.add('z-depth-1');
    messageDiv.appendChild(headerDiv);
    messageDiv.appendChild(bodyDiv);

    return messageDiv;
}


/**
 * Adds a login or logout link to the page, depending on whether the user is
 * already logged in.
 */
function addLoginOrLogoutLinkToNavigation() {
    const navigationElement = document.getElementById('navigation');
    if (!navigationElement) {
        console.warn('Navigation element not found!');
        return;
    }

    fetch('/login-status')
        .then((response) => {
        return response.json();
})
.then((loginStatus) => {
        if (loginStatus.isLoggedIn) {
        navigationElement.appendChild(createListItem(createLink(
            '/puzzle.html?user=' + loginStatus.username, 'Your Page')));

        navigationElement.appendChild(
            createListItem(createLink('/logout', 'Logout')));
    } else {
        navigationElement.appendChild(
            createListItem(createLink('/login', 'Login')));
    }
});
}



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
        scrollwheel: false,
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

function buildUI() {
    document.getElementById('puzzle-title').innerText = 'Puzzle: Level 6 of 6';
    document.getElementById('chat-title').innerText = 'Level 6 Chat';
    showMessageFormIfViewingSelf();
    fetchMessages();
    addLoginOrLogoutLinkToNavigation();
}