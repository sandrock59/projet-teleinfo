package fr.sandrock59.teleinfo.servlet;

import java.io.IOException;
import java.text.ParseException;
import java.util.Date;
import java.util.Enumeration;
import java.util.HashMap;

import javax.servlet.GenericServlet;
import javax.servlet.ServletException;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;

import org.apache.commons.configuration.PropertiesConfiguration;

import fr.sandrock59.teleinfo.outils.LogManager;
import fr.sandrock59.teleinfo.outils.TeleinfoConnectionFakeManager;
import fr.sandrock59.teleinfo.outils.TeleinfoConnectionManager;
import fr.sandrock59.teleinfo.outils.TeleinfoConnectionManagerGenerique;
import fr.sandrock59.teleinfo.persistance.ConnectionManager;
import fr.sandrock59.teleinfo.process.CollecteTeleinfoThread;
import gnu.io.CommPortIdentifier;


public class ServiceTeleinfoServlet extends GenericServlet   {

	private static final long serialVersionUID = 1L;

	private PropertiesConfiguration conf;
	
	public ServiceTeleinfoServlet(){
		
		super();
		System.out.println("test");
	}
	
	
	@Override
    public void init() throws ServletException
    {
		super.init();
		
		System.out.println("test2");
		conf = new PropertiesConfiguration();
		try
    	{
    		conf.load("teleinfo.properties");
    	}
    	catch(Exception e)
    	{
    		LogManager.log("Erreur lors du chargement du fichier de constante");
    	}
		
		LogManager.log("###############################################################");
		LogManager.log("## Initialisation du Thread de collecte des données Téléinfo ##");
		LogManager.log("###############################################################");
		
		test();
		
		
		CollecteTeleinfoThread CollecteThread = new CollecteTeleinfoThread() ;
		CollecteThread.setPeriodeSeconde(conf.getLong("PERIODE_SCAN_TELEINFO"));
		CollecteThread.setIsModeTest(conf.getBoolean("TELEINFO_IS_MODE_TEST"));
		CollecteThread.start();

	}


	@Override
	public void service(ServletRequest arg0, ServletResponse arg1) throws ServletException, IOException {
		// TODO Auto-generated method stub

	}
	
	
	
	private void test()
	{
		
		try
		{
			
		
			LogManager.log("####################### Test du USB");
				
			CommPortIdentifier port = null;

			LogManager.log("Ouverture du port :"+conf.getString("TELEINFO_PORT"));
			Enumeration<CommPortIdentifier> listePort = CommPortIdentifier.getPortIdentifiers();
			while(listePort.hasMoreElements())
			{
				CommPortIdentifier portTest = listePort.nextElement();
				System.out.println(portTest.getName());
				System.out.println(portTest.getPortType());
			}
			
			
			
		}
		catch(Exception e)
		{
			LogManager.log("[ALERT] Erreur lors du test USB");
			e.printStackTrace();
		}
		
		
	}
	
}
