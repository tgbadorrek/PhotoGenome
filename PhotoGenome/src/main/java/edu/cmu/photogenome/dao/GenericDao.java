package edu.cmu.photogenome.dao;

import java.io.Serializable;
import java.util.List;

import org.hibernate.Session;

public interface GenericDao<T, ID extends Serializable> {
	
	public boolean delete(T entity);
	
	public T findById(ID id);
	
	public List<T> findAll();
	
	public boolean save(T entity);
	
	public boolean update(T entity);
	
	public Session getSession();
	
	public void setSession(Session session);
}
