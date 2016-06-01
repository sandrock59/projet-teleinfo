<%@page import="java.util.Date"%>
<%@page import="java.text.DateFormat"%>
<%@page import="fr.sandrock59.teleinfo.beans.InfosConso"%>
<%@page import="fr.sandrock59.teleinfo.persistance.ConnectionManager"%>
<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8" %>
<!DOCTYPE html PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN" "http://www.w3.org/TR/html4/loose.dtd">
<html>
	<head>
		<meta http-equiv="Content-Type" content="text/html; charset=UTF-8">
		<title>Suivi Consommation EDF</title>
	</head>
	<body>
		<% 
		//Récupération des infos de consomation
		String donneesPuissanceGoogle = ConnectionManager.getInstance().getDonneesPuissance(5);
		String donneesConsoGoogle = "[]";
		
		String optionConso = request.getParameter( "optionConso" );
		String selectedkWh = "";
		String selectedEuros = "";
		if(optionConso == null || optionConso.equals("kwh"))
		{
			selectedkWh = "selected";
			selectedEuros = "";
			//Affichage des données de conso en kWh
			donneesConsoGoogle = ConnectionManager.getInstance().getDonneesConsommation(365);
		}
		else
		{
			selectedkWh = "";
			selectedEuros = "selected";
			//Affichage des données de conso en euros
			donneesConsoGoogle = ConnectionManager.getInstance().getDonneesConsommationPrix(365);
		}
		
		String dateTitre = DateFormat.getDateTimeInstance(DateFormat.MEDIUM, DateFormat.MEDIUM).format(new Date());
		
		%>
		

		<script type="text/javascript" src="https://www.google.com/jsapi"></script>
	
		<table width="100%">
			<tr>
				<td width="20%"></td>
				<td width="60%" align="center"><h1><%=dateTitre%></h1></td>
				<td width="20%">
					
					<form method="post" action="index.jsp">
						Option d'affichage:
						<select id="optionConso", name="optionConso" onChange="submit()">
  							<option value="kwh" <%=selectedkWh%>>kWh</option>
  							<option value="euros" <%=selectedEuros%>>Euros</option>
						</select>
					</form>
				</td>
			</tr>
		</table>
	
		
	
		<div id="puissance">
  			<div id="chart_div"></div>
  			<div id="filter_div"></div>
		</div>
		
		
		<div id="conso"></div>
		<div id="consoPrix"></div>


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
			data.addRows(<%=donneesPuissanceGoogle%>);

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
		google.setOnLoadCallback(drawChart);

		function drawChart() {
			var data = new google.visualization.DataTable();
			data.addColumn('string', 'Date');
			data.addColumn('number', 'Heures pleines');
			data.addColumn('number', 'Heures creuses');
			data.addRows(<%=donneesConsoGoogle%>);
			var options = {
				title : '',
				height : 200,
				backgroundColor : '#FFF',
				colors : [ '#375D81', '#ABC8E2' ],
				curveType : 'function',
				focusTarget : 'category',
				lineWidth : '1',
				isStacked : true,
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
			};
			var chart = new google.visualization.ColumnChart(document
					.getElementById("conso"));
			chart.draw(data, options);
		}
	</script>


</body>
</html>