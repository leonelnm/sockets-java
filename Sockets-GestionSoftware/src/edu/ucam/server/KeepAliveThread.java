package edu.ucam.server;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.ServerSocket;
import java.net.Socket;
import java.time.LocalDateTime;
import java.util.Timer;
import java.util.TimerTask;

import edu.ucam.applications.commons.Command;
import edu.ucam.applications.commons.Messages;
import edu.ucam.applications.commons.PortsUtils;

public class KeepAliveThread extends Thread {
	
	private ServerThread serverThread;
	
	public KeepAliveThread(ServerThread serverThread) {
		this.serverThread = serverThread;
	}

	@Override
	public void run() {
		
		try(ServerSocket keepAliveServerSocket = new ServerSocket(PortsUtils.KEEPALIVE_PORT)){
			Socket keepAliveSocket = keepAliveServerSocket.accept();
			BufferedReader br = new BufferedReader(new InputStreamReader(keepAliveSocket.getInputStream()));
			PrintWriter pw = new PrintWriter(keepAliveSocket.getOutputStream());
			
			this.serverThread.setKeepAliveSocket(keepAliveSocket);
			
			int seconds = 30000; // 30s
			
			Timer timer = new Timer("KeepAlive");
			timer.schedule(new TimerTask() {
				String readed = "";
				
				@Override
				public void run() {
					try {
						pw.println(Command.KEEPALIVE.name());
						pw.flush();
						
						readed = br.readLine();
						System.out.println("KEEPALIVE("+ serverThread.getId() +") ->" + readed + " " + LocalDateTime.now().toString());
						if(!Messages.OK.equals(readed)) {
							Server.removeClient(serverThread);
							br.close();
							pw.close();
							keepAliveSocket.close();
							serverThread.closeAll();
							timer.cancel();
						}
					} catch (IOException e) {
						System.err.println("Error on Schedule " + e.getMessage());
					}
				}
			}, seconds, seconds);
			
		} catch (Exception e) {
			System.err.println("Error on KeepAliveThread " + e.getMessage());
		} 
		
	}
	
}
