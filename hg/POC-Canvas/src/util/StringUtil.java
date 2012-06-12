package util;

/**
 * @author Mark van der Tol
 */
public class StringUtil {

	public static boolean IsEmpty(String str) {
		if (str == null)
			return true;
		if (str.length() == 0)
			return true;
		return false;
	}
}
