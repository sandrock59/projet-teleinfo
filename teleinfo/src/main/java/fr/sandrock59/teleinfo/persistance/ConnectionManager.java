package fr.sandrock59.teleinfo.persistance;

import java.sql.*;
import java.text.ParseException;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;

import org.apache.commons.configuration.PropertiesConfiguration;









import com.mysql.jdbc.PreparedStatement;

import fr.sandrock59.teleinfo.beans.InfosConso;
import fr.sandrock59.teleinfo.outils.LogManager;
import fr.sandrock59.teleinfo.outils.TeleinfoConnectionManagerGenerique;

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

			stmt.executeUpdate("CREATE TABLE IF NOT EXISTS TI_Consommation (date DATE, total_hc INTEGER, total_hp INTEGER, daily_hc BIGINT, daily_hp BIGINT);");
			stmt.executeUpdate("CREATE TABLE IF NOT EXISTS TI_Puissance (date DATETIME, hchp VARCHAR(2), va BIGINT, iinst BIGINT, watt BIGINT);");

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
	      String query = " INSERT INTO TI_Puissance (date, hchp, va, iinst, watt) VALUES (?, ?, ?, ?, ?)";
	 
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
	    InfosConso derniereInfosConso = this.getDerniereConso();
	    Date dateInfosEdf = null;
	    listeInfos.get("DATE");
	    try {
			dateInfosEdf = TeleinfoConnectionManagerGenerique.formatDateLecture.parse(listeInfos.get("DATE"));
		} catch (ParseException e) {
			e.printStackTrace();
			//Au pire on prend la date actuelle
			dateInfosEdf = new Date();
		}
	    
	    //On considère la date relevée comme étant le bilan de la veille
	    Calendar cal = Calendar.getInstance();
		cal.setTime(dateInfosEdf);
		cal.add(Calendar.DATE, -1);
		Date dateVeille = cal.getTime(); 
	    
	    java.sql.Date dateNouveauReleve = new java.sql.Date(dateVeille.getTime());
	    
	    //Si pas encore de relevé on en créé un premier
	    if(derniereInfosConso == null)
	    {
			try {
				String query = " INSERT INTO TI_Consommation (date, total_hc, total_hp, daily_hc, daily_hp) VALUES (?, ?, ?, ?, ?)";

				PreparedStatement preparedStmt = (PreparedStatement) conn.prepareStatement(query);
				preparedStmt.setDate(1, dateNouveauReleve);
				preparedStmt.setInt(2, new Integer(listeInfos.get("HCHC")));
				preparedStmt.setInt(3, new Integer(listeInfos.get("HCHP")));
				preparedStmt.setLong(4, 0);
				preparedStmt.setLong(5, 0);

				// execute the preparedstatement
				preparedStmt.execute();

			} catch (SQLException ex) {
				// handle any errors
				System.out.println("SQLException: " + ex.getMessage());
				System.out.println("SQLState: " + ex.getSQLState());
				System.out.println("VendorError: " + ex.getErrorCode());
			}
	    }
	    else
	    {
	    	//on a déjà un relevé
	    	try {
				String query = " INSERT INTO TI_Consommation (date, total_hc, total_hp, daily_hc, daily_hp) VALUES (?, ?, ?, ?, ?)";

				PreparedStatement preparedStmt = (PreparedStatement) conn.prepareStatement(query);
				preparedStmt.setDate(1, dateNouveauReleve);
				preparedStmt.setInt(2, new Integer(listeInfos.get("HCHC")));
				preparedStmt.setInt(3, new Integer(listeInfos.get("HCHP")));
				preparedStmt.setLong(4, (new Integer(listeInfos.get("HCHC")) - derniereInfosConso.getTotal_hc()) );
				preparedStmt.setLong(5, (new Integer(listeInfos.get("HCHP")) - derniereInfosConso.getTotal_hp()));

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
	
	
	public InfosConso getDerniereConso()
	{
		InfosConso infosConso = null;
		
		Statement stmt;
		try {
			stmt = conn.createStatement();
			String sql = "SELECT date, total_hc, total_hp, daily_hc, daily_hp FROM TI_Consommation order by date desc limit 1";
		      
			ResultSet rs = stmt.executeQuery(sql);

			while(rs.next())
			{
				infosConso = new InfosConso();
				infosConso.setDate(rs.getDate("date"));
				infosConso.setTotal_hc(rs.getInt("total_hc"));
				infosConso.setTotal_hp(rs.getInt("total_hp"));
				infosConso.setDaily_hc(rs.getLong("daily_hc"));
				infosConso.setDaily_hp(rs.getLong("daily_hp"));
			}
			rs.close();
		} 
		catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return infosConso;
	}
	
}
