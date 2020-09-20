package edu.ucam.client;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.Socket;

public class ClientDataChannel {

	private Socket socket;
	private ObjectOutputStream channelToSendObject;
	private ObjectInputStream channelToReceiveObject;
	private Object object;
	
	public Object openDataChannelAndReceive(String resp) {
		String parts[] = resp.split(" ");
		try {
			socket = new Socket(parts[2], Integer.parseInt(parts[3]));
			channelToSendObject = new ObjectOutputStream(socket.getOutputStream());
			channelToReceiveObject = new ObjectInputStream(socket.getInputStream());
			
			setObject(receiveObjectFromClient());
			
			closeDataChannel();
			
		} catch (NumberFormatException | IOException e) {
			e.printStackTrace();
			System.out.println(e.getMessage());
		} catch (ClassNotFoundException e) {
			e.printStackTrace();
		}
		
		return getObject();
	}
	
	public Object openDataChannelAndSend(String resp, Object object) {
		String parts[] = resp.split(" ");
		try {
			socket = new Socket(parts[2], Integer.parseInt(parts[3]));
			channelToSendObject = new ObjectOutputStream(socket.getOutputStream());
			channelToReceiveObject = new ObjectInputStream(socket.getInputStream());
			
			sendObjectToClient(object);
			
			closeDataChannel();
			
		} catch (NumberFormatException | IOException e) {
			e.printStackTrace();
			System.out.println(e.getMessage());
		}
		
		return getObject();
	}

	private void sendObjectToClient(Object object) throws IOException {
		this.channelToSendObject.writeObject(object);
		this.channelToSendObject.flush();
	}
	
	private Object receiveObjectFromClient() throws ClassNotFoundException, IOException {
		return this.channelToReceiveObject.readObject();
	}
	
	private void closeDataChannel() throws IOException {
		this.channelToReceiveObject.close();
		this.channelToSendObject.close();
		this.socket.close();
	}
	
	public Object getObject() {
		return object;
	}

	public void setObject(Object object) {
		this.object = object;
	}


}
