package edu.ucam.client;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.ObjectOutputStream;
import java.io.PrintWriter;
import java.net.Socket;

import edu.ucam.applications.commons.Client;
import edu.ucam.applications.commons.PortsUtils;

public class DummyClient {
	
	private BufferedReader br;
	private PrintWriter pw;
	
	public void ejecutar(){
		
		try(Socket socket = new Socket("localhost", PortsUtils.COMMAND_PORT)) {
			
			this.br = new BufferedReader(new InputStreamReader(socket.getInputStream()));
			this.pw = new PrintWriter(socket.getOutputStream());
			
			this.enviarComando("1 ADDCLIENTE");

			String leido = br.readLine();
			String[] partes = leido.split(" ");
			
//			DummyClientData data = new DummyClientData(partes);
//			data.start();
			
			Socket socketData = new Socket(partes[3], Integer.parseInt(partes[4]));
			Client client = new Client("Erik", "Navarrete");

			ObjectOutputStream oos = new ObjectOutputStream(socketData.getOutputStream());
			oos.writeObject(client);
			oos.flush();
			
			oos.close();
			socketData.close();
			
			
			br.close();
			pw.close();
			
		} catch (IOException e) {
			e.printStackTrace();
		}
		
	}
	
	public void enviarComando(String comando) throws IOException{
		System.out.println(comando);
		pw.println(comando);
		pw.flush();
		
	}

	public static void main(String[] args) {
		(new DummyClient()).ejecutar();

	}

}
