package backing;

import javax.annotation.PostConstruct;
import javax.ejb.EJB;
import javax.faces.bean.ApplicationScoped;
import javax.faces.bean.ManagedBean;

import persistence.DataPointDAO;
import entities.DataPoint;

/**
 * @author Mark van der Tol
 */
@ApplicationScoped
@ManagedBean(eager = true)
public class DataInitialize {

	@EJB
	private DataPointDAO dataPointDAO;

	@PostConstruct
	public void init() {
		if (dataPointDAO.getById(1) != null)
			return;

		for (int i = 0; i <= 50; i++) {
			int value = i * i - 50 * i + 650;
			addPoint(i, value);
		}
		
	}

	private void addPoint(int id, int value) {
		DataPoint newPoint = new DataPoint();
		newPoint.setId(id);
		newPoint.setValue(value);
		newPoint.setName("[Id : " + id + "]");

		dataPointDAO.persist(newPoint);
	}
}
