<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8" %>
<% 
	
	String donneesPuissanceGoogle = (String)session.getAttribute("donneesPuissanceGoogle");
%>

<div id="puissance">
	<div id="chart_div"></div>
	<div id="filter_div"></div>
</div>

<script type="text/javascript">
		google.load('visualization', '1.0', {
			'packages' : [ 'controls' ]
		});
		google.setOnLoadCallback(drawDashboard);

		function drawDashboard() {

			var data = new google.visualization.DataTable();
			data.addColumn('date', 'Date');
			data.addColumn('number', 'V.A');
			data.addColumn('number', 'kW');
			data.addRows([<%=donneesPuissanceGoogle%>]);

			var dashboard = new google.visualization.Dashboard(document
					.getElementById('puissance'));

			var rangeSlider = new google.visualization.ControlWrapper({
				'controlType' : 'ChartRangeFilter',
				'containerId' : 'filter_div',
				'options' : {
					filterColumnLabel : 'Date',
					ui : {
						chartType : 'LineChart',
						chartOptions : {
							height : 80,
							backgroundColor : '#FFF',
							colors : [ '#375D81', '#ABC8E2' ],
							curveType : 'function',
							focusTarget : 'category',
							lineWidth : '1',
							'legend' : {
								'position' : 'none'
							},
							'hAxis' : {
								'textPosition' : 'in'
							},
							'vAxis' : {
								'textPosition' : 'none',
								'gridlines' : {
									'color' : 'none'
								}
							}
						}
					}
				}
			});

			var lineChart = new google.visualization.ChartWrapper({
				'chartType' : 'LineChart',
				'containerId' : 'chart_div',
				'options' : {
					title : '',
					height : 400,
					backgroundColor : '#FFF',
					colors : [ '#375D81', '#ABC8E2' ],
					curveType : 'function',
					focusTarget : 'category',
					lineWidth : '1',
					legend : {
						position : 'bottom',
						alignment : 'center',
						textStyle : {
							color : '#333',
							fontSize : 16
						}
					},
					vAxis : {
						textStyle : {
							color : '#555',
							fontSize : '16'
						},
						gridlines : {
							color : '#CCC',
							count : 'auto'
						},
						baselineColor : '#AAA',
						minValue : 0
					},
					hAxis : {
						textStyle : {
							color : '#555'
						},
						gridlines : {
							color : '#DDD'
						}
					}
				}
			});

			dashboard.bind(rangeSlider, lineChart);
			dashboard.draw(data);
		}
		google.load("visualization", "1", {
			packages : [ "corechart" ]
		});
</script>