package edu.ucam.server.repository;

import java.util.List;

public interface IRepository<T> {
	
	public void save(T t);
	public T findById(int id);
	public boolean existById(int id);
	public boolean existById(String id);
	public List<T> getAll();
	public T remove(int id);
	public T update(int id, T t);
	public int size();

}
