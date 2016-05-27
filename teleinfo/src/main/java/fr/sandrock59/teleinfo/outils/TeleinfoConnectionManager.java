package fr.sandrock59.teleinfo.outils;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.Date;
import java.util.HashMap;

import gnu.io.CommPortIdentifier;
import gnu.io.SerialPort;

import org.apache.commons.configuration.PropertiesConfiguration;


public class TeleinfoConnectionManager extends TeleinfoConnectionManagerGenerique {
	
	private static TeleinfoConnectionManager singleInstance;
	
	public static synchronized TeleinfoConnectionManager getInstance() {
		if (singleInstance == null) {
			singleInstance = new TeleinfoConnectionManager();
		}
		return singleInstance;
	}
	
	public TeleinfoConnectionManager() {
		LogManager.log("Initilisation de connection teleinfo");
		conf = new PropertiesConfiguration();
		try {
			conf.load("teleinfo.properties");
			this.nomPort = conf.getString("TELEINFO_PORT");
			
		} catch (Exception e) {
			LogManager.log("Erreur lors de l'initialisation de la connection teleinfo");
			e.printStackTrace();
		}
	}
	
	
	@Override
	public HashMap<String, String> lectureTeleInfo() {
		HashMap<String, String> listeInfos = new HashMap<>();
		BufferedReader bufRead = null;
		SerialPort portSerie =null;
		
		try
		{
			CommPortIdentifier port = null;
			port = CommPortIdentifier.getPortIdentifier(conf.getString("TELEINFO_PORT"));
			
			if (port != null)
	        {
				listeInfos.put("DATE", formatDateLecture.format(new Date()));
				
				portSerie = (SerialPort) port.open("SRV-TELEINFO", 10000);
				portSerie.setFlowControlMode(SerialPort.FLOWCONTROL_NONE);
				portSerie.setSerialPortParams(1200, SerialPort.DATABITS_7, SerialPort.STOPBITS_2, SerialPort.PARITY_EVEN);
				bufRead = new BufferedReader(new InputStreamReader(portSerie.getInputStream()));
				
	            boolean isTermine=false;
	            boolean isDebutFait=false;
	            String ligne;
				while ((ligne=bufRead.readLine())!=null && !isTermine){
					
					String[] elements = ligne.split(" ");
					if(ligne.startsWith("ADCO"))
					{
						isDebutFait=true;
					}
					if(isDebutFait && elements.length>=2)
					{
						listeInfos.put(elements[0], elements[1]);
					}
					if(ligne.startsWith("MOTDETAT")&& isDebutFait)
					{
						isTermine=true;
					}
				}
	        }
	        else
	        {
	            listeInfos = null;
	        }
		}
		catch(Exception e)
		{
			LogManager.log("Erreur au cours de la récupération des données du conpteur EDF");
			e.printStackTrace();
			listeInfos = null;
		}
		finally
		{
			if(bufRead != null)
			{
				try {
					bufRead.close();
				} catch (IOException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			}
	        if(portSerie != null)
	        {
	        	portSerie.close();
	        }
		}
		
		return listeInfos;
	}
	
	
	
}
