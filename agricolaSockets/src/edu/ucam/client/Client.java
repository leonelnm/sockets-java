package edu.ucam.client;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.Socket;
import java.util.List;
import java.util.Scanner;

import edu.ucam.actions.Actions;
import edu.ucam.actions.FincaActions;
import edu.ucam.pojos.Finca;

public class Client {

	private Socket clientSocket;
	private PrintWriter out;
	private BufferedReader in;
	private int numberOfMessage;
	private boolean logged;
	private String user;
	
	public Client() {
		this.numberOfMessage = 1;
		this.setLogged(true); // TODO change to false
	}

	public void startConnection(String ip, int port) {
		try {
			clientSocket = new Socket(ip, port);
			out = new PrintWriter(clientSocket.getOutputStream(), true);
			in = new BufferedReader(new InputStreamReader(clientSocket.getInputStream()));

			Scanner sc = new Scanner(System.in);
			try {
				while (true) {

					showMenu(sc);
					break;
					
				}
			} finally {
				sc.close();
			}

		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	private void showMenu(Scanner scanner) {
		String opc = "";

		while(!opc.equals("10")) {
			
			if(isLogged()) {
				menuWithoutLogin();
			}else {
				menuWithLogin();
			}
			
			System.out.print("\nInserta una opción: ");

			opc = scanner.nextLine();
			String msg, resp;

			switch (opc) {

			case "1":
				if(!isLogged()) {
					msg = Actions.USER.name() + " ";
					System.out.print("Introduce usuario: ");
					setUser(scanner.nextLine().trim());
					msg += getUser();
					
					resp = sendMessageAndReceiveResponse(msg);
					
					if(evaluateResponseOK(resp)) {
						msg = Actions.PASS.name() + " ";
						System.out.print("Introduce contraseña: ");
						msg += scanner.nextLine();
						resp = sendMessageAndReceiveResponse(msg);
						if(evaluateResponseOK(resp)) {
							setLogged(true);
						}
					} 
				} else {
					System.out.println("Ya ha iniciado sesión como: " + getUser());
				}
				break;
			case "2":
				
				msg = Actions.ADDFINCA.name();
				
				resp = sendMessageAndReceiveResponse(msg);
				if (evaluateResponseOK(resp)) {
					Finca finca = new Finca();

					System.out.print("Introduce id de Finca: ");
					finca.setId(Integer.parseInt(scanner.nextLine()));
					System.out.print("Introduce nombre de Finca: ");
					finca.setName(scanner.nextLine());
					System.out.print("Introduce las hectáreas de la Finca: ");
					finca.setHectareas(Integer.parseInt(scanner.nextLine()));
					
					new ClientDataChannel().openDataChannelAndSend(resp, finca);
				}
				
				break;
			case "4":
				msg = FincaActions.REMOVEFINCA + " ";
				System.out.print("Inserta el ID de la Finca a borrar: ");
				msg += scanner.nextLine();
				
				sendMessageAndReceiveResponse(FincaActions.REMOVEFINCA.name());
				
			case "5":
				msg = FincaActions.GETFINCA.name() + " ";
				System.out.print("Inserta el ID de la Finca a buscar: ");
				msg += scanner.nextLine();
				
				resp = sendMessageAndReceiveResponse(msg);
				
				if(evaluateResponseOK(resp)) {
					Finca finca = (Finca) new ClientDataChannel().openDataChannelAndReceive(resp);
					System.out.println(finca.getId() + "\t" + finca.getName() + "\thectareas: " + finca.getHectareas() + "\tcultivos: " + finca.getCultivos().size());
				}
				
				break;
			case "6":
				if(isLogged()) {
					List<Finca> fincas = null;
					
					resp = sendMessageAndReceiveResponse(FincaActions.LISTFINCAS.name());
					if(evaluateResponseOK(resp)) {
						fincas = receiveListFincas(resp);
					}
					if(fincas != null) {
						System.out.println("LISTADO DE FINCAS");
						for (Finca finca : fincas) {
							System.out.println(finca.getId() + "\t" + finca.getName() + "\thectareas: " + finca.getHectareas() + "\tcultivos: " + finca.getCultivos().size());
						}						
					}
				}
				break;
			case "7":
				if(isLogged()) {
					resp = sendMessageAndReceiveResponse(FincaActions.COUNTFINCAS.name());
					System.out.println("Registradas: " + resp.split(" ")[2]);
				}
				break;
			case "9":
				setLogged(false);
				setUser(null);
				System.out.println("Ha cerrado sesión!");
				break;
				
			case "10":
				sendMessageAndReceiveResponse(Actions.EXIT.name());
				break;
			default:
				break;
			}
		}
		
		stopConnection();

	}
	
	@SuppressWarnings("unchecked")
	private List<Finca> receiveListFincas(String resp) {
		return (List<Finca>) new ClientDataChannel().openDataChannelAndReceive(resp); 
	}
	
	private void menuWithLogin() {
		System.out.println("\n1.- LOGIN");
		System.out.println("10.- Salir");
	}
	
	private void menuWithoutLogin() {
		System.out.println("\n2.- Añadir Finca");
		System.out.println("3.- Actualizar Finca");
		System.out.println("4.- Borrar Finca");
		System.out.println("5.- Ver Finca");
		System.out.println("6.- Listar todas las Fincas registradas");
		System.out.println("7.- Consultar cuántas Fincas hay registradas");
		System.out.println("9.- Cerrar sesión");
		System.out.println("10.- Salir");
	}
	
	private boolean evaluateResponseOK(String response) {
		return "OK".equals(response.split(" ")[0]);
	}

	public String sendMessageAndReceiveResponse(String msg) {
		out.println(getNumberOfMessage() + " " + msg);
		String resp = "";
		try {
			resp = in.readLine();
		} catch (IOException e) {
			e.printStackTrace();
		}

		nextNumberOfMessage();
		System.out.println(resp.length() > 0 ? resp : "empty");
		return resp;
	}

	public void stopConnection() {
		try {
			in.close();
			out.close();
			clientSocket.close();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	public int getNumberOfMessage() {
		return numberOfMessage;
	}

	public int nextNumberOfMessage() {
		return this.numberOfMessage++;
	}

	public boolean isLogged() {
		return logged;
	}

	public void setLogged(boolean logged) {
		this.logged = logged;
	}

	public String getUser() {
		return user;
	}

	public void setUser(String user) {
		this.user = user;
	}

}
