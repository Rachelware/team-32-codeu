

function drawChart() {
    var book_data = new google.visualization.DataTable();
    //define columns for the DataTable instance
    book_data.addColumn('string', 'Book Title');
    book_data.addColumn('number', 'Votes');

    //add data to book_data
    book_data.addRows([
        ["The Best We Could Do", 6],
        ["Sing, Unburied, Sing", 10],
        ["The Book of Unknown Americans", 7],
        ["The 57 Bus", 4],
        ["The Handmaid's Tale", 8]
    ]);

    var chart = new google.visualization.BarChart(document.getElementById('book_chart'));
    var chart_options = {
        width: 800,
        height: 400
    };
    chart.draw(book_data, chart_options);
}

function loadAll() {
    drawChart();
}