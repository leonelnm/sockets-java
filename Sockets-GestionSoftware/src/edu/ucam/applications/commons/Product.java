package edu.ucam.applications.commons;

import java.util.concurrent.atomic.AtomicInteger;

public class Product {
	
	private static final AtomicInteger counter = new AtomicInteger(0);
	private int id;
	private String name;
	private double price;
	
	public Product(String name, double price) {
		this.id = counter.incrementAndGet();
		this.name = name;
		this.price = price;
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
	
	
}
