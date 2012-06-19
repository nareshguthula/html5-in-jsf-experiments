package entities;

import javax.ejb.EJB;
import javax.faces.bean.ManagedBean;
import javax.faces.component.UIComponent;
import javax.faces.context.FacesContext;
import javax.faces.convert.Converter;

import dao.UserDAO;

/**
 * @author Mark van der Tol
 */
@ManagedBean
public class UserConvertor implements Converter {

	@EJB
	private UserDAO users;
	
	@Override
	public Object getAsObject(FacesContext context, UIComponent component, String value) {
		if (value == null) {
			return null;
		}
		int id = Integer.parseInt(value);
		
		return users.getByID(id);
	}

	@Override
	public String getAsString(FacesContext context, UIComponent component, Object value) {
		if (value == null) {
			return null;
		}
		Integer id = ((User) value).getId();
		
		return (id != null) ? id.toString() : null;
	}

}
