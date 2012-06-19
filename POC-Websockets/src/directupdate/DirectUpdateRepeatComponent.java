package directupdate;

import java.io.IOException;
import java.lang.reflect.InvocationTargetException;
import java.util.Collection;

import javax.faces.FacesException;
import javax.faces.application.ResourceDependencies;
import javax.faces.application.ResourceDependency;
import javax.faces.component.FacesComponent;
import javax.faces.component.UIComponent;
import javax.faces.component.UIData;
import javax.faces.context.FacesContext;
import javax.faces.context.ResponseWriter;
import javax.faces.model.DataModel;
import javax.faces.view.facelets.FaceletException;

import util.JavascriptHelper;
import util.StringUtil;

/**
 * @author Mark van der Tol
 */
@FacesComponent(value = "component.DirectUpdateRepeat")
@ResourceDependencies({
	@ResourceDependency(library="websocket", name="websockets.js", target="head"),
	@ResourceDependency(library="websocket", name="directUpdate.js", target="head")
})
public class DirectUpdateRepeatComponent extends UIData {

	@Override
	public String getFamily() {
		return "component.DirectUpdateRepeatComponent";
	}

	@Override
	public boolean getRendersChildren() {
		return true;
	}

	@Override
	public void encodeBegin(FacesContext context) throws IOException {
		setRowIndex(-1);

		ResponseWriter writer = context.getResponseWriter();
		if (getRenderOnlyId(context) == null) {
			writer.startElement(getElementType(), this);
			writer.writeAttribute("id", getClientId(context), null);
		}
	}

	@Override
	public void encodeEnd(FacesContext context) throws IOException {
		ResponseWriter writer = context.getResponseWriter();
		if (getRenderOnlyId(context) == null) {
			writer.endElement(getElementType());
			writeScript(writer);
		}
	}

	@Override
	public void encodeChildren(FacesContext context) throws IOException {
		DataModel<?> model = getDataModel();

		Integer renderOnlyId = getRenderOnlyId(context);

		for (int i = 0; model.isRowAvailable(); model.setRowIndex(++i)) {
			setRowIndex(i);
			Object element = model.getRowData();
			int index;
			try {
				index = (Integer) element.getClass().getMethod("get" + getIdField()).invoke(element);
			} catch (IllegalAccessException | IllegalArgumentException | InvocationTargetException | NoSuchMethodException | SecurityException e) {
				throw new FaceletException(e);
			}

			if (renderOnlyId != null && !renderOnlyId.equals(index)) {
				continue;
			}

			String idRowVar = getRowIdVar();
			context.getELContext().getELResolver().setValue(context.getELContext(), null, idRowVar, "" + index);

			for (UIComponent component : getChildren()) {
				encodeRecursive(context, component);
			}
		}
		setRowIndex(-1);
	}
	
	protected void encodeRecursive(FacesContext context, UIComponent component) throws IOException {

		if (!component.isRendered()) {
			return;
		}

		// HttpRequestHelper this component and its children recursively
		component.encodeBegin(context);
		if (component.getRendersChildren()) {
			component.encodeChildren(context);
		} else {
			for (UIComponent child : component.getChildren()) {
				encodeRecursive(context, child);
			}
		}
		component.encodeEnd(context);
	}

	private String getRowIdVar() {
		String rowIdVar = (String) getAttributes().get("rowIdVar");
		if (rowIdVar == null || "".equals(rowIdVar)) {
			throw new FacesException("rowIdVar is empty");
		}

		return rowIdVar;
	}

	private String getElementType() {
		String rowIdVar = (String) getAttributes().get("elementType");
		if (rowIdVar == null || "".equals(rowIdVar)) {
			return "div";
		}

		return rowIdVar;
	}

	private Integer getRenderOnlyId(FacesContext context) {
		String renderOnlyValue = context.getExternalContext().getRequestParameterMap().get("renderOnly");
		if (renderOnlyValue != null) {
			return Integer.parseInt(renderOnlyValue);
		}
		return null;
	}

	private void writeScript(ResponseWriter writer) throws IOException {
		writer.startElement("script", this);
		writer.write("//");
		writer.startCDATA();
		writer.write("\n");

		final String scriptFormat = "$(function () { directUpdate.openSocket(%s, %s, %s, [%s]);";

		writer.write(String.format(scriptFormat, JavascriptHelper.formatAsArgument(getWebsocketUri()), JavascriptHelper.formatAsArgument(getLongpollUri()),
				JavascriptHelper.formatAsJQueryObjectById(getClientId()), StringUtil.joinString(JavascriptHelper.formatAsArguments(getFormFields()), ", ")));

		writer.write("});");
		writer.write("\n//");
		writer.endCDATA();
		writer.endElement("script");

	}

	private String getIdField() {
		String fieldName = (String) getAttributes().get("idField");
		if (fieldName == null || "".equals(fieldName)) {
			throw new FacesException("IdField is empty");
		}

		return fieldName.substring(0, 1).toUpperCase() + fieldName.substring(1);
	}

	private String getWebsocketUri() {
		String websocketUri = (String) getAttributes().get("websocketUri");
		if (websocketUri == null || "".equals(websocketUri)) {
			throw new FacesException("websocketUri is empty");
		}

		return websocketUri;
	}

	private String getLongpollUri() {
		String longpollUri = (String) getAttributes().get("longpollUri");
		if (longpollUri == null || "".equals(longpollUri)) {
			throw new FacesException("longpollUri is empty");
		}

		return longpollUri;
	}

	private Collection<String> getFormFields() {
		String formFields = (String) getAttributes().get("formFields");
		if (formFields == null || "".equals(formFields)) {
			throw new FacesException("formFields is empty");
		}
		return StringUtil.splitByWhitespace(formFields);
	}

}
