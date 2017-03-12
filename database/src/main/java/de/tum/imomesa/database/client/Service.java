package de.tum.imomesa.database.client;

import java.net.URI;

import org.eclipse.jetty.client.HttpClient;
import org.eclipse.jetty.client.api.Request;
import org.eclipse.jetty.websocket.client.ClientUpgradeRequest;
import org.eclipse.jetty.websocket.client.WebSocketClient;

public class Service {
	
	private HttpClient http_client = new HttpClient();
	private WebSocketClient websocket_client = new WebSocketClient();
	
	private Socket socket;
	
	private String host;
	private int port;
	
	public Service(String host, int port, Socket socket) {
		this.host = host;
		this.port = port;
		this.socket = socket;
	}
	
	public void start() throws Exception {
		// Start http
		http_client.start();
		
		// Start web socket
		websocket_client.start();
		websocket_client.connect(socket, new URI("ws://" + host + ":" + port + "/socket"), new ClientUpgradeRequest());
	}
	
	public void stop() throws Exception {
		// Stop http
		http_client.stop();
		
		// Stop web socket
		websocket_client.stop();
	}
	
	public Request createRequest() {
		return http_client.newRequest("http://" + host + ":" + port + "/request");
	}

}
