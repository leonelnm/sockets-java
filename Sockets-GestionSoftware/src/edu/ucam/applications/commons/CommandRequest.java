package edu.ucam.applications.commons;

public class CommandRequest {
	
	private int number;
	private Command command;
	private String extraData;
	
	public CommandRequest() {
		// Empty constructor
	}
	
	public CommandRequest(Command command) {
		this.command = command;
	}
	
	public CommandRequest(Command command, String extraData) {
		this.command = command;
		this.extraData = extraData;
	}



	public int getNumber() {
		return number;
	}

	public void setNumber(int number) {
		this.number = number;
	}

	public Command getCommand() {
		return command;
	}

	public void setCommand(Command command) {
		this.command = command;
	}

	public String getExtraData() {
		return extraData;
	}

	public void setExtraData(String extraData) {
		this.extraData = extraData;
	}
	
}
