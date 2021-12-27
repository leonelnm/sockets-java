package edu.ucam.server;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.ServerSocket;
import java.net.Socket;

import edu.ucam.applications.commons.CommandRequest;
import edu.ucam.server.protocol.ProtocolManagement;

public class DataChannelThread extends Thread {
	
	private int port;
	private Socket socket;
	private CommandRequest request;
	private ObjectInputStream ios;
	private ObjectOutputStream oos; 
	private ServerThread commandChannel;
	
	public DataChannelThread(int port, ServerThread commandChannel, CommandRequest request) {
		this.port = port;
		this.request = request;
		this.commandChannel = commandChannel;
	}

	@Override
	public void run() {
		
		try(ServerSocket dataServerSocket = new ServerSocket(this.port)){
			dataServerSocket.setSoTimeout(30000);
			
			System.out.println("ServerSocket Data waiting by client at: " + this.port);
			socket = dataServerSocket.accept();
			System.out.println("Client connected!");
			initializeBuffers(socket);
			
			new ProtocolManagement().managementData(request, commandChannel, this);

			clouseBuffers();
			socket.close();
		} catch (Exception e) {
			System.err.println("Error on dataCHanelTHREAD " + e.getMessage());
		} 
		
	}
	
	private void initializeBuffers(Socket socket) throws IOException {
		this.oos = new ObjectOutputStream(socket.getOutputStream());
		this.ios = new ObjectInputStream(socket.getInputStream());
	}
	
	private void clouseBuffers() throws IOException{
		this.oos.close();
		this.ios.close();
	}

	public CommandRequest getRequest() {
		return request;
	}

	public ObjectInputStream getIos() {
		return ios;
	}

	public ObjectOutputStream getOos() {
		return oos;
	}

	public Socket getSocket() {
		return socket;
	}
	
	public Object readObject() {
		try {
			return this.ios.readObject();
		} catch (ClassNotFoundException | IOException e) {
			System.err.println("Error at READ data " + e.getMessage());
		}
		
		return null;
	}
	
	public void sendObject(Object obj) {
		try {
			this.oos.writeObject(obj);
			this.oos.flush();
		} catch (IOException e) {
			System.err.println("Error at SEND data " + e.getMessage());
		}
		
	}
	
}
