var messageCountElement;
var userCountElement;
var averageElement;

function drawChart() {
    var book_data = new google.visualization.DataTable();

    //define columns for the DataTable instance
    book_data.addColumn('string', 'Site Statistics');
    book_data.addColumn('number', 'Number');

    //get data from JSON ur;
    fetch('/stats').then(res => res.json()).then((stats) => {
            messageCountElement = stats.messageCount;
            userCountElement = stats.userCount;
            averageElement = parseFloat(stats.average);
    });

    //add data to book_data
    book_data.addRows(
        [
            ["Mock", 6],
            ["Total Messages", messageCountElement],
            ["Active Users", userCountElement],
            ["Average # of Messages Per User", averageElement],
            ["Mock2", 4],
            ["Mock3", 2]
        ]
    );

    var chart = new google.visualization.BarChart(document.getElementById('book_chart'));
    var chart_options = {
        width: 800,
        height: 400
    };

    chart.draw(book_data, chart_options);
}