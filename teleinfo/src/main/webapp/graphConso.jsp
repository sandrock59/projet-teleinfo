<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8" %>
<% 
String donneesConsoGoogleKWh = (String)session.getAttribute("donneesConsoGoogleKWh");
String donneesConsoGooglePrix = (String)session.getAttribute("donneesConsoGooglePrix");
String donneesConsoGoogle = "";

String optionConso = request.getParameter( "optionConso" );
if(optionConso == null)
{
	optionConso = (String)session.getAttribute("optionConso");
}
String selectedkWh = "";
String selectedEuros = "";
if(optionConso == null || optionConso.equals("kwh"))
{
	selectedkWh = "selected";
	selectedEuros = "";
	optionConso="kWh";
	//Affichage des données de conso en kWh
	donneesConsoGoogle = donneesConsoGoogleKWh;
}
else
{
	selectedkWh = "";
	selectedEuros = "selected";
	//Affichage des données de conso en euros
	donneesConsoGoogle = donneesConsoGooglePrix;
}
%>


<div id="conso"></div>

<script type="text/javascript">
		google.setOnLoadCallback(drawChart);

		function drawChart() {
			var data = new google.visualization.DataTable();
			data.addColumn('string', 'Date');
			data.addColumn('number', 'Heures pleines');
			data.addColumn('number', 'Heures creuses');
			data.addRows([<%=donneesConsoGoogle%>]);
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