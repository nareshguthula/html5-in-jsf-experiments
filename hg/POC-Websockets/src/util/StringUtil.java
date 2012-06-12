package util;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

public class StringUtil {

	public static List<String> splitByWhitespace(String toSplit) {
		List<String> result = new ArrayList<>();

		for (String part : toSplit.split("\\s+")) {
			if (!result.contains(part)) {
				result.add(part);
			}
		}
		
		return result;
	}
	
	public static String joinString(Collection<String> strings, String join) {
		StringBuilder builder = new StringBuilder();
		boolean first = true;
		
		for (String str : strings) {
			if (first) {
				first = false;
			} else {
				builder.append(join);
			}
			builder.append(str);
		}
		
		return builder.toString();
	}
}
