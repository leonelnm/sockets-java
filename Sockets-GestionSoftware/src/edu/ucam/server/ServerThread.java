package edu.ucam.server;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.Socket;
import java.util.logging.Logger;

import edu.ucam.applications.commons.Command;
import edu.ucam.applications.commons.Messages;
import edu.ucam.applications.commons.User;
import edu.ucam.server.protocol.IProtocolManagement;
import edu.ucam.server.protocol.ProtocolManagement;

public class ServerThread extends Thread {

	private static final Logger logger = Logger.getLogger(ServerThread.class.getName());

	private Socket socket;
	private BufferedReader br;
	private PrintWriter pw;
	private User user;
	private Socket keepAliveSocket;

	public ServerThread(Socket socket) throws IOException {
		logger.info("Client connected -> " + this.getId());
		this.socket = socket;
		initializeBuffers(this.socket);
		this.user = new User();
	}

	@Override
	public void run() {
		
		// open keepAlive channel
		KeepAliveThread keepAliveThread = new KeepAliveThread(this);
		keepAliveThread.start();

		try {
			IProtocolManagement protocol = new ProtocolManagement();
			String readed = "";

			while (((readed = br.readLine()) != null)) {
				System.out.println("IN  ("+ this.getId() +") <- " + readed);
				if(isExit(readed)) {
					protocol.sendMessage(Messages.OK, readed, Messages.COD_OK, "Bye", pw);	
					
				}else if(Command.CONECTADOS.name().equals(readed)) {
					pw.println(Server.getConnectedClients().size());
					pw.flush();
					
				}else {
					protocol.executeProtocol(this.user, readed, this);
				}
			}
			
			logger.info("Client disconnected -> " + this.getId());
			Server.removeClient(this);

		} catch (IOException e) {
			logger.severe(e.getMessage());
		}

	}

	private boolean isExit(String readed) {
		if(readed != null) {
			String[] partes = readed.split(" ");
			String command = partes.length > 1 ? partes[1] : "";
			return Command.EXIT.name().equals(command);	
		}
		
		return false;
	}

	private void initializeBuffers(Socket socket) throws IOException {
		this.br = new BufferedReader(new InputStreamReader(socket.getInputStream()));
		this.pw = new PrintWriter(socket.getOutputStream());
	}
	
	public void closeAll() throws IOException{
		this.br.close();
		this.pw.close();
		this.socket.close();
	}

	public BufferedReader getBr() {
		return br;
	}

	public PrintWriter getPw() {
		return pw;
	}
	
	public Socket getCommandSocket() {
		return socket;
	}
	
	public User getUser() {
		return user;
	}
	
	public Socket getKeepAliveSocket() {
		return keepAliveSocket;
	}
	
	public void setKeepAliveSocket(Socket socket) {
		this.keepAliveSocket = socket;
	}

}
