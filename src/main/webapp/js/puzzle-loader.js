
// Get ?user=XYZ parameter value
const urlParams = new URLSearchParams(window.location.search);
const parameterUsername = urlParams.get('user');

// URL must include ?user=XYZ parameter. If not, redirect to homepage.
if (!parameterUsername) {
    window.location.replace('/');
}

/*
function fetchPuzzles(){
    const url = '/puzzle';
    fetch(url).then((response) => {
        return response.json();
    }).then((puzzle) => {
        const puzzleContainer = document.getElementById('puzzle-prompt');
        puzzleContainer.innerText = 'Prompt: ' + puzzle;
    });
}
*/

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

/** Loads the Level container and allows users to update their level */
function levelUp() {
    const url = '/user-level';
    fetch(url)
        .then((response) => {
        return response.json();
})
.then((level) => {
    document.getElementById('puzzle-title').innerText = 'Puzzle: Level ' + level + ' of 6';
    document.getElementById('chat-title').innerText = 'Level ' + level + ' Chat';
    if (level === 1) {
        document.getElementById("puzzle-container").innerHTML='<object type="text/html" data="../level1.html" ></object>';
    } else if (level === 2) {
        //TODO: Call this method ONLY when necessary for puzzle
        fetchBlobstoreUrlAndShowForm();
        document.getElementById("puzzle-container").innerHTML='<object type="text/html" data="../level2.html" ></object>';
        //one submit button document.getElementById("answer-section").innerHTML='<form id="my-form" action="/user-level" class="hidden" method="POST" enctype="multipart/form-data"> <h3 class="enter-answer"></h3><input type="file" name="image"><br/><br/><input class="submit-answer z-depth-1" type="submit" value="Submit Answer"></form>';
        document.getElementById("answer-section").innerHTML='<form id="my-form" class="hidden" method="POST" enctype="multipart/form-data"><p>Upload an image:</p><input type="file" name="image"><br/><button style="margin-left:35px; margin-top:10px">Upload</button></form> <form id="answer-form" action="/user-level" method="POST"> <h3 class="enter-answer">Submit Your Answer Here:</h3> <input class="submit-answer z-depth-1" type="submit" value="Submit Answer"></form>';
    } else if (level === 3) {
        document.getElementById("puzzle-container").innerHTML='<object type="text/html" data="../level3.html" ></object>';
    } else if (level === 4) {
        document.getElementById("puzzle-container").innerHTML='<object type="text/html" data="../level4.html" ></object>';
    } else if (level === 5) {
        document.getElementById("puzzle-container").innerHTML='<object type="text/html" data="../level5.html" ></object>';
    } else if (level === 6) {
        //document.getElementById("puzzle-container").innerHTML='<object type="text/html" data="../level6.html" ></object>';
        /*document.getElementById("answer-section").innerHTML='';
        //document.getElementById("puzzle-container").style.setProperty('height', '535px');
        document.head.innerHTML='<title>Puzzle Page</title>' +
            '<meta charset="UTF-8">' +
            '<link type="text/css" rel="stylesheet" href="/css/materialize.css" media="screen,projection"/>' +
            '<link rel = "stylesheet" href = "/css/main.css">' +
            '<link rel = "stylesheet" href = "/css/puzzle.css">' +
            '<link rel = "stylesheet" href = "/css/map.css">' +
            '<link rel = "stylesheet" href = "/css/level4.css">' +
            '<link href="https://fonts.googleapis.com/css?family=Open+Sans&display=swap" rel="stylesheet">' + '<script src="https://maps.googleapis.com/maps/api/js?key=AIzaSyBZkWQmagGmCHKRX2osG0BSWiUYLOZaxWA"></script> <script src="/js/puzzle-loader.js"></script>' +
            '<script src = "/js/scavenger-map-loader.js"></script>';
        document.getElementById("puzzle-container").innerHTML='<script src="https://maps.googleapis.com/maps/api/js?key=AIzaSyBZkWQmagGmCHKRX2osG0BSWiUYLOZaxWA"></script>\n' +
            '    <script src = "/js/scavenger-map-loader.js"></script><p>The following clues will lead you on a global scavenger hunt. The answer to each clue is a city somewhere in the world. To submit your response, drop a map pin on each city! In order to answer the puzzle correctly, all three pins must be placed within 1 degree of latitude and longitude of the city we are looking for. Good Luck!</p>\n' +
            '<div id="clues">\n' +
            '    <div id="clue" class="card">\n' +
            '        <div class="title">CLUE #1</div>\n' +
            '        <div class="text">\n' +
            '            <p>A sparkle in water. Hope for the poor. <br>\n' +
            '                Suddently, I was a fronteir no more! <br>\n' +
            '                Franticly, they flocked to my state, <br>\n' +
            '                for rare treasure discovered in 1848. <br>\n' +
            '                <i style="color: #263238">Drop a pin on my capitol city!</i></p>\n' +
            '        </div>\n' +
            '    </div>\n' +
            '    <div id="clue" class="card">\n' +
            '        <div class="title">CLUE #2</div>\n' +
            '        <div class="text">\n' +
            '            <p>\n' +
            '                Uranium, graphite, dosimeters, oh my! <br>\n' +
            '                A ghost-town, wasteland, accident, am I. <br>\n' +
            '                Who\'s fault, you ask? Don&#39t say a name! <br>\n' +
            '                My state played a cruel, sick game. <br>\n' +
            '                <i style="color: #263238">Drop a pin on my city!</i>\n' +
            '            </p>\n' +
            '        </div>\n' +
            '    </div>\n' +
            '    <div id="clue" class="card">\n' +
            '        <div class="title">CLUE #3</div>\n' +
            '        <div class="text">\n' +
            '            <p>\n' +
            '                <i>Vamos a El Centro!</i> Old walls, so high, <br>\n' +
            '                seat us as we see lively boats drive by! <br>\n' +
            '                Don&#39t burn, stay cool, dance, and enjoy <br>\n' +
            '                My color, my vibrance, my history ... <br>\n' +
            '                Oh and a cute <i>mochila</i>! Wow! <i>Me voy!</i> <br>\n' +
            '                <i style="color: #263238">Drop a pin on my city!</i>\n' +
            '            </p>\n' +
            '        </div>\n' +
            '    </div>\n' +
            '</div>\n' +
            '<br style="clear: left;" />\n' +
            '<div id="floating-panel">\n' +
            '    <input onclick="deleteMarkers();" type=button value="Delete Markers">\n' +
            '</div>\n' +
            '<div id="map"></div>\n' +
            '<p>Click on the map to add pins. Hover over pins to see the clue they are responding to.</p>\n' +
            '<div id="positions"></div>\n' +
            '<div id="answer-section" class="card z-depth-5">\n' +
            '    <form id="answer-form" action="/user-level" method="POST" class="hidden">\n' +
            '        <textarea name="answer" id="answer-input"></textarea>\n' +
            '        <input onclick="return saveMarkers();" type="submit" value="Sumbit Answers"><br>\n' +
            '    </form>\n' +
            '</div>\n' +
            '<script async defer\n' +
            '        src="https://maps.googleapis.com/maps/api/js?key=AIzaSyBZkWQmagGmCHKRX2osG0BSWiUYLOZaxWA&callback=initMap">\n' +
            '</script>';
        //document.body.appendChild('<script async defer src="https://maps.googleapis.com/maps/api/js?key=AIzaSyBZkWQmagGmCHKRX2osG0BSWiUYLOZaxWA&callback=initMap"></script>');
        const map_script = document.createElement('div');
        map_script.innerHTML = '<script async defer src="https://maps.googleapis.com/maps/api/js?key=AIzaSyBZkWQmagGmCHKRX2osG0BSWiUYLOZaxWA&callback=initMap"></script>';
        document.querySelector('body').appendChild(map_script); */
        window.location.replace('/puzzle_level6.html?user=' + parameterUsername);
    } else {
        //Redirect to Congratulations message
        window.location.replace('/escaped.html');
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

//Only call for image upload!
function fetchBlobstoreUrlAndShowForm() {
        fetch('/blobstore-upload-url')
          .then((response) => {
            return response.text();
          })
          .then((imageUploadUrl) => {
            const messageForm = document.getElementById('my-form');
            messageForm.action = imageUploadUrl;
            messageForm.classList.remove('hidden');
          });
}

function check() {
    if (document.getElementById("puzzle-container").innerHTML= '<object type="text/html" data="../incorrect.html"></object>') {
        window.location.replace('/incorrect.html');
    } else if (document.getElementById("puzzle-container").innerHTML= '<object type="text/html" data="../correct.html"></object>') {
        window.location.replace('/correct.html');
    } else if (document.getElementById("puzzle-container").innerHTML= '<object type="text/html" data="../escaped.html"></object>') {
        window.location.replace('/escaped.html');
    }
}



/** Fetches data and populates the UI of the page. */
function buildUI() {
    showMessageFormIfViewingSelf();
    fetchMessages();
    addLoginOrLogoutLinkToNavigation();
    levelUp();
}


