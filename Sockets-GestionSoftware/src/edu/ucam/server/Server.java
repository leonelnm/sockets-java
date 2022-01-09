package edu.ucam.server;

import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.logging.Logger;

import edu.ucam.applications.commons.Client;
import edu.ucam.applications.commons.PortsUtils;
import edu.ucam.applications.commons.Product;

public class Server {
	
	private static final Logger logger = Logger.getLogger(Server.class.getName());
	
	private static Map<Integer, Client> clients;
	private static Map<Integer, Product> products;
	private static List<ServerThread> connectedClients;
	
	public void ejecutar() {

		try (ServerSocket serverSocket = new ServerSocket(PortsUtils.COMMAND_PORT)){
			
			logger.info("Server at port: " + PortsUtils.COMMAND_PORT);

			while (true) {
				Socket socket = serverSocket.accept();
				ServerThread serverThread = new ServerThread(socket);
				getConnectedClients().add(serverThread);
				serverThread.start();
			}

		} catch (IOException e) {
			logger.severe(e.getMessage());
			e.printStackTrace();
			
		}
		
	}

	public static void main(String[] args) {
		(new Server()).ejecutar();
	}

	public static synchronized Map<Integer, Product> getProducts() {
		if(products == null) {
			products = new HashMap<>();
		}
		return products;
	}
	
	public static synchronized Map<Integer, Client> getClients() {
		if(clients == null) {
			clients = new HashMap<>();
		}
		return clients;
	}
	
	public static synchronized List<ServerThread> getConnectedClients() {
		if(connectedClients == null) {
			connectedClients = new ArrayList<>();
		}
		return connectedClients;
	}
	
	public static synchronized void removeClient(ServerThread serverThread) {
		getConnectedClients().removeIf(e -> e.getId() == serverThread.getId());
	}
	

}
