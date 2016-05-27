package fr.sandrock59.teleinfo.outils;

import java.util.Date;
import java.util.HashMap;


public class TeleinfoConnectionFakeManager extends TeleinfoConnectionManagerGenerique {
	
private static TeleinfoConnectionFakeManager singleInstance;
	
	public static synchronized TeleinfoConnectionFakeManager getInstance() {
		if (singleInstance == null) {
			singleInstance = new TeleinfoConnectionFakeManager();
		}
		return singleInstance;
	}
	
	
	public TeleinfoConnectionFakeManager() {
		
		//Rien : classe fake
	}
	
	
	@Override
	public HashMap<String, String> lectureTeleInfo() {
		HashMap<String, String> listeInfos = new HashMap<>();
		
		
		listeInfos.put("DATE", formatDateLecture.format(new Date()));
		listeInfos.put("ADCO", "271028236633");
		listeInfos.put("OPTARIF", "HC..");
		listeInfos.put("ISOUSC", "60");
		listeInfos.put("HCHC", "034238011");
		listeInfos.put("HCHP", "049088542");
		listeInfos.put("PTEC", "HP..");
		listeInfos.put("IINST", "002");
		listeInfos.put("IMAX", "063");
		listeInfos.put("PAPP", "00570");
		listeInfos.put("HHPHC", "D");
		
		
		return listeInfos;
	}
	
	
	
}
