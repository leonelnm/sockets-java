package edu.ucam.server.protocol;

import edu.ucam.server.ClientHandler;
import edu.ucam.server.message.Message;

public interface IProtocol {
	
	public final boolean SEND = true;
	public final boolean NO_SEND = false;
	public final boolean RECEIVE = true;
	public final boolean NO_RECEIVE = false;
	
	public String processAction(Message message, ClientHandler client);

}
