
function drawChart() {
    var messageCountElement;
    var userCountElement;
    var averageElement;

    //get data from JSON ur;
    fetch('/stats').then(res => res.json())
    .then((stats) => {
        console.log("");
        messageCountElement = stats.messageCount;
        userCountElement = stats.userCount;
        averageElement = parseFloat(stats.average);

        var data = google.visualization.arrayToDataTable([
            ["Stats", "Number", { role: "style" } ],
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


        var chart = new google.charts.Bar(document.getElementById('book_chart'));
        var chart_options = {
            width: 800,
            colors: ['#ffffff'],
            chart: {
                title: ''
            },
            fontSize: 18,
            bars: 'horizontal',
            legend: { position: "none" },
            hAxis: {
                textStyle: {
                    color: '#ffffff'
                }
            },
            vAxis: {
                textStyle: {
                    color: '#ffffff'
                }
            },
            axes: {
                x: {
                    0: { side: 'bottom', color: '#ffffff', label: ''} // Top x-axis.
                },
                y: {
                    0: {label: ''}
                }
            },
            backgroundColor: '#263238',
            annotations: {
                textStyle: {
                    fontSize: 18,
                    color: '#f2f3f5'
                }
            }
        };

        chart.draw(view, google.charts.Bar.convertOptions(chart_options));
    });
}