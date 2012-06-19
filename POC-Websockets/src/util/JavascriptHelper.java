package util;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

/**
 * @author Mark van der Tol
 */
public class JavascriptHelper {

	public static String formatAsArgument(String e) {
		return '"' + e.replace("\"", "\\\"") + '"';
	}
	
	public static String formatAsJQueryObjectById(String id) {
		return "$(\"[id='" + id.replace("\"", "\\\"") + "']\")";
	}
	
	public static List<String> formatAsArguments(Collection<String> arguments) {
		List<String> result = new ArrayList<>(arguments.size());
		for (String argument : arguments) {
			result.add(formatAsArgument(argument));
		}
		return result;
	}
}
