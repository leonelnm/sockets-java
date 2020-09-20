package edu.ucam.server.protocol;

import edu.ucam.server.ClientHandler;
import edu.ucam.server.Server;
import edu.ucam.server.message.Message;

public class LoginProtocol implements IProtocol{


	@Override
	public String processAction(Message message, ClientHandler client) {

		String resp = "";
		
		switch (message.getAction()) {
		case USER:
			if(client.getUser() == null && getUser(message.getData())){
				client.setUser(message.getData());
				resp = "OK " + message.getId() + " Envíe contraseña."; 
			} else if (client.getUser() != null && getUser(message.getData())){
				client.setUser(message.getData());
				resp = "OK " + message.getId() + " Envíe contraseña."; 
			} else {
				client.setAuthorized(false);
				client.setUser(null);
				resp = "FAILED " + message.getId() + " Not user.";
			}
			break;

		case PASS:
			if(client.getUser() != null && checkPassword(client.getUser(), message.getData())){
				client.setAuthorized(true);
				resp = "OK " + message.getId() + " Welcome " + client.getUser(); 
			} else {
				resp = "FAILED " + message.getId() + " Prueba de nuevo";
			}
			break;
		default:
			break;
		}
		
		return resp;
	}
	
	private boolean getUser(String userString){
		return Server.getUsers().containsKey(userString);
	}
	
	private boolean checkPassword(String userString, String password) {
		
		if(getUser(userString)) {
			if(Server.getUsers().get(userString).equals(password)) {
				return true;
			}
			
		}
		
		return false;
	}

}
