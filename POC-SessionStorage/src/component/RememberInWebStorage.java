package component;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Collection;

import javax.faces.application.ResourceDependencies;
import javax.faces.application.ResourceDependency;
import javax.faces.component.FacesComponent;
import javax.faces.component.UIComponent;
import javax.faces.component.UIComponentBase;
import javax.faces.component.UIForm;
import javax.faces.context.FacesContext;
import javax.faces.context.ResponseWriter;
/**
 * @author Mark van der Tol
 */
@FacesComponent(value = "component.RememberInWebStorage")
@ResourceDependencies({
	@ResourceDependency(library="webstorage", name="storage.js", target="head")
})
public class RememberInWebStorage extends UIComponentBase {

	@Override
	public void encodeBegin(FacesContext context) throws IOException {
		ResponseWriter writer = context.getResponseWriter();
		
		writer.startElement("script", this);
		writer.writeText("$(function () {\n", null);
		
		writeFunctionsForFields(context, writer);
		
		writer.writeText("});", null);
		writer.endElement("script");
	}
		
	public StorageType getStorageTypeFromString(String storageType) {		
		switch (storageType) {
			case "session":
				return StorageType.SESSION;
			case "local":
				return StorageType.LOCAL;
			default:
				throw new IllegalArgumentException("Invalid storageType");
		}		
	}
	
	@Override
	public String getFamily() {
		return "component.RememberInWebStorage";
	}
	
	private void writeFunctionsForFields(FacesContext context, ResponseWriter writer) throws IOException {
		StorageType type = getStorageTypeFromString((String) getAttributes().get("persistence"));
		Collection<UIComponent> components = getComponentsFromString((String) getAttributes().get("fields"));
		
		for (UIComponent component : components) {
			writer.writeText("storage.registerSessionStorageElement($(\"[id='" + component.getClientId() + "']\"), ", null);
			writer.writeText(type.getInterfaceName() + ");", null);
		}
	}
	
	private UIForm getParentForm(UIComponent component) {
		if (component.getParent() instanceof UIForm) {
			return (UIForm) component.getParent();
		}
		return getParentForm(component.getParent());
	}
	
	private Collection<UIComponent> getComponentsFromString(String str) {
		UIForm form = getParentForm(this);
		
		ArrayList<UIComponent> result = new ArrayList<>();
		for (String componentClientId : str.split("\\s+")) {
			UIComponent component = form.findComponent(componentClientId);
			if (component == null) {
				throw new IllegalArgumentException("Couldn't find '" + componentClientId + "'");
			}
			result.add(component);
		}
		return result;
	}
	
	private enum StorageType {
		SESSION("sessionStorage"),
		LOCAL("localStorage");
		
		private String interfaceName;
		
		public String getInterfaceName() {
			return interfaceName;
		}
		
		private StorageType(String interfaceName) {
			this.interfaceName = interfaceName;
		}
	}
}
