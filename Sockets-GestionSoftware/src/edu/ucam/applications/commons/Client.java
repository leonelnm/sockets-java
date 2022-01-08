package edu.ucam.applications.commons;

import java.io.Serializable;
import java.util.concurrent.atomic.AtomicInteger;

public class Client implements Serializable {

	private static final long serialVersionUID = 1L;
	
	private static final AtomicInteger counter = new AtomicInteger(0);
	private int id;
	private String name;
	private String lastname;
	
	public Client() {
		this.id = counter.incrementAndGet();
	}
	
	public Client(int id) {
		this.id = id;
	}
	
	public Client(String name, String lastname) {
		this();
		this.name = name;
		this.lastname = lastname;
	}
	
	public int getId() {
		return id;
	}

	public String getName() {
		return name;
	}
	
	public void setName(String name) {
		this.name = name;
	}
	
	public String getLastname() {
		return lastname;
	}
	
	public void setLastname(String lastname) {
		this.lastname = lastname;
	}

	@Override
	public String toString() {
		StringBuilder builder = new StringBuilder();
		builder.append("Client [id=");
		builder.append(id);
		builder.append(", name=");
		builder.append(name);
		builder.append(", lastname=");
		builder.append(lastname);
		builder.append("]");
		return builder.toString();
	}
	
	
	
}
