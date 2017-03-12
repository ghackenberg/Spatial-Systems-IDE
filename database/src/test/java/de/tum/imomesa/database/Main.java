package de.tum.imomesa.database;

import javafx.collections.ObservableList;
import de.tum.imomesa.database.client.Client;
import de.tum.imomesa.database.model.Hobby;
import de.tum.imomesa.database.model.Person;
import de.tum.imomesa.database.server.Server;

public class Main {

	public static final String HOST = "localhost";
	public static final int PORT = 8080;
	
	public static void main(String[] args) {
	
		try {
			// Create server
			Server server = new Server(PORT);
			
			// Create clients
			Client[] clients = new Client[2];
			for (int i = 0; i < clients.length; i++) {
				clients[i] = new Client(HOST, PORT);
			}
			
			// Start server
			server.start();
			
			// Start clients
			for (Client client : clients) {
				client.start();
			}
			
			// Sleep
			Thread.sleep(50);
			
			// Query clients
			for (Client client : clients) {
				ObservableList<Person> persons = client.getObjects(Person.class);
				System.out.println(persons);
			}
			
			// Sleep
			Thread.sleep(50);
			
			// Update object
			clients[0].manageObject(new Person());
			clients[0].getObjects(Person.class).get(0).setName(clients[0].getObjects(Person.class).get(0).getName() + "+");
			clients[0].manageObject(new Person());
			clients[0].getObjects(Person.class).get(1).getHobbies().add(new Hobby());
			clients[0].getObjects(Person.class).get(1).getHobbies().add(new Hobby());
			clients[0].releaseObject(clients[0].getObjects(Person.class).get(1));
			clients[0].manageObject(new Person());
			for (Person person : clients[0].getObjects(Person.class)) {
				clients[0].releaseObject(person);
			}
			clients[0].manageObject(new Person());
			
			// Sleep
			Thread.sleep(50);
			
			// Query clients
			for (Client client : clients) {
				ObservableList<Person> persons = client.getObjects(Person.class);
				System.out.println(persons);
			}
			
			// Sleep
			Thread.sleep(50);

			// Stop clients
			for (Client client : clients) {
				client.stop();
			}
			
			// Stop server
			server.stop();
		}
		catch (Exception e) {
			e.printStackTrace();
		}
		
	}
	
}
