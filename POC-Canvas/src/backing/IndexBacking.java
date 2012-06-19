package backing;

import java.util.Collection;

import javax.ejb.EJB;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.RequestScoped;

import dao.DataPointDAO;

import entities.DataPoint;

/**
 * @author Mark van der Tol
 */
@RequestScoped
@ManagedBean
public class IndexBacking {

	@EJB
	private DataPointDAO dataPointDAO;
	private String value = "No point clicked";
	
	public Collection<DataPoint> getAllDataPoints() {
		return dataPointDAO.getAll();
	}
	
	
	public String getValue() {
		return value;
	}
	
	public void setValue(String value) {
		this.value = value;
	}

}
