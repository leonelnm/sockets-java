package edu.ucam.server;

import java.io.IOException;
import java.net.ServerSocket;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import edu.ucam.pojos.Finca;

public class Server {

	public static int PORT = 2020;
	private int countClients;
	private static List<Integer> ports;
	private static Map<Integer, ClientHandler> clientsConected;
	private static Map<Integer, Finca> fincas;
	private static Map<String, String> users;

	private ServerSocket serverSocket;

	public Server() {
		this.countClients = 1;
		Server.clientsConected = new HashMap<Integer, ClientHandler>();
		Server.fincas = initTableFincas();
		Server.users = initTableUsers();
		
		ports = new ArrayList<Integer>();
		for (int i = 2021; i < 2030; i++) {
			ports.add(i);
		}
		
	}

	private Map<Integer, Finca> initTableFincas() {
		Map<Integer, Finca> fincas = new HashMap<Integer, Finca>();
		fincas.put(1, new Finca(1, "Finca 1", 100));
		fincas.put(2, new Finca(2, "Finca 2", 200));
		fincas.put(3, new Finca(3, "Finca 3", 300));
		return fincas;
	}

	private Map<String, String> initTableUsers() {
		Map<String, String> map = new HashMap<String, String>();
		map.put("admin", "admin");
		map.put("erik", "1234");
		map.put("ucam", "ucam");
		return map;
	}

	public void startServer() {
		System.out.println("Server running...");
		System.out.println("Server waits for clients...");
		try {
			serverSocket = new ServerSocket(PORT);
			while (true) {
				ClientHandler client = new ClientHandler(serverSocket.accept(), getCountClients());
				System.out.println("Cliend ID: " + getCountClients() + " connected!");

				registerNewClient(client);

				client.start();
			}
		} catch (IOException e) {
			System.err.println(e.getMessage());
		}
	}

	public void stop() {
		try {
			serverSocket.close();
		} catch (IOException e) {
			System.err.println("No se ha podido cerrar serverSocket");
			e.printStackTrace();
		}
	}

	private void registerNewClient(ClientHandler client) {
		addCountClient();
		getClientsConected().put(getCountClients(), client);
	}

	public int getCountClients() {
		return countClients;
	}

	public void addCountClient() {
		this.countClients++;
	}

	public static Map<Integer, ClientHandler> getClientsConected() {
		return clientsConected != null ? clientsConected : new HashMap<Integer, ClientHandler>();
	}

	public static Map<String, String> getUsers() {
		return users != null ? users : new HashMap<String, String>();
	}

	public static Map<Integer, Finca> getFincas() {
		return fincas != null ? fincas : new HashMap<Integer, Finca>();
	}

	public static List<Integer> getPorts() {
		return ports != null ? ports : new ArrayList<Integer>();
	}
	
}
