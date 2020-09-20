package edu.ucam.server;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.ServerSocket;
import java.net.Socket;

import edu.ucam.actions.Actions;
import edu.ucam.actions.ActionsHelper;
import edu.ucam.server.message.Message;
import edu.ucam.server.services.CultivoService;
import edu.ucam.server.services.FincaService;

public class ServerDataChannelThread extends Thread{

	private ServerSocket serverSocket;
	private Socket socketData;
	private int port;
	private Object object;
	private ObjectOutputStream channelToSendObject;
	private ObjectInputStream channelToReceiveObject;
	private boolean send;
	private boolean receive;
	private Actions action;
	private Message message;
	
	public ServerDataChannelThread(int port, Object object, boolean send, boolean receive, Message message) {
		this.setPort(port);
		this.setObject(object);
		this.setSend(send);
		this.setReceive(receive);
		this.setAction(message.getAction());
		this.setMessage(message);
	}

	@Override
	public void run() {
					
		try {
			serverSocket = new ServerSocket(getPort());
			System.out.println("Server to DATA exchange, watching on: " + getPort());
			socketData = serverSocket.accept();
			
			initializeBuffers(socketData);
			
			if(isSend() && !isReceive()) {
				sendObjectToClient(getObject());
				
			} else if(!isSend() && isReceive()) {
				setObject(receiveObjectFromClient());
				
			} else if (isSend() && isReceive()) {
				sendObjectToClient(getObject());
				setObject(receiveObjectFromClient());
			}
			
			closeClientData();
			if(isReceive()) {
				processResponse();		
			}
						
		} catch (IOException e) {
			System.out.println(e.getMessage());
			e.printStackTrace();
		}
		
	}
	
	private void processResponse() {
		if(ActionsHelper.isFincaAction(getAction())) {
			new FincaService().process(getAction(), getObject(), null);
			
		} else if(ActionsHelper.isCultivoAction(getAction())) {
			new CultivoService().process(getAction(), getObject(), Server.getFincas().get(Integer.parseInt(message.getData2())));
		}
		
	}

	public void closeClientData() {
		try {
			channelToReceiveObject.close();
			channelToSendObject.close();
			socketData.close();
			serverSocket.close();
		} catch (IOException e) {
			System.err.println("No se ha podido cerrar serverSocket");
			e.printStackTrace();
		}
	}
	
	private void initializeBuffers(Socket clientSocket) throws IOException {
		channelToSendObject = new ObjectOutputStream(clientSocket.getOutputStream());
		channelToReceiveObject = new ObjectInputStream(clientSocket.getInputStream());
	}
	
	public void sendObjectToClient(Object object) throws IOException {
		this.getChannelToSendObject().writeObject(object);
		this.getChannelToSendObject().flush();
	}
	
	public Object receiveObjectFromClient() {
		Object object = null;
		try {
			object = this.getChannelToReceiveObject().readObject();
		} catch (ClassNotFoundException | IOException e) {
			e.printStackTrace();
		}
		return object;
	}

	public int getPort() {
		return port;
	}

	public void setPort(int port) {
		this.port = port;
	}

	public ObjectOutputStream getChannelToSendObject() {
		return channelToSendObject;
	}

	public ObjectInputStream getChannelToReceiveObject() {
		return channelToReceiveObject;
	}

	public Object getObject() {
		return object;
	}

	public void setObject(Object object) {
		this.object = object;
	}

	public boolean isSend() {
		return send;
	}

	public void setSend(boolean send) {
		this.send = send;
	}

	public boolean isReceive() {
		return receive;
	}

	public void setReceive(boolean receive) {
		this.receive = receive;
	}

	public Actions getAction() {
		return action;
	}

	public void setAction(Actions action) {
		this.action = action;
	}

	public Message getMessage() {
		return message;
	}

	public void setMessage(Message message) {
		this.message = message;
	}

}
