package component;

import java.io.IOException;
import java.util.Arrays;
import java.util.Collection;

import javax.faces.application.ResourceDependencies;
import javax.faces.application.ResourceDependency;
import javax.faces.component.FacesComponent;
import javax.faces.component.UIInput;
import javax.faces.component.behavior.ClientBehavior;
import javax.faces.component.behavior.ClientBehaviorContext;
import javax.faces.component.behavior.ClientBehaviorHolder;
import javax.faces.context.FacesContext;
import javax.faces.context.ResponseWriter;

import util.StringUtil;

@FacesComponent("component.CanvasGraph")

/**
 * @author Mark van der Tol
 */
@ResourceDependencies({
	@ResourceDependency(library="canvas", name="canvas.js", target="head")
})
public class CanvasGraph extends UIInput implements ClientBehaviorHolder {

	@Override
	public String getDefaultEventName() {
		return "click";
	}
	
	@Override
	public Collection<String> getEventNames() {
		return Arrays.asList("click", "mousemove");
	}
	
	@Override
	public String getFamily() {
		return "component.CanvasGraph";
	}

	@Override
	public boolean getRendersChildren() {
		return true;
	}
	
	@Override
	public void encodeBegin(FacesContext context) throws IOException {
		ResponseWriter writer = context.getResponseWriter();
		writer.startElement("canvas", this);
		writer.writeAttribute("id", getCanvasClientId(context), null);
		
		String style = (String) getAttributes().get("style");
		if (style != null && style.length() != 0) {
			writer.writeAttribute("style", style, null);
		}
		
		String width = (String) getAttributes().get("width");
		if (width != null && width.length() > 0) {
			writer.writeAttribute("width", width, null);
		}
		String height = (String) getAttributes().get("height");
		if (height != null && height.length() > 0) {
			writer.writeAttribute("height", height, null);
		}
	}

	@Override
	public void encodeEnd(FacesContext context) throws IOException {
		ResponseWriter writer = context.getResponseWriter();
		writer.endElement("canvas");
		
		writer.startElement("input", this);
		writer.writeAttribute("id", getInputClientId(context), null);
		writer.writeAttribute("name", getInputClientId(context), null);
		writer.writeAttribute("type", "hidden", null);
		
		if (getValue() != null) {
			writer.writeAttribute("value", getValue(), null);
		}
		
		writer.endElement("input");
		
		writer.startElement("script", this);
		writer.write("$(function () {\n");
		
		writeCanvasBinding(context, writer);	
		writeTableBinding(writer);
		
		writer.write(" });");
		
		writer.endElement("script");
	}
	
	@Override
	public void decode(FacesContext context) {
		super.decode(context);
		
		String clientId = getInputClientId(context);
	    String value = (String) context.getExternalContext().getRequestParameterMap().get(clientId);
		
		setSubmittedValue(value);
	}

	private void writeCanvasBinding(FacesContext context, ResponseWriter writer) throws IOException {
		String backgroundColor = (String) getAttributes().get("backgroundColor");
		if (StringUtil.IsEmpty(backgroundColor)) {
			backgroundColor = "white";
		}
		
		String pointColor = (String) getAttributes().get("pointColor");
		if (StringUtil.IsEmpty(pointColor)) {
			pointColor = "black";
		}
		
		String lineColor = (String) getAttributes().get("lineColor");
		if (StringUtil.IsEmpty(lineColor)) {
			lineColor = "gray";
		}
		
		String axesColor = (String) getAttributes().get("axesColor");
		if (StringUtil.IsEmpty(axesColor)) {
			axesColor = "black";
		}
		
		writer.write("var table = CanvasGraph($(\"[id='");
		writer.writeText(getCanvasClientId(context), null);
		writer.write("']\"), $(\"[id='");
		writer.writeText(getInputClientId(context), null);
		writer.write("']\"),\"");
		writer.writeText(backgroundColor, null);
		writer.write("\", \"");
		writer.writeText(pointColor, null);
		writer.write("\", \"");
		writer.writeText(lineColor, null);
		writer.write("\", \"");
		writer.writeText(axesColor, null);
		
		writer.write("\");\n");
		
		String onclick = getClientBehaviorScript("click", context);
		if (!StringUtil.IsEmpty(onclick)) {
			writer.write("table.onclick = function (event, point) {");
			writer.writeText(onclick, null);
			writer.write("};");
		}
		
		String onmousemove = getClientBehaviorScript("mousemove", context);
		if (!StringUtil.IsEmpty(onmousemove)) {
			writer.write("table.onmousemove = function (event, point) {");
			writer.writeText(onmousemove, null);
			writer.write("};");
		}
	}

	private void writeTableBinding(ResponseWriter writer) throws IOException {
		String table= (String) getAttributes().get("table");
		String idColumnClass = (String) getAttributes().get("idColumn");
		String valueColumnClass = (String) getAttributes().get("valueColumn");
		String visibleCheckboxClass = (String) getAttributes().get("visibleCheckbox");
		
		writer.write("var binding = CanvasGraphTableBinding(table, $(\"[id='");
		writer.writeText(table, null);
		writer.write("']\"), \"");
		writer.writeText(idColumnClass, null);
		writer.write("\", \"");
		writer.writeText(valueColumnClass, null);
		
		if (visibleCheckboxClass != null && visibleCheckboxClass.length() > 0) {
			writer.write("\", \"");
			writer.writeText(visibleCheckboxClass, null);
		}
		writer.write("\");\n");
		writer.write("binding.update();");
	}
	
	private String getClientBehaviorScript(String event, FacesContext context) {
		ClientBehaviorContext clientBehaviorContext = ClientBehaviorContext.createClientBehaviorContext(context, this,
				event, getInputClientId(context), null);
		
		StringBuilder builder = new StringBuilder();
		for (ClientBehavior behavior : getClientBehaviors().get(event)) {
			builder.append(behavior.getScript(clientBehaviorContext));
			builder.append(';');
		}
		return builder.toString();
	}
	
	private String getInputClientId(FacesContext context) {
		//must have the same Id as the clientId, else the decode method won't be called.
		return getClientId(context);
	}
	
	private String getCanvasClientId(FacesContext context) {
		return getClientId(context) + ":canvas";
	}
}
