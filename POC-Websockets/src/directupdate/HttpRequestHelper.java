package directupdate;

import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.URL;
import java.net.URLConnection;
/**
 * @author Mark van der Tol
 */
public class HttpRequestHelper {

	public static String requestPage(String path) {
		URL url;
		
		try {
			url = new URL(path);
			URLConnection connection = url.openConnection();
			InputStreamReader in = new InputStreamReader((InputStream) connection.getContent());
		   
			char[] buffer = new char[1024];
			int charsRead;
			StringBuilder result = new StringBuilder();
			while ((charsRead = in.read(buffer)) != -1) {
				result.append(buffer, 0, charsRead);
			}
			return result.toString();
			
		} catch (IOException e) {
			e.printStackTrace();
		}
		
		return "";
	}
}
