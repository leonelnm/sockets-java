package edu.ucam.applications.commons;

import java.io.IOException;
import java.net.ServerSocket;

public class PortsUtils {
	
	private PortsUtils() {}
	
	public static final int COMMAND_PORT = 2022;
	
	public static synchronized int getAvailableDataPort() {
		int dataPort = COMMAND_PORT +1;
		boolean isAvailable = false;
		
		while(!isAvailable) {
			try(ServerSocket ss = new ServerSocket(dataPort)){
				isAvailable = true;
			}catch (IOException e) {
				System.err.println("Port " + dataPort + " is not available!");
				dataPort += 1;
			}
		}
		return dataPort;
	}
	
}
