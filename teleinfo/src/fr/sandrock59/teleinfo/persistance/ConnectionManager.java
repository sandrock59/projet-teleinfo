package fr.sandrock59.teleinfo.persistance;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.sql.Statement;

public class ConnectionManager 
{
	public Connection conn = null;
	
	public ConnectionManager()
	{
		try {
    	    conn = DriverManager.getConnection("jdbc:mysql://localhost/test?user=root");
    	} catch (SQLException ex) {
    	    // handle any errors
    	    System.out.println("SQLException: " + ex.getMessage());
    	    System.out.println("SQLState: " + ex.getSQLState());
    	    System.out.println("VendorError: " + ex.getErrorCode());
    	}
	}
	
	
	public void initialisationBdd()
	{
		try {
		Statement stmt = conn.createStatement();
	    
	       stmt.executeUpdate("CREATE TABLE IF NOT EXISTS conso (timestamp INTEGER, total_hc INTEGER, total_hp INTEGER, daily_hc REAL, daily_hp REAL);");
	       stmt.executeUpdate("CREATE TABLE IF NOT EXISTS puissance (timestamp INTEGER, hchp TEXT, va REAL, iinst REAL, watt REAL);");

		} catch (SQLException ex) {
    	    // handle any errors
    	    System.out.println("SQLException: " + ex.getMessage());
    	    System.out.println("SQLState: " + ex.getSQLState());
    	    System.out.println("VendorError: " + ex.getErrorCode());
    	}
	}
	
}
