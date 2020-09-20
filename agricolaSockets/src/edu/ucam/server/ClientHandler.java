package edu.ucam.server;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.Socket;

import edu.ucam.actions.Actions;
import edu.ucam.actions.ActionsHelper;
import edu.ucam.server.message.Message;
import edu.ucam.server.message.MessageValidator;
import edu.ucam.server.protocol.FincaProtocol;
import edu.ucam.server.protocol.LoginProtocol;

public class ClientHandler extends Thread {

	private Socket clientSocket;
	private PrintWriter out;
	private BufferedReader in;
	private int idClient;
	private boolean authorized;
	private String user;

	public ClientHandler(Socket socket, int id) {
		this.clientSocket = socket;
		this.idClient = id;
		this.setAuthorized(true); //TODO change to false
		this.setUser(null);
	}

	@Override
	public void run() {

		try {
			initializeBuffers(clientSocket);
			
			String msgFromClient;

			while ((msgFromClient = in.readLine()) != null) {
				// print all received messages
				System.out.println(msgFromClient);

				if (MessageValidator.validate(msgFromClient)) {
					if ("EXIT".equals(msgFromClient.split(" ")[1])) {
						sendMessageToClient("OK " + msgFromClient.split(" ")[0] + " Bye");
						break;
					}
					evaluateMessageAction(msgFromClient);
				} else {
					sendMessageToClient("Unexpected message :: " + (msgFromClient.length() > 0 ? msgFromClient : "empty"));
				}

			}

			in.close();
			out.close();
			clientSocket.close();
		} catch (IOException e) {
			e.printStackTrace();
			Server.getClientsConected().remove(getIdClient());
		}
	}
	
	private void initializeBuffers(Socket clientSocket) throws IOException {
		out = new PrintWriter(clientSocket.getOutputStream(), true);
		in = new BufferedReader(new InputStreamReader(clientSocket.getInputStream()));
	}

	private void evaluateMessageAction(String msgFromClient) {

		String parts[] = msgFromClient.split(" ");

		Actions action = ActionsHelper.obtainAction(parts[1]);
		
		Message message;
		if (parts.length > 2) {
			message = new Message(parts[0], action, parts[2]);
		} else {
			message = new Message(parts[0], action);
		}

		if (!isAuthorized() && !ActionsHelper.isLoginAction(message)) {
			sendMessageToClient(notAuthorizedMessage(message));
			
		} else if (!isAuthorized() && ActionsHelper.isLoginAction(message)) {
			sendMessageToClient(new LoginProtocol().processAction(message, this));
			
		} else if (isAuthorized() && ActionsHelper.isLoginAction(message)) {
			setAuthorized(false);
			setUser(null);
			sendMessageToClient(new LoginProtocol().processAction(message, this));
			
		} else if (isAuthorized() && ActionsHelper.isFincaAction(message)) {
			sendMessageToClient(new FincaProtocol().processAction(message, this));
			
		} else if (isAuthorized() && ActionsHelper.isCultivoAction(message)) {
			sendMessageToClient(new FincaProtocol().processAction(message, this));
		}
		
	}

	public void sendMessageToClient(String message) {
		this.out.println(message);
	}

	public int getIdClient() {
		return idClient;
	}

	private String notAuthorizedMessage(Message message) {
		return "FAILED " + message.getId() + " No autorizado";
	}

	public boolean isAuthorized() {
		return authorized;
	}

	public void setAuthorized(boolean authorized) {
		this.authorized = authorized;
	}

	public String getUser() {
		return user;
	}

	public void setUser(String user) {
		this.user = user;
	}

	public int getPortAvailable() {
		int portAvailable = Server.PORT+1;
		try {
			for (int port : Server.getPorts()) {
				portAvailable = port;
				Socket checkSocket = new Socket(this.clientSocket.getInetAddress(), portAvailable);
				System.out.println("Socket: "+ port + " not available");
				checkSocket.close();
			}
		} catch (Exception e) {
			System.out.println("Port available: " + portAvailable);
		}
		
		return portAvailable;
	}

	public Socket getClientSocket() {
		return clientSocket;
	}
	
	public String msgOKwithIpAndPort(Message message, int port) {
		return "OK " + message.getId() + " " + getClientSocket().getLocalAddress().getHostAddress() + " " + port;
	}

	public String msgOK(String idMessage, String message) {
		return "OK " + idMessage + " " + message;
	}
}
