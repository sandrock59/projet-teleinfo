package fr.sandrock59.teleinfo.persistance;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.sql.Statement;

import org.apache.commons.configuration.PropertiesConfiguration;

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

			stmt.executeUpdate("CREATE TABLE IF NOT EXISTS TI_Consommation (timestamp INTEGER, total_hc INTEGER, total_hp INTEGER, daily_hc REAL, daily_hp REAL);");
			stmt.executeUpdate("CREATE TABLE IF NOT EXISTS TI_Puissance (timestamp INTEGER, hchp TEXT, va REAL, iinst REAL, watt REAL);");

		} catch (SQLException ex) {
			// handle any errors
			System.out.println("SQLException: " + ex.getMessage());
			System.out.println("SQLState: " + ex.getSQLState());
			System.out.println("VendorError: " + ex.getErrorCode());
		}
	}

}
