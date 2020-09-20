package edu.ucam.server.message;

import edu.ucam.actions.Actions;

public class Message {
	
	private String id;
	private Actions action;
	private String data;
	private String data2;
		
	public Message(String id, Actions action) {
		this.id = id;
		this.action = action;
	}
	
	public Message(String id, Actions action, String data) {
		this.id = id;
		this.action = action;
		this.data = data;
	}

	public Message(String id, Actions action, String data, String data2) {
		this.id = id;
		this.action = action;
		this.data = data;
		this.data2 = data2;
	}
	
	

	public String getId() {
		return id;
	}
	public void setId(String id) {
		this.id = id;
	}
	public Actions getAction() {
		return action;
	}
	public void setAction(Actions action) {
		this.action = action;
	}
	public String getData() {
		return data;
	}
	public void setData(String data) {
		this.data = data;
	}

	public String getData2() {
		return data2;
	}

	public void setData2(String data2) {
		this.data2 = data2;
	}
	
	
	

}
