package edu.ucam.server.protocol;

import java.io.PrintWriter;

import edu.ucam.applications.commons.CommandRequest;
import edu.ucam.applications.commons.User;
import edu.ucam.server.DataChannelThread;
import edu.ucam.server.ServerThread;

public interface IProtocolManagement {

	public void executeProtocol(User user, String readed, ServerThread commandChannel);
	
	public void loginManagement(CommandRequest request, PrintWriter pw, User user) throws Exception;
	public void management(CommandRequest request, ServerThread commandChannel) throws Exception;
	public void managementData(CommandRequest request, ServerThread commandChannel, DataChannelThread dataChannel) throws Exception;
	
	public CommandRequest createCommandRequest(String readed) throws Exception;
	public void sendMessage(String type, CommandRequest request, String cod, String extra, PrintWriter pw);
	public void sendMessage(String type, String number, String cod, String extra, PrintWriter pw);
	
	/**
	 * 
	 * @return port where connect
	 */
	public int openDataChannel(ServerThread commandChannel, CommandRequest request);
}
