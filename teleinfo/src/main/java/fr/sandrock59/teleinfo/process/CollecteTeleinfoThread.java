package fr.sandrock59.teleinfo.process;

import fr.sandrock59.teleinfo.outils.LogManager;

public class CollecteTeleinfoThread extends Thread 
{
	long periodeSeconde = 5; 
	
	
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

	private void setPeriodeSeconde(long periode)
	{
		this.periodeSeconde = periode;
	}
	
	
	private void processus()
	{
		
		
		LogManager.log("On Collecte");
		
		
	}
	
}
