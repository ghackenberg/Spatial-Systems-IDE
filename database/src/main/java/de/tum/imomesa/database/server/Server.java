package de.tum.imomesa.database.server;

import org.eclipse.jetty.servlet.ServletContextHandler;

import de.tum.imomesa.database.server.servlets.ServletRequest;
import de.tum.imomesa.database.server.servlets.ServletSocket;

public class Server {
	
	private ServletContextHandler handler;
	private org.eclipse.jetty.server.Server server;
	
	public Server(int port) {
		// Create servlet context handler
		handler = new ServletContextHandler();
		handler.setContextPath("/");
		handler.addServlet(ServletRequest.class, "/request");
		handler.addServlet(ServletSocket.class, "/socket");
		
		// Create Jetty server
		server = new org.eclipse.jetty.server.Server(port);
		server.setHandler(handler);
	}
	
	public void start() throws Exception {
		Database.getInstance().start();
		server.start();
	}
	
	public void stop() throws Exception {
		server.stop();
		Database.getInstance().stop();
	}

}
