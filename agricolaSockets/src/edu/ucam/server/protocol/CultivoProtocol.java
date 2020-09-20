package edu.ucam.server.protocol;

import edu.ucam.server.ClientHandler;
import edu.ucam.server.message.Message;

public class CultivoProtocol implements IProtocol{

	@Override
	public String processAction(Message message, ClientHandler client) {

		String resp = "";
		
		switch (message.getAction()) {
		case ADDFINCA:
			break;

		case UPDATEFINCA:
			break;
		
		default:
			break;
		}
		
		return resp;
	}

}
