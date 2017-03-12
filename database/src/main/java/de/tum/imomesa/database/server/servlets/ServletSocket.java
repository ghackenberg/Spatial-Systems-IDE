package de.tum.imomesa.database.server.servlets;

import org.eclipse.jetty.websocket.servlet.WebSocketServlet;
import org.eclipse.jetty.websocket.servlet.WebSocketServletFactory;

import de.tum.imomesa.database.server.Socket;

public class ServletSocket extends WebSocketServlet {
	
	private static final long serialVersionUID = 2294250138387389023L;

	@Override
	public void configure(WebSocketServletFactory factory) {
        factory.register(Socket.class);
	}

}
