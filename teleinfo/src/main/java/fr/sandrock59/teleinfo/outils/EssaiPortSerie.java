package fr.sandrock59.teleinfo.outils;

import fr.sandrock59.teleinfo.persistance.ConnectionManager;
import gnu.io.CommPortIdentifier;
import gnu.io.NoSuchPortException;
import gnu.io.PortInUseException;
import gnu.io.SerialPort;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.Date;
import java.util.Enumeration;
import java.util.HashMap;





public class EssaiPortSerie
{
	
	
	
    public static void main(String[] args)
    {

    	ConnectionManager connectionManager = new ConnectionManager();
    	connectionManager.initialisationBdd();
    	    	
    	//afficherListePort();

    	
    	
    	for(int i=0; i<10; i++)
    	{
    		lecturePort("COM3");
    		try {
				Thread.sleep(1000);
			} catch (InterruptedException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
    	}
    	
    	
    	


    }

    public static void lecturePort(String libellePort)
    {
        //récupération du port
        CommPortIdentifier portId = null;
        
        HashMap<String, String> infosEdf = new HashMap<>();
        
        try
        {
            portId = CommPortIdentifier.getPortIdentifier(libellePort);
        }
        catch (NoSuchPortException ex)
        {
            //traitement de l'exception
            ex.printStackTrace();
        }
        SerialPort port = null;
        try
        {

            if (portId != null)
            {
                port = (SerialPort) portId.open("Mon_Appli", 10000);
            }
            else
            {
                System.out.println("PortId null");
            }

            if (port != null)
            {
                try
                {
                    port.setFlowControlMode(SerialPort.FLOWCONTROL_NONE);
                    port.setSerialPortParams(1200, SerialPort.DATABITS_7, SerialPort.STOPBITS_2, SerialPort.PARITY_EVEN);

                    BufferedReader bufRead = new BufferedReader(new InputStreamReader(port.getInputStream()));
                    String chaine="";
                    boolean isTermine=false;
                    boolean isDebutFait=false;
                    String ligne;
        			while ((ligne=bufRead.readLine())!=null && !isTermine){
        				
        				String[] elements = ligne.split(" ");
        				if(ligne.startsWith("ADCO"))
        				{
        					isDebutFait=true;
        					infosEdf.put("DATE", new Date().toString());
        				}
        				if(isDebutFait && elements.length>=2)
        				{
        					infosEdf.put(elements[0], elements[1]);
        				}
        				if(ligne.startsWith("MOTDETAT")&& isDebutFait)
        				{
        					isTermine=true;
        				}
        				System.out.println(ligne);
        			}
                    

        			afficherInfosEdf(infosEdf);
        			
        			
                    
                    bufRead.close();
                    port.close();
                }
                catch (Exception ex)
                {
                    //gestion de l'exception
                    ex.printStackTrace();
                }
            }

        }
        catch (PortInUseException ex)
        {
            //traitement de l'exception
            ex.printStackTrace();
        }

    }

    public static void afficherListePort()
    {
        Enumeration<CommPortIdentifier> listePort = CommPortIdentifier.getPortIdentifiers();

        CommPortIdentifier portId;
        while (listePort.hasMoreElements())
        {
            portId = (CommPortIdentifier) listePort.nextElement();
            System.out.println(portId.getName());
        }
    }
    
    
    public static void afficherInfosEdf(HashMap<String, String> infosEdf)
    {
    	System.out.println("Date :"+infosEdf.get("DATE"));
    	System.out.println("N° d’identification du compteur :"+infosEdf.get("ADCO"));
    	System.out.println("Option tarifaire :"+infosEdf.get("OPTARIF"));
    	System.out.println("Intensité souscrite :"+infosEdf.get("ISOUSC"));
		System.out.println("Index heures creuses  :"+infosEdf.get("HCHC") + "Wh");
		System.out.println("Index heures pleines :"+infosEdf.get("HCHP")+ "Wh");
		System.out.println("Période tarifaire en cours :"+infosEdf.get("PTEC"));
		System.out.println("Intensité instantanée : :"+infosEdf.get("IINST"));
		System.out.println("Intensité souscrite :"+infosEdf.get("HCHC") + "A");
		System.out.println("Intensité maximale :"+infosEdf.get("IMAX") + "A");
		System.out.println("Puissance apparente :"+infosEdf.get("PAPP") + "VA");
		System.out.println("Groupe horaire :"+infosEdf.get("HHPHC"));
    }
}
