package edu.ucam.server.repository;

import java.util.List;
import java.util.stream.Collectors;

import edu.ucam.applications.commons.Product;
import edu.ucam.server.Server;

public class ProductRepository implements IRepository<Product>{

	@Override
	public void save(Product product) {
		if(product != null) {
			Server.getProducts().put(product.getId(), product);
		}
		
	}

	@Override
	public Product findById(int id) {
		return Server.getProducts().get(id);
	}

	@Override
	public List<Product> getAll() {
		return Server.getProducts().values().stream().collect(Collectors.toList());
	}

	@Override
	public boolean existById(int id) {
		return Server.getProducts().get(id) != null;
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
		return Server.getProducts().isEmpty() ? 0 : Server.getProducts().size();
	}

	@Override
	public Product remove(int id) {
		return Server.getProducts().remove(id);
	}

	@Override
	public Product update(int id, Product product) {
		return Server.getProducts().replace(id, product);
	}

}
