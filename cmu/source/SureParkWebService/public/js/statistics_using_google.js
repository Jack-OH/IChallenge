google.charts.load('visualization', '1', {'packages':['bar', 'corechart', 'line', 'table']});

function drawBarChart(elementID) {
    var data = google.visualization.arrayToDataTable([
            ['Day', 'Sure-Park#1', 'Sure-Park#2'],
            ['21', 40, 10],
            ['22', 20, 60],
            ['23', 30, 70],
            ['24', 80, 40]
          ]);

          var options = {
            chart: {
              title: 'Average Occupancy',
                    subtitle: 'Average occupancy : Jun 2016',
            }
          };

          var chart = new google.charts.Bar(elementID);
          chart.draw(data, options);
}

function drawLineChart(elementID) {
        var data = google.visualization.arrayToDataTable([
          ['Hour', 'Sure-Park#1', 'Sure-Park#2'],
          ['01',  0,      90],
          ['02',  0,      90],
          ['03',  0,      90],
          ['04',  0,      90],
          ['05',  0,      90],
          ['06',  30,      80],
          ['07',  70,      50],
          ['08',  90,      40],
          ['09',  95,      40],
          ['10',  95,      30],
          ['11',  95,      20],
          ['12',  80,      10],
          ['13',  75,      10],
          ['14',  95,      5],
          ['15',  95,      20],
          ['16',  80,      40],
          ['17',  75,      50],
          ['18',  60,      60],
          ['19',  40,      70],
          ['20',  30,      70],
          ['21',  20,      85],
          ['22',  10,      88],
          ['23',  5,      90],
          ['24',  0,      90]
        ]);

        var options = {
          title: 'Peak Usage Hours',
          curveType: 'function',
          legend: { position: 'bottom' }
        };

        var chart = new google.visualization.LineChart(elementID);

        chart.draw(data, options);
}

function drawBarChart1(elementID) {
      var data = google.visualization.arrayToDataTable([
        ["Drivers", "Density", { role: "style" } ],
        ["Namjin", 300, "green"],
        ["Joan", 270, "gold"],
        ["Jaeheon", 250, "silver"],
        ["Jack", 200, "LightYellow"],
        ["Charles", 100, "color: #e5e4e2"]
      ]);

      var view = new google.visualization.DataView(data);
      view.setColumns([0, 1,
                       { calc: "stringify",
                         sourceColumn: 1,
                         type: "string",
                         role: "annotation" },
                       2]);

      var options = {
        title: "Heavy users's usage hour per month",
        width: 600,
        height: 400,
        bar: {groupWidth: "95%"},
        legend: { position: "none" },
      };
      var chart = new google.visualization.BarChart(elementID);
      chart.draw(view, options);
}

function drawBarChart2(elementID) {
    var data = google.visualization.arrayToDataTable([
            ['Day', 'Sure-Park#1', 'Sure-Park#2'],
            ['21', 400, 200],
            ['22', 200, 1200],
            ['23', 300, 1400],
            ['24', 800, 800]
          ]);

          var options = {
            chart: {
              title: 'Revenue ($)',
                    subtitle: 'Revenue : Jun 2016',
            }
          };

          var chart = new google.charts.Bar(elementID);
          chart.draw(data, options);
}

function drawBarChart3(elementID) {
    var data = google.visualization.arrayToDataTable([
            ['Day', 'Sure-Park#1', 'Sure-Park#2'],
            ['21', 10, 5],
            ['22', 5, 30],
            ['23', 6, 35],
            ['24', 15, 20]
          ]);

          var options = {
            chart: {
              title: 'Number of drivers',
                    subtitle: '# of using drivers : Jun 2016',
            }
          };

          var chart = new google.charts.Bar(elementID);
          chart.draw(data, options);
}

