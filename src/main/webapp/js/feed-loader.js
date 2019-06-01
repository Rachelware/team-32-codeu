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



