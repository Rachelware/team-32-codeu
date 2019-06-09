function fetchStats(){
    const url = '/stats';
    fetch(url).then((response) => {
        return response.json();
}).then((stats) => {
        const statsContainer = document.getElementById('stats-container');
    statsContainer.innerHTML = '';

    const messageCountElement = buildStatElement('Total Messages: ' + stats.messageCount);
    statsContainer.appendChild(messageCountElement);
    const userCountElement = buildStatElement('Active Users: ' + stats.userCount);
    statsContainer.appendChild(userCountElement);
    const averageElement = buildStatElement('Average # of Messages per User: ' + stats.average);
    statsContainer.appendChild(averageElement);
});
}

function buildStatElement(statString) {
    const statElement = document.createElement('p');
    statElement.appendChild(document.createTextNode(statString));
    return statElement;
}


// Fetch data and populate the UI of the page.
function buildUI(){
    fetchStats();
}