package edu.ucam.client;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.Socket;
import java.util.concurrent.atomic.AtomicBoolean;

import edu.ucam.applications.commons.Command;
import edu.ucam.applications.commons.Messages;
import edu.ucam.applications.commons.PortsUtils;

public class ClientKeepAliveThread extends Thread{
	
	private MainClient mainClient;
	private AtomicBoolean running = new AtomicBoolean(false);

	public ClientKeepAliveThread(MainClient mainClient) {
		this.mainClient = mainClient;
	}

	@Override
	public void run() {
		running.set(true);
		int attemps = 1;
		while (attemps <= 3) {
			try(Socket socket = new Socket("127.0.0.1", PortsUtils.KEEPALIVE_PORT)){
				BufferedReader br = new BufferedReader(new InputStreamReader(socket.getInputStream()));
				PrintWriter pw = new PrintWriter(socket.getOutputStream());
				
				this.mainClient.setKeepAliveThread(this);

				String readed = "";
				while (((readed = br.readLine()) != null) && isRunning()) {
					if(Command.KEEPALIVE.name().equals(readed)) {
						pw.println(Messages.OK);
						pw.flush();
					}
				}

				br.close();
				pw.close();
				break;
			}catch (IOException e) {
				System.err.println("Error en Hilo KeepAlive del CLIENTE " + e.getMessage() + ". Intento: " + attemps);
				try {
					Thread.sleep(200);
				} catch (InterruptedException exp) {
					System.err.println("Error at InterruptedException");
				    Thread.currentThread().interrupt();
				}
			}
			attemps++; 
		}

	}
	
	public void interruptKeepAlive() {
        running.set(false);
        this.interrupt();
    }
	boolean isRunning() {
        return running.get();
    }

}
