package fr.sandrock59.teleinfo.persistance;

import java.sql.*;
import java.util.Date;
import java.util.HashMap;

import org.apache.commons.configuration.PropertiesConfiguration;




import com.mysql.jdbc.PreparedStatement;

import fr.sandrock59.teleinfo.outils.LogManager;

public class ConnectionManager {
	private static ConnectionManager singleInstance;

	public Connection conn = null;

	private PropertiesConfiguration conf;

	public static synchronized ConnectionManager getInstance() {
		if (singleInstance == null) {
			singleInstance = new ConnectionManager();
			
		}
		return singleInstance;
	}

	public ConnectionManager() {
		LogManager.log("Initilisation de la BDD");
		conf = new PropertiesConfiguration();
		try {
			conf.load("teleinfo.properties");
		} catch (Exception e) {
			LogManager.log("Erreur lors du chargement du fichier de constante");
		}

		try {
			// Chargement du driver JDBC pour MySQL
			try {
			    Class.forName( "com.mysql.jdbc.Driver" );
			} catch ( ClassNotFoundException e ) {
			    e.printStackTrace();
			}
			conn = DriverManager
					.getConnection("jdbc:mysql://"+conf.getString("BDD_URL")+"/"+conf.getString("BBD_NAME")+"?user="+conf.getString("BDD_USER")+"&password="+conf.getString("BDD_PWD"));
			//Initialisation des tables
			this.initialisationBdd();
		} catch (SQLException ex) {
			// handle any errors
			LogManager.log("SQLException: " + ex.getMessage());
			LogManager.log("SQLState: " + ex.getSQLState());
			LogManager.log("VendorError: " + ex.getErrorCode());
		}
	}

	
	public void initialisationBdd() {
		try {
			Statement stmt = conn.createStatement();

			stmt.executeUpdate("CREATE TABLE IF NOT EXISTS TI_Consommation (timestamp DATETIME, total_hc INTEGER, total_hp INTEGER, daily_hc BIGINT, daily_hp BIGINT);");
			stmt.executeUpdate("CREATE TABLE IF NOT EXISTS TI_Puissance (timestamp DATETIME, hchp VARCHAR(2), va BIGINT, iinst BIGINT, watt BIGINT);");

		} catch (SQLException ex) {
			// handle any errors
			System.out.println("SQLException: " + ex.getMessage());
			System.out.println("SQLState: " + ex.getSQLState());
			System.out.println("VendorError: " + ex.getErrorCode());
		}
	}

	
	public void enregistrementDonneesPuissance(HashMap<String, String> listeInfos)
	{
		try {
		     // the mysql insert statement
	      String query = " INSERT INTO TI_Puissance (timestamp, hchp, va, iinst, watt) VALUES (?, ?, ?, ?, ?)";
	 
	      // create the mysql insert preparedstatement
	      PreparedStatement preparedStmt = (PreparedStatement) conn.prepareStatement(query);
	      preparedStmt.setTimestamp(1, new Timestamp(new Date().getTime()));
	      preparedStmt.setString(2, listeInfos.get("PTEC").substring(0,2));
	      preparedStmt.setLong(3, new Long(listeInfos.get("PAPP")));
	      preparedStmt.setLong(4, new Long(listeInfos.get("IINST")));
	      preparedStmt.setLong(5, new Long(listeInfos.get("IINST")) * 220);
	 
	      // execute the preparedstatement
	      preparedStmt.execute();

		} catch (SQLException ex) {
		// handle any errors
			System.out.println("SQLException: " + ex.getMessage());
			System.out.println("SQLState: " + ex.getSQLState());
			System.out.println("VendorError: " + ex.getErrorCode());
		}
	}
	
	
	public void enregistrementDonneesConsomation(HashMap<String, String> listeInfos)
	{
	    
	    
		
//		$datas = array();
//	    $datas['query']     = 'hchp';
//	    $datas['timestamp'] = $today;
//	    $datas['total_hc']  = preg_replace('`^[0]*`','',$trame['HCHC']); // conso total en Wh heure creuse, on supprime les 0 en debut de chaine
//	    $datas['total_hp']  = preg_replace('`^[0]*`','',$trame['HCHP']); // conso total en Wh heure pleine, on supprime les 0 en debut de chaine
//
//	    if($previous['total_hc'] == 0){
//	      $datas['daily_hc'] = 0;
//	    }
//	    else{
//	      $datas['daily_hc']  = ($datas['total_hc']-$previous['total_hc'])/1000; // conso du jour heure creuse = total aujourd'hui - total hier, on divise par 1000 pour avec un resultat en kWh
//	    }
//
//	    if($previous['total_hp'] == 0){
//	      $datas['daily_hp'] = 0;
//	    }
//	    else{
//	      $datas['daily_hp']  = ($datas['total_hp']-$previous['total_hp'])/1000; // conso du jour heure pleine = total aujourd'hui - total hier, on divise par 1000 pour avec un resultat en kWh
//	    }
//
//	    if($db->busyTimeout(5000)){ // stock les donnees
//	      $db->exec("INSERT INTO conso (timestamp, total_hc, total_hp, daily_hc, daily_hp) VALUES (".$datas['timestamp'].", ".$datas['total_hc'].", ".$datas['total_hp'].", ".$datas['daily_hc'].", ".$datas['daily_hp'].");");
//	    }
		
	    
	    try {
			     // the mysql insert statement
		      String query = " INSERT INTO TI_Consommation (timestamp, total_hc, total_hp, daily_hc, daily_hp) VALUES (?, ?, ?, ?, ?)";
		 
		      // create the mysql insert preparedstatement
		      PreparedStatement preparedStmt = (PreparedStatement) conn.prepareStatement(query);
		      preparedStmt.setDate(1, new java.sql.Date(new Date().getTime()));
		      preparedStmt.setInt(2, new Integer(listeInfos.get("HCHC")));
		      preparedStmt.setInt(3, new Integer(listeInfos.get("HCHP")));
//		      preparedStmt.setLong(4, );
//		      preparedStmt.setLong(5, );
		 
		      // execute the preparedstatement
		      preparedStmt.execute();

		} catch (SQLException ex) {
			// handle any errors
			System.out.println("SQLException: " + ex.getMessage());
			System.out.println("SQLState: " + ex.getSQLState());
			System.out.println("VendorError: " + ex.getErrorCode());
		}
	}
	
}
