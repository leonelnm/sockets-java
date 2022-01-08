package edu.ucam.applications.commons;

import java.io.Serializable;
import java.util.concurrent.atomic.AtomicInteger;

public class Product implements Serializable{
	
	private static final long serialVersionUID = 1L;
	
	private static final AtomicInteger counter = new AtomicInteger(0);
	private int id;
	private String name;
	private double price;
	
	public Product() {
		this.id = counter.incrementAndGet();
	}
	
	public Product(String name, double price) {
		this();
		this.name = name;
		this.price = price;
	}

	public Product(int id) {
		this.id = id;
	}

	public int getId() {
		return id;
	}
	public String getName() {
		return name;
	}
	public void setName(String name) {
		this.name = name;
	}
	public double getPrice() {
		return price;
	}
	public void setPrice(double price) {
		this.price = price;
	}

	@Override
	public String toString() {
		StringBuilder builder = new StringBuilder();
		builder.append("Product [id=");
		builder.append(id);
		builder.append(", name=");
		builder.append(name);
		builder.append(", price=");
		builder.append(price);
		builder.append("]");
		return builder.toString();
	}
	
}
