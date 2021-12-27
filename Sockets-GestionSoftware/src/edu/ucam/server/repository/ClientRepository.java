package edu.ucam.server.repository;

import java.util.List;
import java.util.stream.Collectors;

import edu.ucam.applications.commons.Client;
import edu.ucam.server.Server;

public class ClientRepository implements IRepository<Client>{

	@Override
	public void save(Client client) {
		Server.getClients().put(client.getId(), client);
		System.out.println("Cliente added " + client.toString() );
	}

	@Override
	public Client findById(int id) {
		return Server.getClients().get(id);
	}
	
	@Override
	public List<Client> getAll() {
		return Server.getClients().values().stream().collect(Collectors.toList());
	}

	@Override
	public boolean existById(int id) {
		return Server.getClients().get(id) != null;
	}
	
	@Override
	public boolean existById(String id) {
		try {
			return existById(Integer.parseInt(id));
		} catch (NumberFormatException e) {
			System.out.println("Error ID " + id + " " + e.getMessage());
		}
		return false;
	}

	@Override
	public int size() {
		return Server.getClients().isEmpty() ? 0 : Server.getClients().size();
	}

	@Override
	public Client remove(int id) {
		return Server.getClients().remove(id);
	}

	@Override
	public Client update(int id, Client client) {
		return Server.getClients().replace(id, client);
	}

}
