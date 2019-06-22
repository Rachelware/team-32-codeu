
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
    if(messages.length == 0){
        messageContainer.innerHTML = '<p>There are no posts yet.</p>';
    }
    else{
        messageContainer.innerHTML = '';
    }
    messages.forEach((message) => {
        const messageDiv = buildMessageDiv(message);
    messageContainer.appendChild(messageDiv);
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

/** Sets the page title based on the URL parameter username. */
function setPageTitle() {
    document.getElementById('page-title').innerText = datastore.getUser(parameterUsername).getLevel();
    document.title = parameterUsername + ' - User Page';
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
        const levelContainer = document.getElementById('level-container');
    const headerDiv = document.createElement('div');
    headerDiv.classList.add('level-header');
    headerDiv.appendChild(document.createTextNode(
        'Level: ' + level));
    document.getElementById('puzzle-title').innerText = 'Puzzle: Level ' + level;
    document.getElementById('chat-title').innerText = 'Level ' + level + ' Chat';
    levelContainer.appendChild(headerDiv);
});
}

/**
 * Builds an element that displays the message.
 * @param {Message} message
 * @return {Element}
 */
function buildMessageDiv(message) {
    const headerDiv = document.createElement('div');
    headerDiv.classList.add('message-header');
    headerDiv.appendChild(document.createTextNode(
        message.user + ' - ' + new Date(message.timestamp)));

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

/** Fetches data and populates the UI of the page. */
function buildUI() {
   //setPageTitle();
    showMessageFormIfViewingSelf();
    fetchMessages();
    addLoginOrLogoutLinkToNavigation();
    levelUp();
}
