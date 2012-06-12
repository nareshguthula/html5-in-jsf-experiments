package backing;

import java.util.List;

import javax.annotation.PostConstruct;
import javax.faces.bean.ApplicationScoped;
import javax.faces.bean.ManagedBean;
import javax.faces.component.UIComponent;
import javax.faces.context.FacesContext;
import javax.faces.event.AbortProcessingException;
import javax.faces.event.PreRenderViewEvent;
import javax.faces.event.SystemEvent;
import javax.faces.event.SystemEventListener;
import javax.servlet.http.HttpServletRequest;
/**
 * @author Mark van der Tol
 */
@ApplicationScoped
@ManagedBean(eager = true)
public class RenderBacking {
	
	@PostConstruct
	public void init() {
		FacesContext context = FacesContext.getCurrentInstance();

		context.getApplication().subscribeToEvent(PreRenderViewEvent.class, new SystemEventListener() {

			@Override
			public void processEvent(SystemEvent event) throws AbortProcessingException {
				//request context for current scope
				FacesContext context = FacesContext.getCurrentInstance();

				HttpServletRequest request = (HttpServletRequest) context.getExternalContext().getRequest();
				String render = request.getParameter("render");
				if (render == null)
					return;

				UIComponent component = context.getViewRoot().findComponent(render);

				if (component != null) {
					List<UIComponent> children = context.getViewRoot().getChildren();
					children.clear();
					children.add(component);
				}
			}

			@Override
			public boolean isListenerForSource(Object source) {
				return true;
			}
		});
	}

}
