package de.tum.imomesa.database.server.servlets;

import java.io.IOException;

import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.json.JSONObject;

import de.tum.imomesa.database.server.Database;

public class ServletRequest extends HttpServlet {

	private static final long serialVersionUID = -7203119944266974587L;
	
	@Override
	public void doPut(HttpServletRequest request, HttpServletResponse response) throws IOException {
		JSONObject json = new JSONObject(request.getReader().readLine());
		response.getWriter().write(Database.getInstance().manageObject(json).toString());
	}
	
	@Override
	public void doPost(HttpServletRequest request, HttpServletResponse response) throws IOException {
		JSONObject json = new JSONObject(request.getReader().readLine());
		Database.getInstance().updateObject(json);
	}
	
	@Override
	public void doGet(HttpServletRequest request, HttpServletResponse response) throws IOException {
		response.getWriter().write(Database.getInstance().getJson().toString());
	}
	
	@Override
	public void doDelete(HttpServletRequest request, HttpServletResponse response) throws IOException {
		JSONObject json = new JSONObject(request.getReader().readLine());
		Database.getInstance().releaseObject(json);
	}
	
	@Override
	public void doHead(HttpServletRequest request, HttpServletResponse response) throws IOException {
		
	}
	
	@Override
	public void doOptions(HttpServletRequest request, HttpServletResponse response) throws IOException {
		
	}
	
	@Override
	public void doTrace(HttpServletRequest request, HttpServletResponse response) throws IOException {
		
	}

}
