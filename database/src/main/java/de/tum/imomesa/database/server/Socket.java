package de.tum.imomesa.database.server;

import org.eclipse.jetty.websocket.api.Session;
import org.eclipse.jetty.websocket.api.annotations.OnWebSocketClose;
import org.eclipse.jetty.websocket.api.annotations.OnWebSocketConnect;
import org.eclipse.jetty.websocket.api.annotations.WebSocket;

@WebSocket
public class Socket {
	
	private Session session;
	
	public Session getSession() {
		return session;
	}
 
    @OnWebSocketConnect
    public void onConnect(Session session) {
    	this.session = session;
    	
    	// Register socket on database
    	Database.getInstance().registerSocket(this);
    }
 
    @OnWebSocketClose
    public void onClose(int statusCode, String reason) {
    	// Unregister socket on database
    	Database.getInstance().unregisterSocket(this);
    	
    	session = null;
    }

}
