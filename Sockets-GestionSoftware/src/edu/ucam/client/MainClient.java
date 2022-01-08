package edu.ucam.client;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.PrintWriter;
import java.net.Socket;
import java.util.List;
import java.util.Scanner;
import java.util.StringJoiner;

import edu.ucam.applications.commons.Client;
import edu.ucam.applications.commons.Command;
import edu.ucam.applications.commons.CommandRequest;
import edu.ucam.applications.commons.Messages;
import edu.ucam.applications.commons.PortsUtils;
import edu.ucam.applications.commons.Product;
import edu.ucam.applications.commons.User;

public class MainClient {
	
	private static int msgCount = 0;
	
	private BufferedReader br;
	private PrintWriter pw;
	private ObjectInputStream ios;
	private ObjectOutputStream oos; 
	private User user;
	private boolean menuClient = false;
	private boolean menuProduct = false;
	
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
			List<Client> clientList = null;
			Integer idTemp = null;
			Product product = null;
			List<Product> productList = null;
			
			Command command = null;
			while (!Command.EXIT.equals(command)) {
				if(user.isLogged()) {
					if(menuClient || menuProduct) {
						if(menuClient) {
							showMenuClient();
							command = selectMenuClient(sc);
						}
						if(menuProduct) {
							showMenuProduct();
							command = selectMenuProduct(sc);
						}
					}else {
						showMenuManagement();
						command = selectMenuManagement(sc);
					}
				}else {
					showMenuLogin();
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
						System.out.println("\nAñadir Cliente");
						readed = sendComand(new CommandRequest(command));
						if(isOkResponse(Messages.PREOK, readed)) {
							dataChannel = getDataChannel(readed);
							if(dataChannel != null) {
								client = new Client();
								System.out.println("Inserte Nombre: ");
								readed = sc.nextLine();
								client.setName(readed);
								System.out.println("Inserte Apellido: ");
								readed = sc.nextLine();
								client.setLastname(readed);
								// Enviar/Crear cliente
								oos.writeObject(client);
								oos.flush();
								closeDataChannel(dataChannel);
								
							}
							readFinalTransactionMessage();
						}else {
							System.out.println("Volver a intentar");
						}
						break;
						
					case ADDPRODUCTO:
						System.out.println("\nAñadir Producto");
						readed = sendComand(new CommandRequest(command));
						if(isOkResponse(Messages.PREOK, readed)) {
							dataChannel = getDataChannel(readed);
							if(dataChannel != null) {
								product = new Product();
								System.out.println("Inserte Nombre: ");
								readed = sc.nextLine();
								product.setName(readed);
								System.out.println("Inserte Precio: ");
								readed = sc.nextLine();
								product.setPrice(Double.parseDouble(readed));
								oos.writeObject(product);
								oos.flush();
								closeDataChannel(dataChannel);
								
							}
							readFinalTransactionMessage();
						}else {
							System.out.println("Volver a intentar");
						}
						break;

					case GETCLIENTE:
						System.out.println("\nVer Cliente");
						System.out.println("Inserte ID de cliente: ");
						readed = sc.nextLine();
						readed = sendComand(new CommandRequest(command, readed));
						
						if(isOkResponse(Messages.PREOK, readed)) {
							dataChannel = getDataChannel(readed);
							if(dataChannel != null) {
								client = (Client) ios.readObject();
								if(client != null) {
									System.out.println(client.toString());
								}else {
									System.out.println("Cliente no encontrado!");
								}
								closeDataChannel(dataChannel);
							}
							readFinalTransactionMessage();
						}else {
							System.out.println("Volver a intentar");
						}
						
						break;
						
					case GETPRODUCTO:
						System.out.println("\nVer Producto");
						System.out.println("Inserte ID de producto: ");
						readed = sc.nextLine();
						readed = sendComand(new CommandRequest(command, readed));
						
						if(isOkResponse(Messages.PREOK, readed)) {
							dataChannel = getDataChannel(readed);
							if(dataChannel != null) {
								product = (Product) ios.readObject();
								if(product != null) {
									System.out.println(product.toString());
								}else {
									System.out.println("Producto no encontrado!");
								}
								closeDataChannel(dataChannel);
							}
							readFinalTransactionMessage();
						}else {
							System.out.println("Volver a intentar");
						}
						
						break;
						
					case UPDATECLIENTE:
						System.out.println("\nActualizar Cliente");
						System.out.println("Inserte ID de cliente para actualizar: ");
						readed = sc.nextLine();
						idTemp = Integer.parseInt(readed);
						readed = sendComand(new CommandRequest(command, readed));
						if(isOkResponse(Messages.PREOK, readed)) {
							dataChannel = getDataChannel(readed);
							if(dataChannel != null) {
								client = new Client(idTemp);
								System.out.println("Inserte Nombre: ");
								readed = sc.nextLine();
								client.setName(readed);
								System.out.println("Inserte Apellido: ");
								readed = sc.nextLine();
								client.setLastname(readed);
								oos.writeObject(client);
								oos.flush();
								closeDataChannel(dataChannel);
							}
							readFinalTransactionMessage();
						}else {
							System.out.println("Volver a intentar");
						}
						break;
						
					case UPDATEPRODUCTO:
						System.out.println("\nActualizar Productp");
						System.out.println("Inserte ID de producto para actualizar: ");
						readed = sc.nextLine();
						idTemp = Integer.parseInt(readed);
						readed = sendComand(new CommandRequest(command, readed));
						if(isOkResponse(Messages.PREOK, readed)) {
							dataChannel = getDataChannel(readed);
							if(dataChannel != null) {
								product = new Product(idTemp);
								System.out.println("Inserte Nombre: ");
								readed = sc.nextLine();
								product.setName(readed);
								System.out.println("Inserte Precio: ");
								readed = sc.nextLine();
								product.setPrice(Double.parseDouble(readed));
								oos.writeObject(product);
								oos.flush();
								closeDataChannel(dataChannel);
							}
							readFinalTransactionMessage();
						}else {
							System.out.println("Volver a intentar");
						}
						break;
						
					case REMOVECLIENTE:
						System.out.println("\nEliminar cliente");
						System.out.println("Inserte ID de cliente a eliminar: ");
						readed = sc.nextLine();
						readed = sendComand(new CommandRequest(command, readed));
						if(isOkResponse(readed)) {
							System.out.println("Cliente eliminado");
						}else {
							System.out.println("Cliente no encontrado");
						}
						break;
						
					case REMOVEPRODUCTO:
						System.out.println("\nEliminar producto");
						System.out.println("Inserte ID de producto a eliminar: ");
						readed = sc.nextLine();
						readed = sendComand(new CommandRequest(command, readed));
						if(isOkResponse(readed)) {
							System.out.println("Producto eliminado");
						}else {
							System.out.println("Producto no encontrado");
						}
						break;
						
					case COUNTCLIENTES:
						readed = sendComand(new CommandRequest(command));
						parts = readed.split(" ");
						System.out.println("Número total de clientes: " + parts[parts.length-1]);
						break;
						
					case COUNTPRODUCTOS:
						readed = sendComand(new CommandRequest(command));
						parts = readed.split(" ");
						System.out.println("Número total de productos: " + parts[parts.length-1]);
						break;
						
					case LISTCLIENTES:
						System.out.println("\nListar clientes");
						readed = sendComand(new CommandRequest(command));
						if(isOkResponse(Messages.PREOK, readed)) {
							dataChannel = getDataChannel(readed);
							if(dataChannel != null) {
								clientList = (List<Client>) ios.readObject();
								if(clientList.isEmpty()) {
									System.out.println("\tNo existen clientes registrados!!!");									
								}else {
									clientList.stream().forEach(elem -> System.out.println("\t" + elem.toString()));
								}
								closeDataChannel(dataChannel);
							}
							readFinalTransactionMessage();
						}else {
							System.out.println("Volver a intentar");
						}
						break;
						
					case LISTPRODUCTOS:
						System.out.println("\nListar productos");
						readed = sendComand(new CommandRequest(command));
						if(isOkResponse(Messages.PREOK, readed)) {
							dataChannel = getDataChannel(readed);
							if(dataChannel != null) {
								productList = (List<Product>) ios.readObject();
								if(productList.isEmpty()) {
									System.out.println("\tNo existen productos registrados!!!");
								}else {
									productList.stream().forEach(elem -> System.out.println("\t" + elem.toString()));									
								}
								closeDataChannel(dataChannel);
							}
							readFinalTransactionMessage();
						}else {
							System.out.println("Volver a intentar");
						}
						break;
					default:
						System.out.println("No implementado");
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
	
	private void readFinalTransactionMessage() throws IOException {
		System.out.println("IN  <- " + this.br.readLine());	
		
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
			System.out.println("\nOpción no disponible!!!");
			break;
		}
		
		return command;
	}
	
	private Command selectMenuManagement(Scanner sc) {
		System.out.println("Seleccione opción: ");
		Integer selection = Integer.parseInt(sc.nextLine());
		
		switch (selection) {
		case 1:
			this.menuClient = true;
			this.menuProduct = false;
			break;
		case 2:
			this.menuProduct = true;
			this.menuClient = false;
			break;
		case 0:
			return Command.EXIT;
		default:
			System.out.println("\nOpción no disponible!!!");
			break;
		}
		
		return null;
	}

	private Command selectMenuProduct(Scanner sc) {
		System.out.print("Seleccione opción: ");
		Command command = null;
		Integer selection = Integer.parseInt(sc.nextLine());
		
		switch (selection) {
		case 1:
			command = Command.ADDPRODUCTO;
			break;
		case 2:
			command = Command.GETPRODUCTO;
			break;
		case 3:
			command = Command.UPDATEPRODUCTO;
			break;
		case 4:
			command = Command.REMOVEPRODUCTO;
			break;
		case 5:
			command = Command.COUNTPRODUCTOS;
			break;
		case 6:
			command = Command.LISTPRODUCTOS;
			break;
		case 0:
			command = Command.EXIT;
			break;
		case 7:
			this.menuClient = false;
			this.menuProduct = false;
			break;

		default:
			System.out.println("Opción no disponible");
			break;
		}
		
		return command;
	}

	private Command selectMenuClient(Scanner sc) {
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
			command = Command.UPDATECLIENTE;
			break;
		case 4:
			command = Command.REMOVECLIENTE;
			break;
		case 5:
			command = Command.COUNTCLIENTES;
			break;
		case 6:
			command = Command.LISTCLIENTES;
			break;
		case 0:
			command = Command.EXIT;
			break;
		case 7:
			this.menuClient = false;
			this.menuProduct = false;
			break;

		default:
			System.out.println("Opción no disponible");
			break;
		}
		
		return command;
	}

	private void showMenuLogin() {
		System.out.println("Bienvenido");
		System.out.println("1. Acceder");
		System.out.println("2. Salir");
	}
	
	private void showMenuManagement() {
		System.out.println("\n\n1. Gestionar clientes");
		System.out.println("2. Gestionar productos");
		System.out.println("0. Salir");
	}
	
	private void showMenuClient() {
		System.out.println("\n\n\t*** Gestión CLIENTES ***");
		System.out.println("1. Añadir cliente");
		System.out.println("2. Ver Cliente");
		System.out.println("3. Actualizar cliente");
		System.out.println("4. Eliminar cliente");
		System.out.println("5. Ver Total de Clientes");
		System.out.println("6. Mostrar todos los clientes");
		System.out.println("7. Volver");
		System.out.println("0. Salir");
	}
	
	private void showMenuProduct() {
		System.out.println("\n\n\t*** Gestión PRODUCTOS ***");
		System.out.println("1. Añadir Producto");
		System.out.println("2. Ver Producto");
		System.out.println("3. Actualizar Producto");
		System.out.println("4. Eliminar Producto");
		System.out.println("5. Ver Total de Producto");
		System.out.println("6. Mostrar todos los Productos");
		System.out.println("7. Volver");
		System.out.println("0. Salir");
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
