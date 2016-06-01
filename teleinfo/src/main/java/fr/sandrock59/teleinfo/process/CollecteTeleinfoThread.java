package fr.sandrock59.teleinfo.process;

import java.text.ParseException;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;

import fr.sandrock59.teleinfo.beans.InfosConso;
import fr.sandrock59.teleinfo.outils.LogManager;
import fr.sandrock59.teleinfo.outils.TeleinfoConnectionFakeManager;
import fr.sandrock59.teleinfo.outils.TeleinfoConnectionManager;
import fr.sandrock59.teleinfo.outils.TeleinfoConnectionManagerGenerique;
import fr.sandrock59.teleinfo.persistance.ConnectionManager;

public class CollecteTeleinfoThread extends Thread 
{
	private long periodeSeconde = 5; 
	private boolean isModeTest = false;
	private Date derniereDateConsoRelevee = null;
	private Date dateLimiteReleveeConso = null;
	
	
	public void run() 
	{
		while(true)
		{
			//Initilisation de la date de dernier relevé
			InfosConso infosConso = ConnectionManager.getInstance().getDerniereConso();
			if(infosConso != null && infosConso.getDate() != null)
			{
				derniereDateConsoRelevee = infosConso.getDate();
				
				Calendar cal = Calendar.getInstance();
				cal.setTime(derniereDateConsoRelevee);
				cal.add(Calendar.DATE, 2);
				
				dateLimiteReleveeConso = cal.getTime(); 
			}
			else
			{
				dateLimiteReleveeConso = new Date();
			}
			 
			
			//Lancement du traitement
			this.processus();
			
			//Période d'attente
			try {
				Thread.sleep(this.periodeSeconde * 1000);
			} catch (InterruptedException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			
			
		}
	}

	public void setPeriodeSeconde(long periode)
	{
		this.periodeSeconde = periode;
	}
	
	public void setIsModeTest(boolean isTest)
	{
		if(isTest)
		{
			LogManager.log("###############");
			LogManager.log("## Mode TEST ##");
			LogManager.log("###############");
		}
		
		
		this.isModeTest = isTest;
	}
	
	
	private void processus()
	{
		
		
		LogManager.log("Collecte des inforamtions");
			
		
		TeleinfoConnectionManagerGenerique teleinfoConnection = null;
		
		if(this.isModeTest)
		{
			teleinfoConnection = TeleinfoConnectionFakeManager.getInstance();
		}
		else
		{
			teleinfoConnection = TeleinfoConnectionManager.getInstance();
		}
		
		HashMap<String, String> listeInfos = teleinfoConnection.lectureTeleInfo();
		
		if(listeInfos !=null)
		{
			//Affichage des données EDF brut
			TeleinfoConnectionManagerGenerique.afficherInfosEdf(listeInfos);
			
			//On va stocker les inforamtions en BDD pour le graph de puissance
			ConnectionManager.getInstance().enregistrementDonneesPuissance(listeInfos);
			
			//On regarde si on doit relever les infos pour le suivi de consomation
			try {
				Date dateInfosEdf = teleinfoConnection.formatDateLecture.parse(listeInfos.get("DATE"));
				
	
				if(derniereDateConsoRelevee == null || dateLimiteReleveeConso.before(dateInfosEdf))
				{
					//Stockage des données en base
					ConnectionManager.getInstance().enregistrementDonneesConsomation(listeInfos);
				}
			} catch (ParseException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			
			
		}
		
		
		
	}
	
}
