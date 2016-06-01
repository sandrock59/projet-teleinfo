package fr.sandrock59.teleinfo.outils;

import java.text.SimpleDateFormat;
import java.util.HashMap;

import org.apache.commons.configuration.PropertiesConfiguration;

public class TeleinfoConnectionManagerGenerique {

	public static SimpleDateFormat formatDateLecture = new SimpleDateFormat("yyyy/MM/dd HH:mm:ss");
	protected PropertiesConfiguration conf;
	protected String nomPort = null;

	
	public TeleinfoConnectionManagerGenerique() {
		super();
	}

	public HashMap<String, String> lectureTeleInfo() 
	{
		return null;
	}
	
	
	public static void afficherInfosEdf(HashMap<String, String> infosEdf)
    {
		System.out.println("--------------------------------------------------");
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
		System.out.println("--------------------------------------------------");
    }

}