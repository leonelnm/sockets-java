package edu.ucam.client;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.PrintWriter;
import java.net.Socket;
import java.util.Scanner;
import java.util.StringJoiner;

import edu.ucam.applications.commons.Client;
import edu.ucam.applications.commons.Command;
import edu.ucam.applications.commons.CommandRequest;
import edu.ucam.applications.commons.Messages;
import edu.ucam.applications.commons.PortsUtils;
import edu.ucam.applications.commons.User;

public class MainClient {
	
	private static int msgCount = 0;
	
	private BufferedReader br;
	private PrintWriter pw;
	private ObjectInputStream ios;
	private ObjectOutputStream oos; 
	private User user;
	
	public void ejecutar(){
		
		try (Socket socket = new Socket("localhost", PortsUtils.COMMAND_PORT)){
			user = new User();
			
			this.br = new BufferedReader(new InputStreamReader(socket.getInputStream()));
			this.pw = new PrintWriter(socket.getOutputStream());
			
			Scanner sc = new Scanner(System.in);
			Socket dataChannel = null;
			String readed = "";
			String[] parts;
			Client client = null;
			
			Command command = null;
			while (!Command.EXIT.equals(command)) {
				if(user.isLogged()) {
					showMenu();
					command = selectMenu(sc);
				}else {
					showLogin();
					command = selectLogin(sc);
				}
				
				if(command != null) {
					switch (command) {
					case USER:
						readed = sendComand(new CommandRequest(Command.USER, "admin"));
						if(isOkResponse(readed)) {
							user.setName("admin");
							readed = sendComand(new CommandRequest(Command.PASS, "admin"));
							if(isOkResponse(readed)) {
								user.setPass("admin");
								System.out.println("Usuario loggeado correctammente!");
							}
						}
						
						break;
					case ADDCLIENTE:
						readed = sendComand(new CommandRequest(command));
						if(isOkResponse(Messages.PREOK, readed)) {
							dataChannel = getDataChannel(readed);
							if(dataChannel != null) {
								client = new Client("Erik", "Navarrete");
								// Crear cliente
								oos.writeObject(client);
								oos.flush();
								closeDataChannel(dataChannel);
								
								System.out.println(br.readLine());								
							}
						}
						break;

					case GETCLIENTE:
						System.out.println("Inserte id: ");
						readed = sc.nextLine();
						readed = sendComand(new CommandRequest(command, readed));
						
						if(isOkResponse(Messages.PREOK, readed)) {
							dataChannel = getDataChannel(readed);
							if(dataChannel != null) {
								client = (Client) ios.readObject();
								if(client != null) {
									System.out.println("Recibido: " + client.toString());
								}
								closeDataChannel(dataChannel);
								
								System.out.println(br.readLine());
							}
						}
						
						break;
					case COUNTCLIENTES:
						readed = sendComand(new CommandRequest(command));
						parts = readed.split(" ");
						System.out.println("Número total de clientes: " + parts[parts.length-1]);
						break;
					default:
						break;
					}
				}
			}
			
			sendComand(new CommandRequest(Command.EXIT));
			
			
		} catch (IOException e) {
			e.printStackTrace();
		} catch (ClassNotFoundException e) {
			System.err.println("Error al leer object " + e.getMessage());
			e.printStackTrace();
		}
		
	}
	
	
	private boolean isOkResponse(String readed) {
		boolean valid = false;
		if(!"".equals(readed)) {
			valid = readed.startsWith(Messages.OK);
		}
		
		return valid;
	}
	
	private boolean isOkResponse(String str, String readed) {
		boolean valid = false;
		if(!"".equals(readed)) {
			valid = readed.startsWith(str);
		}
		
		return valid;
	}

	private Command selectLogin(Scanner sc) {
		System.out.print("Seleccione opción: ");
		Command command = null;
		Integer selection = Integer.parseInt(sc.nextLine());
		
		switch (selection) {
		case 1:
			command = Command.USER;
			break;
		case 2:
			command = Command.EXIT;
			break;
		default:
			System.out.println("Opción no disponible");
			break;
		}
		
		return command;
	}


	private Command selectMenu(Scanner sc) {
		System.out.print("Seleccione opción: ");
		Command command = null;
		Integer selection = Integer.parseInt(sc.nextLine());
		
		switch (selection) {
		case 1:
			command = Command.ADDCLIENTE;
			break;
		case 2:
			command = Command.GETCLIENTE;
			break;
		case 3:
			command = Command.COUNTCLIENTES;
			break;
		case 4:
			command = Command.EXIT;
			break;

		default:
			System.out.println("Opción no disponible");
			break;
		}
		
		return command;
	}

	private void showLogin() {
		System.out.println("Bienvenido");
		System.out.println("1. Acceder");
		System.out.println("2. Salir");
	}
	
	private void showMenu() {
		System.out.println("\nGestion clientes");
		System.out.println("1. Añadir cliente");
		System.out.println("2. Ver Cliente");
		System.out.println("3. Total de Clientes");
		System.out.println("4. Salir");
	}
	
	private Socket getDataChannel(String readed) {
		
		int attemps = 3;
		int i = 0;
		
		while (i < attemps) {
			String[] partes = readed.split(" ");
			try {
				Socket socketData = new Socket(partes[3], Integer.parseInt(partes[4]));
				this.oos = new ObjectOutputStream(socketData.getOutputStream());
				this.ios = new ObjectInputStream(socketData.getInputStream());
				return socketData;
			} catch (NumberFormatException | IOException e) {
				System.err.println(e.getMessage());
			}
			
			i++;
		}
		
		System.out.println("No es posible conectarse!");
		return null;
	}
	
	private void closeDataChannel(Socket socketData) throws IOException {
		this.oos.close();
		this.ios.close();
		socketData.close();
	}


	private String sendComand(CommandRequest request) throws IOException{

		StringJoiner joiner = new StringJoiner(" ");
		joiner
			.add(getNexMessageNumber())
			.add(request.getCommand().name());
		
		if(request.getExtraData() != null && !"".equals(request.getExtraData())) {
			joiner.add(request.getExtraData());
		}
		
		System.out.println("OUT -> " + joiner.toString());
		pw.println(joiner.toString());
		pw.flush();
		
		String readed = br.readLine();
		System.out.println("IN  <- " + readed);
		return readed;
	}
	
	private static String getNexMessageNumber() {
		msgCount += 1;
		return Integer.toString(msgCount);
	}
	
	public User getUser() {
		return user;
	}

	public static void main(String[] args) {
		(new MainClient()).ejecutar();
	}

}
