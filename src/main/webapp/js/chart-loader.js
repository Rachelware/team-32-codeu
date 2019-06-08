
function drawChart() {
    //var book_data = new google.visualization.DataTable();

    var messageCountElement;
    var userCountElement;
    var averageElement;

    //define columns for the DataTable instance
    //book_data.addColumn('string', 'Site Statistics');
    //book_data.addColumn('number', 'Number');

    //get data from JSON ur;
    fetch('/stats').then(res => res.json())
    .then((stats) => {
        console.log("");
        messageCountElement = stats.messageCount;
        userCountElement = stats.userCount;
        averageElement = parseFloat(stats.average);

        var data = google.visualization.arrayToDataTable([
            ["Stat", "Number", { role: "style" } ],
            ["Total Messages", messageCountElement, "color: #e5e4e2"],
            ["Active Users", userCountElement, "color: #e5e4e2"],
            ["Average # of Messages Per User", averageElement, "color: #e5e4e2"]
        ]);

        var view = new google.visualization.DataView(data);
        view.setColumns([0, 1,
            { calc: "stringify",
                sourceColumn: 1,
                type: "string",
                role: "annotation" },
            2]);


        var chart = new google.visualization.BarChart(document.getElementById('book_chart'));
        var chart_options = {
            title: "Site Statistics",
            legend: { position: "none" },
            width: 800,
            height: 400
        };

        chart.draw(view, chart_options);
    });
}