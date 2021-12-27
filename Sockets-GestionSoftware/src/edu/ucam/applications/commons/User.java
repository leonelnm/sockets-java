package edu.ucam.applications.commons;

public class User {
	
	private String name;
	private String pass;
	
	public User() {
		this.setName("");
		this.setPass("");
	}
	
	public boolean isLogged() {
		return "admin".equals(this.name) && "admin".equals(this.pass);
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public String getPass() {
		return pass;
	}

	public void setPass(String pass) {
		this.pass = pass;
	}
	
	

}
