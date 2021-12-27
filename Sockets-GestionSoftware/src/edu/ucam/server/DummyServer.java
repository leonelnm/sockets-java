package edu.ucam.server;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.ServerSocket;
import java.net.Socket;

import edu.ucam.applications.commons.PortsUtils;

public class DummyServer {
	
	public void ejecutar() {
		
		try(ServerSocket serverSocket = new ServerSocket(PortsUtils.COMMAND_PORT)){
			
			System.out.println("Init server at port: " +  PortsUtils.COMMAND_PORT);
			
			Socket socketServer = serverSocket.accept();
			PrintWriter pw = new PrintWriter(socketServer.getOutputStream());
			BufferedReader br = new BufferedReader(new InputStreamReader(socketServer.getInputStream()));
			
			//Write
			pw.write("Hola desde Server");
			pw.flush();
			
			//Read
			System.out.println("Get info from client: " + br.readLine());
			
			serverSocket.close();
		} catch (IOException e) {
			e.printStackTrace();
		}
		
	}

	public static void main(String[] args) {
		(new DummyServer()).ejecutar();

	}

}
