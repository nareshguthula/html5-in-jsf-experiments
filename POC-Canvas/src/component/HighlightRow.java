package component;

import javax.faces.component.behavior.ClientBehaviorBase;
import javax.faces.component.behavior.ClientBehaviorContext;
import javax.faces.component.behavior.FacesBehavior;

/**
 * @author Mark van der Tol
 */
@FacesBehavior(value="component.HighlightRow")
public class HighlightRow extends ClientBehaviorBase {

	private String cssClass;
	final private String SCRIPT = "if (point !== null) {binding.highlightRow(point.id, '%s'); }";
	
	@Override
	public String getScript(ClientBehaviorContext behaviorContext) {
		String eventName = behaviorContext.getEventName();
		if (!"click".equals(eventName) && !"mousemove".equals(eventName)) {
			return "";
		}
		
		return String.format(SCRIPT, cssClass);
	}
	
	public String getCssClass() {
		return cssClass;
	}
	
	public void setCssClass(String color) {
		this.cssClass = color;
	}
}
