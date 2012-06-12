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
 * During initialization it subscribes to PreRenderViewEvents. During the handling of the event is looked at the <code>render</code>
 * request parameter. When the parameter is present, only the single element with the name name as parameter will be rendered.
 * 
 * @author Mark van der Tol
 */
@ApplicationScoped
@ManagedBean(eager = true)
public class RenderBacking {
	
	@PostConstruct
	public void init() {
		FacesContext.getCurrentInstance().getApplication().subscribeToEvent(PreRenderViewEvent.class, new SystemEventListener() {

			@Override
			public void processEvent(SystemEvent event) throws AbortProcessingException {
				//request context for current scope
				FacesContext context = FacesContext.getCurrentInstance();

				HttpServletRequest request = (HttpServletRequest) context.getExternalContext().getRequest();
				String render = request.getParameter("render");
				if (render == null)
					return;

				UIComponent componentToRender = context.getViewRoot().findComponent(render);

				List<UIComponent> viewRootchildren = context.getViewRoot().getChildren();

				if (componentToRender != null) {
					viewRootchildren.clear();
					viewRootchildren.add(componentToRender);
				}
			}

			@Override
			public boolean isListenerForSource(Object source) {
				return true;
			}
		});
	}

}
