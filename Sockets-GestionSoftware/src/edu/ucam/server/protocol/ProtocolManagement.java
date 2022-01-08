package edu.ucam.server.protocol;

import java.io.PrintWriter;
import java.util.StringJoiner;

import edu.ucam.applications.commons.Client;
import edu.ucam.applications.commons.Command;
import edu.ucam.applications.commons.CommandRequest;
import edu.ucam.applications.commons.Messages;
import edu.ucam.applications.commons.PortsUtils;
import edu.ucam.applications.commons.Product;
import edu.ucam.applications.commons.User;
import edu.ucam.server.DataChannelThread;
import edu.ucam.server.ServerThread;
import edu.ucam.server.repository.ClientRepository;
import edu.ucam.server.repository.IRepository;
import edu.ucam.server.repository.ProductRepository;

public class ProtocolManagement implements IProtocolManagement {

	private IRepository<Client> clientRepo = new ClientRepository();
	private IRepository<Product> productRepo = new ProductRepository();

	@Override
	public void executeProtocol(User user, String readed, ServerThread commandChannel) {
		CommandRequest request = null;
		try {
			request = createCommandRequest(readed);

			if (user.isLogged()) {
				management(request, commandChannel);
			} else {
				loginManagement(request, commandChannel.getPw(), user);
			}

		} catch (Exception e) {
			String msg = e.getMessage();
			if(msg != null && !"".equals(msg)) {
				
				if (msg.startsWith(Messages.COD_NOT_IMPLEMENTED)) {
					sendMessage(Messages.FAILED, readed, Messages.COD_NOT_IMPLEMENTED, "Comando no implementado", commandChannel.getPw());
				} else if (msg.startsWith(Messages.COD_UNAUTHORIZED)) {
					sendMessage(Messages.FAILED, readed, Messages.COD_UNAUTHORIZED, "No autorizado", commandChannel.getPw());
				} else if (msg.startsWith(Messages.BAD_FORMAT)) {
					sendMessage(Messages.FAILED, readed, Messages.COD_BAD_REQUEST, "Formato inesperado", commandChannel.getPw());
				} else {
					sendMessage(Messages.FAILED, readed, Messages.COD_INTERNAL_SERVER_ERROR, "Error inesperado", commandChannel.getPw());
				}
				
			}

			System.out.println("ProtocolManagement.executeProtocol()" + e.getMessage());
		}

	}

	@Override
	public void loginManagement(CommandRequest request, PrintWriter pw, User user) throws Exception {
		switch (request.getCommand()) {
		case USER:
			if ("admin".equals(request.getExtraData())) {
				user.setName(request.getExtraData());
				sendMessage(Messages.OK, request, Messages.COD_OK, "Envíe contraseña", pw);

			} else {
				user.setName("");
				sendMessage(Messages.FAILED, request, Messages.COD_NOT_FOUND, "Not user", pw);
			}
			break;

		case PASS:
			if ("admin".equals(request.getExtraData())) {
				user.setPass(request.getExtraData());
				sendMessage(Messages.OK, request, Messages.COD_OK, "Welcome " + user.getName(), pw);
			} else {
				user.setPass("");
				sendMessage(Messages.FAILED, request, Messages.COD_NOT_FOUND, "Prueba de nuevo", pw);
			}
			break;
		default:
			throw new RuntimeException(Messages.NOT_DEFINED);
		}

	}

	@Override
	public void management(CommandRequest request, ServerThread commandChannel) throws Exception {

		Integer port = null;
		
		switch (request.getCommand()) {
		case ADDCLIENTE:
		case LISTCLIENTES:
		case ADDPRODUCTO:
		case LISTPRODUCTOS:
		case UPDATECLIENTE:
		case UPDATEPRODUCTO:
		case GETCLIENTE:
		case GETPRODUCTO:
			port = openDataChannel(commandChannel, request);
			sendPreOK(request, commandChannel, port);
			break;
			
		case REMOVECLIENTE:
			if(clientRepo.remove(Integer.parseInt(request.getExtraData())) != null) {
				sendMessage(Messages.OK, request, Messages.COD_OK, "Client removed!", commandChannel.getPw());
			}else {
				sendMessage(Messages.FAILED, request, Messages.COD_NOT_FOUND, "Client Id dont found", commandChannel.getPw());
			}
			break;
			
		case REMOVEPRODUCTO:
			if(productRepo.remove(Integer.parseInt(request.getExtraData())) != null) {
				sendMessage(Messages.OK, request, Messages.COD_OK, "Product removed!", commandChannel.getPw());
			}else {
				sendMessage(Messages.FAILED, request, Messages.COD_NOT_FOUND, "Product Id dont found", commandChannel.getPw());
			}
			break;
			
		case COUNTCLIENTES:
			sendMessage(Messages.OK, request, Messages.COD_OK, Integer.toString(clientRepo.size()), commandChannel.getPw());
			break;
			
		case COUNTPRODUCTOS:
			sendMessage(Messages.OK, request, Messages.COD_OK, Integer.toString(productRepo.size()), commandChannel.getPw());
			break;
			
		default:
			if (commandChannel.getUser().isLogged()) {
				throw new RuntimeException(Messages.COD_NOT_IMPLEMENTED);
			} else {
				throw new RuntimeException(Messages.COD_UNAUTHORIZED);
			}
		}

	}
	
	@Override
	public void managementData(CommandRequest request, ServerThread commandChannel, DataChannelThread dataChannel) throws Exception {
		Client client = null;
		Product product = null;
		String msg = "";
		switch (request.getCommand()) {
		case ADDCLIENTE:
			client = (Client) dataChannel.readObject();
			if(client == null) {
				sendInvalidData(request, commandChannel);
			}else if(clientRepo.existById(client.getId())) {
				sendMessage(Messages.FAILED, request, Messages.COD_DUPLICATED, "Client duplicated", commandChannel.getPw());
			}else {
				clientRepo.save(client);					
				sendTransferOK(request, commandChannel);
			}
			break;
			
			
		case ADDPRODUCTO:
			product = (Product) dataChannel.readObject();
			if(product == null) {
				sendInvalidData(request, commandChannel);
			}else if(productRepo.existById(product.getId())) {
				sendMessage(Messages.FAILED, request, Messages.COD_DUPLICATED, "Product duplicated", commandChannel.getPw());
			}else {
				productRepo.save(product);					
				sendTransferOK(request, commandChannel);
			}
			break;
			
			
		case UPDATECLIENTE:
			client = (Client) dataChannel.readObject();
			if(client == null) {
				sendInvalidData(request, commandChannel);
			}else if(Integer.parseInt(request.getExtraData()) != client.getId()){
				msg = "Expected client id: " + request.getExtraData() + " received id: " + client.getId();
				sendMessage(Messages.FAILED, request, Messages.COD_UNEXPECTED, msg, commandChannel.getPw());
			}else if(!clientRepo.existById(client.getId())) {
				msg = "Client " + client.getId() + " dont found";
				sendMessage(Messages.FAILED, request, Messages.COD_NOT_FOUND, msg, commandChannel.getPw());
			} else {
				clientRepo.update(Integer.parseInt(request.getExtraData()), client);
				sendTransferOK(request, commandChannel);
			}
				
			break;
			
		case UPDATEPRODUCTO:
			product = (Product) dataChannel.readObject();
			if(product == null) {
				sendInvalidData(request, commandChannel);
			}else if(Integer.parseInt(request.getExtraData()) != product.getId()){
				msg = "Expected product id: " + request.getExtraData() + " received id: " + product.getId();
				sendMessage(Messages.FAILED, request, Messages.COD_UNEXPECTED, msg, commandChannel.getPw());
			}else if(!productRepo.existById(product.getId())) {
				msg = "Product " + product.getId() + " dont found";
				sendMessage(Messages.FAILED, request, Messages.COD_NOT_FOUND, msg, commandChannel.getPw());
			} else {
				productRepo.update(Integer.parseInt(request.getExtraData()), product);
				sendTransferOK(request, commandChannel);
			}
				
			break;
			
		case GETCLIENTE:
			dataChannel.sendObject(clientRepo.findById(Integer.parseInt(request.getExtraData())));
			sendTransferOK(request, commandChannel);
			break;
			
		case GETPRODUCTO:
			dataChannel.sendObject(productRepo.findById(Integer.parseInt(request.getExtraData())));
			sendTransferOK(request, commandChannel);
			break;
			
		case LISTCLIENTES:
			dataChannel.sendObject(clientRepo.getAll());
			sendTransferOK(request, commandChannel);
			break;
			
		case LISTPRODUCTOS:
			dataChannel.sendObject(productRepo.getAll());
			sendTransferOK(request, commandChannel);
			break;

		default:
			throw new RuntimeException(Messages.COD_UNAUTHORIZED);
		}
		
	}

	@Override
	public CommandRequest createCommandRequest(String readed) throws Exception {
		CommandRequest request = new CommandRequest();

		String[] partes;

		System.out.println("Readed: " + readed);

		partes = readed.split(" ");

		if (partes.length > 3 || partes.length < 2) {
			throw new RuntimeException(Messages.COD_BAD_REQUEST);
		}

		try {
			request.setNumber(Integer.parseInt(partes[0]));
		} catch (NumberFormatException e) {
			throw new RuntimeException(e.getMessage());
		}

		Command command = Command.valueOf(partes[1].toUpperCase());
		if (command == null) {
			throw new RuntimeException(Messages.COD_NOT_IMPLEMENTED);
		}

		request.setCommand(command);
		if (partes.length > 2) {
			request.setExtraData(partes[2]);
		}

		return request;
	}
	
	@Override
	public synchronized int openDataChannel(ServerThread commandChannel, CommandRequest request) {
		int port = PortsUtils.getAvailableDataPort();
		DataChannelThread dataChannel = new DataChannelThread(port, commandChannel, request);
		dataChannel.start();
		try {
			Thread.sleep(200);
		} catch (InterruptedException e) {
			System.err.println("Error at InterruptedException");
		}
		return port;
	}

	@Override
	public void sendMessage(String type, CommandRequest request, String cod, String extra, PrintWriter pw) {

		StringJoiner joiner = new StringJoiner(" ");
		joiner
			.add(type)
			.add(Integer.toString(request.getNumber()))
			.add(cod)
			.add(extra);

		pw.println(joiner.toString());
		pw.flush();
	}

	@Override
	public void sendMessage(String type, String readed, String cod, String extra, PrintWriter pw) {

		String[] partes = readed.split(" ");
		
		StringJoiner joiner = new StringJoiner(" ");
		joiner
			.add(type)
			.add(partes.length > 1 ? partes[0] : readed)
			.add(cod)
			.add(extra);

		pw.println(joiner.toString());
		pw.flush();
		
		System.out.println("out: " + joiner.toString());

	}
	
	private void sendPreOK(CommandRequest request, ServerThread commandChannel, int port) {

		String ip = commandChannel.getSocket().getInetAddress().toString().substring(1);
		
		StringJoiner joiner = new StringJoiner(" ");
		joiner
			.add(Messages.PREOK)
			.add(Integer.toString(request.getNumber()))
			.add(Messages.COD_ACCEPTED)
			.add(ip)
			.add(Integer.toString(port));
		
		commandChannel.getPw().println(joiner.toString());
		commandChannel.getPw().flush();
		
		System.out.println("out: " + joiner.toString());
	}
	
	private void sendTransferOK(CommandRequest request, ServerThread commandChannel) {
		StringJoiner joiner = new StringJoiner(" ");
		joiner
			.add(Messages.OK)
			.add(Integer.toString(request.getNumber()))
			.add(Messages.COD_OK)
			.add(Messages.TRANSFER);
		
		commandChannel.getPw().println(joiner.toString());
		commandChannel.getPw().flush();
	}
	
	private void sendInvalidData(CommandRequest request, ServerThread commandChannel) {
		StringJoiner joiner = new StringJoiner(" ");
		joiner
			.add(Messages.FAILED)
			.add(Integer.toString(request.getNumber()))
			.add(Messages.COD_UNEXPECTED)
			.add("Invalid Data");
		
		commandChannel.getPw().println(joiner.toString());
		commandChannel.getPw().flush();
	}

}
