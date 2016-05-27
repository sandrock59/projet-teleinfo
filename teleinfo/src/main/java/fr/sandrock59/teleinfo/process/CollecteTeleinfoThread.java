package fr.sandrock59.teleinfo.process;

import java.util.HashMap;

import fr.sandrock59.teleinfo.outils.LogManager;
import fr.sandrock59.teleinfo.outils.TeleinfoConnectionFakeManager;
import fr.sandrock59.teleinfo.outils.TeleinfoConnectionManager;
import fr.sandrock59.teleinfo.outils.TeleinfoConnectionManagerGenerique;
import fr.sandrock59.teleinfo.persistance.ConnectionManager;

public class CollecteTeleinfoThread extends Thread 
{
	private long periodeSeconde = 5; 
	private boolean isModeTest = false;
	
	public void run() 
	{
		while(true)
		{
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
			teleinfoConnection.afficherInfosEdf(listeInfos);
			
			//On va stocker les inforamtions en BDD
			ConnectionManager.getInstance().enregistrementDonneesPuissance(listeInfos);
			
		}
		
		
		
	}
	
}
