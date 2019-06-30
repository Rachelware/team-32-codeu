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

function buildMessageDiv(message){
    const usernameDiv = document.createElement('div');
    usernameDiv.classList.add("left-align");
    usernameDiv.appendChild(document.createTextNode(message.user));
   
    const timeDiv = document.createElement('div');
    timeDiv.classList.add('right-align');
    timeDiv.appendChild(document.createTextNode(new Date(message.timestamp)));
   
    const headerDiv = document.createElement('div');
    headerDiv.classList.add('message-header');
    headerDiv.appendChild(usernameDiv);
    headerDiv.appendChild(timeDiv);
   
    const bodyDiv = document.createElement('div');
    bodyDiv.classList.add('message-body');
    bodyDiv.innerHTML = message.text;
   
    const messageDiv = document.createElement('div');
    messageDiv.classList.add("message-div");
    messageDiv.appendChild(headerDiv);
    messageDiv.appendChild(bodyDiv);
   
    return messageDiv;
}

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
  
// Fetch data and populate the UI of the page.
function buildUI(){
    fetchMessages();
    addLoginOrLogoutLinkToNavigation();
}



