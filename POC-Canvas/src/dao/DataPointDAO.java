package dao;

import java.util.List;

import javax.ejb.Stateless;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;

import entities.DataPoint;

/**
 * @author Mark van der Tol
 */
@Stateless
public class DataPointDAO {
	@PersistenceContext(unitName = "POC-Canvas")
	private EntityManager entityManager;
	
	public void persist(DataPoint dataPoint) {
		entityManager.persist(dataPoint);
	}

	public void merge(DataPoint dataPoint) {
		entityManager.merge(dataPoint);
	}
	
	public DataPoint getById(int id) {
		return entityManager.find(DataPoint.class, id);
	}

	public List<DataPoint> getAll() {
		return entityManager.createNamedQuery("DataPoint.getAll", DataPoint.class).getResultList();
	}
}
