
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
        document.getElementById("answer-section").innerHTML='<form id="my-form" class="hidden" method="POST" enctype="multipart/form-data"><p>Upload an image:</p><input type="file" name="image"><br/><button>Submit</button></form> <form id="answer-form" action="/user-level" method="POST"> <h3 class="enter-answer">Submit Your Answer Here:</h3> <input class="submit-answer z-depth-1" type="submit" value="Submit Answer"></form>';
    } else if (level === 3) {
        document.getElementById("puzzle-container").innerHTML='<object type="text/html" data="../level3.html" ></object>';
    } else if (level === 4) {
        document.getElementById("puzzle-container").innerHTML='<object type="text/html" data="../level4.html" ></object>';
    } else if (level === 5) {
        document.getElementById("puzzle-container").innerHTML='<object type="text/html" data="../level5.html" ></object>';
    } else if (level === 6) {
        document.getElementById("puzzle-container").innerHTML='<object type="text/html" data="../level6.html" ></object>';
        document.getElementById("answer-section").innerHTML='';
        document.getElementById("puzzle-container").style.setProperty('height', '535px');
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


/** Fetches data and populates the UI of the page. */
function buildUI() {
    showMessageFormIfViewingSelf();
    fetchMessages();
    addLoginOrLogoutLinkToNavigation();
    levelUp();
}
