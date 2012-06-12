package entities;

import javax.ejb.EJB;
import javax.faces.bean.ManagedBean;
import javax.faces.component.UIComponent;
import javax.faces.context.FacesContext;
import javax.faces.convert.Converter;

import dao.MessageDAO;

/**
 * @author Mark van der Tol
 */
@ManagedBean
public class MessageConvertor implements Converter {

	@EJB
	private MessageDAO messageDAO;
	
	@Override
	public Object getAsObject(FacesContext context, UIComponent component, String value) {
		if (value == null) {
			return null;
		}
		int id = Integer.parseInt(value);
		
		return messageDAO.getMessage(id);
	}

	@Override
	public String getAsString(FacesContext context, UIComponent component, Object value) {
		if (value == null) {
			return null;
		}
		Integer id = ((Message) value).getId();
		
		return (id != null) ? id.toString() : null;
	}

}
