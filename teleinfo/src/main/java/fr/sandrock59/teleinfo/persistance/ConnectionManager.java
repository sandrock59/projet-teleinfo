package fr.sandrock59.teleinfo.persistance;

import java.math.BigDecimal;
import java.sql.*;
import java.text.DateFormat;
import java.text.NumberFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.Locale;
import java.util.TreeMap;

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

			stmt.executeUpdate("CREATE TABLE IF NOT EXISTS TI_Consommation (date datetime NOT NULL,total_hc int(11) DEFAULT NULL,total_hp int(11) DEFAULT NULL,daily_hc bigint(20) DEFAULT NULL,daily_hp bigint(20) DEFAULT NULL, PRIMARY KEY (`date`)) ENGINE=InnoDB DEFAULT CHARSET=utf8;");
			stmt.executeUpdate("CREATE TABLE IF NOT EXISTS TI_Puissance (date datetime NOT NULL,hchp varchar(2) DEFAULT NULL,va bigint(20) DEFAULT NULL,iinst bigint(20) DEFAULT NULL,watt bigint(20) DEFAULT NULL, PRIMARY KEY (`date`)) ENGINE=InnoDB DEFAULT CHARSET=utf8;");

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

	
	public String getDonneesPuissanceJour(String jour)
	{
		SimpleDateFormat simpleDateFormatGoogle = new SimpleDateFormat("yyyy, MM, dd, HH, mm, ss");
		SimpleDateFormat simpleDateFormatLecture = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
		
		DateFormat simpleDateFormatTexteGoogle = DateFormat.getDateTimeInstance(DateFormat.MEDIUM, DateFormat.SHORT);

		String donneesPuissance = "";
		
		try {
			Date dateDebut = simpleDateFormatLecture.parse(jour + " 00:00:00");
			
			Calendar cal = Calendar.getInstance();
			cal.setTime(new Date());
			cal.add(Calendar.DATE, 1);
			Date dateFin = cal.getTime(); 
			
			
			String query = "SELECT date, hchp, va, iinst, watt FROM TI_Puissance WHERE date >= ? AND date < ? ORDER BY date ASC;";

			PreparedStatement preparedStmt = (PreparedStatement) conn.prepareStatement(query);
			preparedStmt.setDate(1, new java.sql.Date(dateDebut.getTime()));
			preparedStmt.setDate(2, new java.sql.Date(dateFin.getTime()));
			preparedStmt.execute();
			ResultSet rs = preparedStmt.executeQuery();

			while(rs.next())
			{
				
				if(donneesPuissance.length() > 1)
				{
					//On ajout une virgule car encore des données à ajouter)
					donneesPuissance = donneesPuissance + ",";
				}
				
				Date dateData = rs.getTimestamp("date");
				long va =  rs.getLong("va");
				long watt = rs.getLong("watt");
				
				String donnesUnitaire = "[{v:new Date("+simpleDateFormatGoogle.format(dateData)+"), f:'"+simpleDateFormatTexteGoogle.format(dateData)+"'}, {v:"+va+", f:'"+va+" V.A'},{v:"+watt+", f:'"+watt+" W'}]";
				
				donneesPuissance = donneesPuissance + donnesUnitaire;
				
			}
			rs.close();
		} 
		catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		return donneesPuissance;
	}
	
	
	public String getDonneesPuissance(int nbJour)
	{
		SimpleDateFormat simpleDateFormatGoogle = new SimpleDateFormat("yyyy, MM, dd, HH, mm, ss");
		DateFormat simpleDateFormatTexteGoogle = DateFormat.getDateTimeInstance(DateFormat.MEDIUM, DateFormat.SHORT);

		String donneesPuissance = "";
		
		try {
			//On considère la date relevée comme étant le bilan de la veille
		    Calendar cal = Calendar.getInstance();
			cal.setTime(new Date());
			cal.add(Calendar.DATE, nbJour * -1);
			Date dateDebut = cal.getTime(); 
			
			
			String query = "SELECT date, hchp, va, iinst, watt FROM TI_Puissance WHERE date > ? ORDER BY date ASC;";

			PreparedStatement preparedStmt = (PreparedStatement) conn.prepareStatement(query);
			preparedStmt.setDate(1, new java.sql.Date(dateDebut.getTime()));
			preparedStmt.execute();
			ResultSet rs = preparedStmt.executeQuery();

			while(rs.next())
			{
				
				if(donneesPuissance.length() > 1)
				{
					//On ajout une virgule car encore des données à ajouter)
					donneesPuissance = donneesPuissance + ",";
				}
				
				Date dateData = rs.getTimestamp("date");
				long va =  rs.getLong("va");
				long watt = rs.getLong("watt");
				
				String donnesUnitaire = "[{v:new Date("+simpleDateFormatGoogle.format(dateData)+"), f:'"+simpleDateFormatTexteGoogle.format(dateData)+"'}, {v:"+va+", f:'"+va+" V.A'},{v:"+watt+", f:'"+watt+" W'}]";
				
				donneesPuissance = donneesPuissance + donnesUnitaire;
				
			}
			rs.close();
		} 
		catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		

		return donneesPuissance;
	}
	
	
	public TreeMap<String , String> getDonneesPuissanceRefresh(int nbJour, TreeMap<String , String> donneesPuissance, String dateDemande)
	{
		SimpleDateFormat simpleDateFormatLecture = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
		SimpleDateFormat simpleDateFormatGoogle = new SimpleDateFormat("yyyy, MM, dd, HH, mm, ss");
		DateFormat simpleDateFormatTexteGoogle = DateFormat.getDateTimeInstance(DateFormat.MEDIUM, DateFormat.SHORT);
		SimpleDateFormat simpleDateFormatDemande = new SimpleDateFormat("dd-MM-yyyy");
		
		
		Date dateFin = null;
		if(dateDemande != null && dateDemande.equals(simpleDateFormatDemande.format(new Date())))
		{
			System.out.println("date now");
			dateFin=new Date();
		}
		else
		{
			try {
				if(dateDemande != null)
				{
					dateFin =  simpleDateFormatDemande.parse(dateDemande);
					Calendar cal = Calendar.getInstance();
					cal.setTime(dateFin);
					cal.setTime(dateFin);
					cal.add(Calendar.DATE, 1);
					dateFin = cal.getTime();
					
				}
				else
				{
					dateFin = new Date();
				}
				
			} catch (ParseException e1) {
				dateFin = new Date();
			}
			
		}
		
		
		
		
		
		TreeMap<String, String> donneesPuissanceResult = new TreeMap<String, String>();
		
		//On recherche les enregistrements de donnes qui ne doivent plus être affichée
		//On considère la date relevée comme étant le bilan de la veille
	    Calendar cal = Calendar.getInstance();
		cal.setTime(dateFin);
		cal.add(Calendar.DATE, nbJour * -1);
		Date dateDebut = cal.getTime();
		
		
		String dateDonneesPlusRecente = simpleDateFormatLecture.format(dateDebut);
		
		
		System.out.println("date fin:"+String.valueOf(simpleDateFormatLecture.format(dateFin)));
		System.out.println("date debut:"+String.valueOf(simpleDateFormatLecture.format(dateDebut)));
		
//		if(donneesPuissance == null)
//		{
//			donneesPuissance = new TreeMap<>();
//		}
//		
//		for(String keyDate : donneesPuissance.keySet())
//		{
//			//On ne conserve que les dates dans la période demandée
//			if(keyDate.compareTo(simpleDateFormatLecture.format(dateDebut)) > 0 && keyDate.compareTo(simpleDateFormatLecture.format(dateFin)) < 0) 
//			{
//				donneesPuissanceResult.put(keyDate, donneesPuissance.get(keyDate));
//				if(dateDonneesPlusRecente.compareTo(keyDate) < 0)
//				{
//					dateDonneesPlusRecente = keyDate;
//				}
//			}
//		}
		
		// ==> Tableau nettoyé
		
		
		//Récupération des données manquantes
		try {
			String query = "SELECT date, hchp, va, iinst, watt FROM TI_Puissance WHERE date > ?  AND date < ? ORDER BY date ASC;";

			PreparedStatement preparedStmt = (PreparedStatement) conn.prepareStatement(query);
			preparedStmt.setString(1, dateDonneesPlusRecente);
			preparedStmt.setString(2, simpleDateFormatLecture.format(dateFin));
			preparedStmt.execute();
			ResultSet rs = preparedStmt.executeQuery();

			System.out.println("date debut:"+String.valueOf(new java.sql.Date(dateFin.getTime())));
			
			while(rs.next())
			{
				Date dateData = rs.getTimestamp("date");
				long va =  rs.getLong("va");
				long watt = rs.getLong("watt");
				
				String donnesUnitaire = "[{v:new Date("+simpleDateFormatGoogle.format(dateData)+"), f:'"+simpleDateFormatTexteGoogle.format(dateData)+"'}, {v:"+va+", f:'"+va+" V.A'},{v:"+watt+", f:'"+watt+" W'}]";
				
				donneesPuissanceResult.put(simpleDateFormatLecture.format(dateData), donnesUnitaire);
			}
			rs.close();
		} 
		catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		

		return donneesPuissanceResult;
	}
	
	
	public String transformerMapDonnees(TreeMap<String, String> donneesMap)
	{
		String resultat = null;
		
		for(String valeur:donneesMap.values())
		{
			if(resultat == null)
			{
				resultat = valeur;
			}
			else
			{
				resultat = resultat + ","+valeur;
			}
		}
		
		return resultat;
	}
	
	
	public String getDonneesConsommation(int nbJour)
	{
		DateFormat simpleDateFormatTexteGoogle = DateFormat.getDateInstance(DateFormat.MEDIUM);

		String donneesConsomation = "";
		
		try {
			//On considère la date relevée comme étant le bilan de la veille
		    Calendar cal = Calendar.getInstance();
			cal.setTime(new Date());
			cal.add(Calendar.DATE, nbJour * -1);
			Date dateDebut = cal.getTime(); 
			
			
			String query = "SELECT date, total_hc, total_hp, daily_hc, daily_hp FROM TI_Consommation WHERE date > ? ORDER BY date ASC;";

			PreparedStatement preparedStmt = (PreparedStatement) conn.prepareStatement(query);
			preparedStmt.setDate(1, new java.sql.Date(dateDebut.getTime()));
			preparedStmt.execute();
			ResultSet rs = preparedStmt.executeQuery();

			while(rs.next())
			{
				
				if(donneesConsomation.length() > 1)
				{
					//On ajout une virgule car encore des données à ajouter)
					donneesConsomation = donneesConsomation + ",";
				}
				
				Date dateData = rs.getTimestamp("date");
				double daily_hc =  new Double(rs.getLong("daily_hc"))/1000.0;
				double daily_hp = new Double(rs.getLong("daily_hp"))/1000.0;
				
				
				String donnesUnitaire = "['"+simpleDateFormatTexteGoogle.format(dateData)+"', {v:"+daily_hp+",f:'"+daily_hp+" kWh'}, {v:"+daily_hc+",f:'"+daily_hc+" kWh'}]";
				
				donneesConsomation = donneesConsomation + donnesUnitaire;
				
			}
			rs.close();
		} 
		catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		

		return donneesConsomation;
	}
	 
	public String getDonneesConsommationPrix(int nbJour)
	{
		DateFormat simpleDateFormatTexteGoogle = DateFormat.getDateInstance(DateFormat.MEDIUM);

		String donneesConsomation = "";
		
		try {
			//On considère la date relevée comme étant le bilan de la veille
		    Calendar cal = Calendar.getInstance();
			cal.setTime(new Date());
			cal.add(Calendar.DATE, nbJour * -1);
			Date dateDebut = cal.getTime(); 
			
			
			String query = "SELECT date, total_hc, total_hp, daily_hc, daily_hp FROM TI_Consommation WHERE date > ? ORDER BY date ASC;";

			PreparedStatement preparedStmt = (PreparedStatement) conn.prepareStatement(query);
			preparedStmt.setDate(1, new java.sql.Date(dateDebut.getTime()));
			preparedStmt.execute();
			ResultSet rs = preparedStmt.executeQuery();

			while(rs.next())
			{
				
				if(donneesConsomation.length() > 1)
				{
					//On ajout une virgule car encore des données à ajouter)
					donneesConsomation = donneesConsomation + ",";
				}
				
				Date dateData = rs.getTimestamp("date");
				BigDecimal daily_hc =  (new BigDecimal(rs.getLong("daily_hc")).divide(new BigDecimal("1000"))).multiply(new BigDecimal("0.115"));
				BigDecimal daily_hp =  (new BigDecimal(rs.getLong("daily_hp")).divide(new BigDecimal("1000"))).multiply(new BigDecimal("0.1636"));

				NumberFormat n = NumberFormat.getCurrencyInstance(Locale.FRANCE);
				String shc = n.format(daily_hc);
				String shp = n.format(daily_hp);
				
				String donnesUnitaire = "['"+simpleDateFormatTexteGoogle.format(dateData)+"', {v:"+daily_hp+",f:'"+shp+"'}, {v:"+daily_hc+",f:'"+shc+"'}]";
				
				donneesConsomation = donneesConsomation + donnesUnitaire;
				
			}
			rs.close();
		} 
		catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		

		return donneesConsomation;
	}
	
}
