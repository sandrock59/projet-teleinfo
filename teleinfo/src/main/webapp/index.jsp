<%@page import="java.util.TreeMap"%>
<%@page import="java.text.SimpleDateFormat"%>
<%@page import="java.util.Date"%>
<%@page import="java.text.DateFormat"%>
<%@page import="fr.sandrock59.teleinfo.beans.InfosConso"%>
<%@page import="fr.sandrock59.teleinfo.persistance.ConnectionManager"%>
<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8" %>

		<% 
		session = request.getSession(true);
        SimpleDateFormat simpleDAteFormatJour = new SimpleDateFormat("yyyy-MM-dd");
		
		
		//Récupération de la date du jour pour optimiser les requêtes:
		String dateDonneesSession = (String)session.getAttribute("dateDonneesSession");
		
        //Données de consommation en session
        String donneesConsoGoogleKWh = (String)session.getAttribute("donneesConsoGoogleKWh");
        String donneesConsoGooglePrix = (String)session.getAttribute("donneesConsoGooglePrix");
        String donneesPuissanceGoogle = (String)session.getAttribute("donneesPuissanceGoogle");
        TreeMap<String, String> donneesPuissanceMap = (TreeMap<String, String>)session.getAttribute("donneesPuissanceMap");
        
        

		//Récupération des infos de consomation
		if(dateDonneesSession == null)
		{
			//On a encore rien récupéré on doit tout charger
			donneesPuissanceMap = ConnectionManager.getInstance().getDonneesPuissanceRefresh(5, donneesPuissanceMap);
			donneesPuissanceGoogle = ConnectionManager.getInstance().transformerMapDonnees(donneesPuissanceMap);
			
			donneesConsoGooglePrix = ConnectionManager.getInstance().getDonneesConsommationPrix(365);
			donneesConsoGoogleKWh = ConnectionManager.getInstance().getDonneesConsommation(365);
			dateDonneesSession = simpleDAteFormatJour.format(new Date());
		}
		else
		{
			//On ne recharge que ce qui est utile:
			String dateMaintenant = simpleDAteFormatJour.format(new Date());
			if(dateMaintenant.equals(dateDonneesSession))
			{
				//On n'a pas changé de jour, on ne recharge que les données de puissance
				donneesPuissanceMap = ConnectionManager.getInstance().getDonneesPuissanceRefresh(5, donneesPuissanceMap);
				donneesPuissanceGoogle = ConnectionManager.getInstance().transformerMapDonnees(donneesPuissanceMap);
			}
			else
			{
				//On doit recharger les données de puissance et conso
				donneesPuissanceMap = ConnectionManager.getInstance().getDonneesPuissanceRefresh(5, donneesPuissanceMap);
				donneesPuissanceGoogle = ConnectionManager.getInstance().transformerMapDonnees(donneesPuissanceMap);
			
				donneesConsoGooglePrix = ConnectionManager.getInstance().getDonneesConsommationPrix(365);
				donneesConsoGoogleKWh = ConnectionManager.getInstance().getDonneesConsommation(365);
				dateDonneesSession = simpleDAteFormatJour.format(new Date());	
			}
		}
		
		
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
		}
		else
		{
			selectedkWh = "";
			selectedEuros = "selected";
		}
		
		//Option de refresh
		String optionRaffraichir[] = request.getParameterValues( "raffraichir" );
		String optionPasRaffraichir = request.getParameter( "pasRaffraichir" );
		if(optionRaffraichir == null && optionPasRaffraichir == null)
		{
			optionRaffraichir = (String[])session.getAttribute("optionRaffraichir");
			if(optionRaffraichir == null)
			{
				optionPasRaffraichir="No";
			}
		}
		else
		{
			if(optionRaffraichir != null)
			{
				 optionPasRaffraichir="";
			}
		}
		String checkedRaffraichir = "";
		String cmdRaffraichir = "<meta http-equiv='refresh' content='60'>";
		if (optionRaffraichir != null && optionRaffraichir.length != 0) 
		{
			checkedRaffraichir = "checked";
		}
		else
		{
			cmdRaffraichir="";
		}
		
		//Date à afficher
		String dateTitre = DateFormat.getDateTimeInstance(DateFormat.MEDIUM, DateFormat.MEDIUM).format(new Date());
		
		//Stockage en session
		session.setAttribute("optionConso", optionConso);
		session.setAttribute("optionRaffraichir", optionRaffraichir);
		session.setAttribute("dateDonneesSession", dateDonneesSession);
		session.setAttribute("donneesPuissanceGoogle", donneesPuissanceGoogle);
		session.setAttribute("donneesConsoGooglePrix", donneesConsoGooglePrix);
		session.setAttribute("donneesConsoGoogleKWh", donneesConsoGoogleKWh);
		session.setAttribute("donneesPuissanceMap", donneesPuissanceMap);
		
		
		%>
		
<!DOCTYPE html PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN" "http://www.w3.org/TR/html4/loose.dtd">
<html>
	<head>
		<%=cmdRaffraichir%>
		<meta http-equiv="Content-Type" content="text/html; charset=UTF-8">
		<title>Suivi Consommation EDF</title>
	</head>
	<body>
		<script type="text/javascript" src="https://www.google.com/jsapi"></script>
	
		<table width="100%" border=0>
			<tr>
				<td width="40%" align="center">
					<img src="http://www.prevision-meteo.ch/uploads/widget/douvrin_0.png" width="550" height="200" />
				</td>
				<td width="20%" align="center"><h1><%=dateTitre%></h1></td>
				<td width="30%" align="right">
					
					<form method="post" action="index.jsp">
						Option d'affichage:
						<select id="optionConso", name="optionConso" onChange="submit()">
  							<option value="kwh" <%=selectedkWh%>>kWh</option>
  							<option value="euros" <%=selectedEuros%>>Euros</option>
						</select>
						<br/>
						Raffraichir :<input type="checkbox" name="raffraichir" value="raffraichir" onchange="submit()" <%=checkedRaffraichir%>>
						<input id='pasRaffraichir' type='hidden' value='No' name='pasRaffraichir'>
					</form>
				</td>
				<td width="10%"></td>
			</tr>
		</table>
	
		
	
		<jsp:include page="graphPuissance.jsp" />
		<jsp:include page="graphConso.jsp" />
	
	</body>
</html>