package fr.sandrock59.teleinfo.servlet;

import java.io.IOException;

import javax.servlet.GenericServlet;
import javax.servlet.ServletException;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;

import fr.sandrock59.teleinfo.outils.LogManager;
import fr.sandrock59.teleinfo.process.CollecteTeleinfoThread;


public class ServiceTeleinfoServlet extends GenericServlet   {

	private static final long serialVersionUID = 1L;

	public ServiceTeleinfoServlet(){
		super();
	}
	
	
	@Override
    public void init() throws ServletException
    {
		super.init();
		LogManager.log("###############################################################");
		LogManager.log("## Initialisation du Thread de collecte des données Téléinfo ##");
		LogManager.log("###############################################################");
		
		CollecteTeleinfoThread CollecteThread = new CollecteTeleinfoThread() ;
		CollecteThread.start();

	}


	@Override
	public void service(ServletRequest arg0, ServletResponse arg1) throws ServletException, IOException {
		// TODO Auto-generated method stub

	}
}
