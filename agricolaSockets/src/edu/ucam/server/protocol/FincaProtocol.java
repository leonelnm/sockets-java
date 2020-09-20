package edu.ucam.server.protocol;

import java.util.ArrayList;
import java.util.List;
import java.util.Map.Entry;

import edu.ucam.pojos.Finca;
import edu.ucam.server.ServerDataChannelThread;
import edu.ucam.server.ClientHandler;
import edu.ucam.server.Server;
import edu.ucam.server.message.Message;

public class FincaProtocol implements IProtocol{

	@Override
	public String processAction(Message message, ClientHandler client) {

		String resp = "";
		Finca finca = null;
		
		switch (message.getAction()) {
		case ADDFINCA:
			resp = openDataChannel(message, client, finca, NO_SEND, RECEIVE);
			break;

		case UPDATEFINCA:
			finca = Server.getFincas().get(Integer.parseInt(message.getData()));
			
			if(finca != null) {
				resp = openDataChannel(message, client, finca, NO_SEND, RECEIVE);
			}
			
			break;
		
		case GETFINCA:
			finca = Server.getFincas().get(Integer.parseInt(message.getData()));
			
			if(finca != null) {
				resp = openDataChannel(message, client, finca, SEND, NO_RECEIVE);
			}
			
			break;
			
		case REMOVEFINCA:
			Server.getFincas().remove(Integer.parseInt(message.getData()));
			resp = client.msgOK(message.getId(), "Finca eliminada");
			break;
			
		case LISTFINCAS:
			List<Finca> fincas = new ArrayList<Finca>();
			for (Entry<Integer, Finca> entry : Server.getFincas().entrySet()) {
				fincas.add(entry.getValue());
			}
			
			resp = openDataChannel(message, client, fincas, SEND, NO_RECEIVE);
			break;
			
		case COUNTFINCAS:
			resp = client.msgOK(message.getId(), Integer.toString(Server.getFincas().size()));
			break;
		default:
			break;
		}
		
		return resp;
	}


	private String openDataChannel(Message message, ClientHandler client, Finca finca, boolean send, boolean receive) {
		String resp;
		int portAvailable = client.getPortAvailable();
		new ServerDataChannelThread(portAvailable, finca, send, receive, message).start();			

		resp = client.msgOKwithIpAndPort(message, portAvailable);
		return resp;
	}
	
	private String openDataChannel(Message message, ClientHandler client, List<Finca> fincas, boolean send, boolean receive) {
		String resp;
		int portAvailable = client.getPortAvailable();
		new ServerDataChannelThread(portAvailable, fincas, send, receive, message).start();			

		resp = client.msgOKwithIpAndPort(message, portAvailable);
		return resp;
	}

}
