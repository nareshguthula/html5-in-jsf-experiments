package directupdate;

import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.nio.charset.Charset;
import java.util.Collections;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;
import java.util.logging.Level;
import java.util.logging.Logger;

import javax.servlet.ServletConfig;
import javax.servlet.ServletContext;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import com.sun.grizzly.comet.CometContext;
import com.sun.grizzly.comet.CometEngine;
import com.sun.grizzly.comet.CometEvent;
import com.sun.grizzly.comet.CometHandler;
/**
 * @author Mark van der Tol
 */
@SuppressWarnings("unchecked")
public class DirectUpdateCometServlet extends HttpServlet implements DirectUpdateAdapter {
	private static final long serialVersionUID = 2007699602094286981L;
	private Set<DirectUpdateTextMessageListener> messageListeners = Collections.newSetFromMap(new ConcurrentHashMap<DirectUpdateTextMessageListener, Boolean>());

	private String contextPath;
	private Logger logger = Logger.getLogger("directupdate.CometServlet");

	private static DirectUpdateCometServlet instance;
	
	public static DirectUpdateCometServlet getInstance() {
		return instance;
	}

	public void init(ServletConfig config) throws ServletException {
		super.init(config);
		
		ServletContext context = config.getServletContext();
		contextPath = context.getContextPath() + "/comet";

		CometEngine engine = CometEngine.getEngine();
		CometContext<?> cometContext = engine.register(contextPath);
		cometContext.setExpirationDelay(120 * 1000);

		instance = this;
	}

	

	/**
	 * If the post request contains postdata, then the data is processed. Else it is
	 * treated as Comet request.
	 */
	@Override
	protected void doPost(HttpServletRequest request, HttpServletResponse response) {
		try {
			if (request.getContentLength() > 0) {
				String data = readStream(request.getInputStream());

				for (DirectUpdateTextMessageListener messageListener : messageListeners) {
					messageListener.onMessage(data);
				}

				return;
			}

			CometEngine engine = CometEngine.getEngine();
			CometContext<HttpServletResponse> context = engine.getCometContext(contextPath);
			context.addCometHandler(new CometRequestHandler(response));
		} catch (IOException e) {
			logger.log(Level.WARNING, "doPost", e);
		}
	}
	
	@Override
	public void addIncomingMessageListener(DirectUpdateTextMessageListener messageListener) {
		messageListeners.add(messageListener);
	}

	@Override
	public void removeIncomingMessageListener(DirectUpdateTextMessageListener messageListener) {
		messageListeners.remove(messageListener);
	}

	public void sendMessageToAll(String message) {
		CometContext<HttpServletResponse> context = CometEngine.getEngine().getCometContext(contextPath);
		try {
			context.notify(message);
		} catch (IOException e) {
			logger.log(Level.WARNING, "sendMessageToAll", e);
		}
	}
	
	private String readStream(InputStream stream) throws IOException {
		StringBuilder builder = new StringBuilder();

		InputStreamReader reader = new InputStreamReader(stream, Charset.forName("UTF-8"));

		char[] buffer = new char[512];
		int read = 0;
		while ((read = reader.read(buffer)) != -1) {
			builder.append(buffer, 0, read);
		}
		return builder.toString();
	}

	
	private class CometRequestHandler implements CometHandler<HttpServletResponse> {
		private final HttpServletResponse response;

		public CometRequestHandler(HttpServletResponse response) {
			this.response = response;
		}

		@Override
		public void attach(HttpServletResponse attachment) {
			// no-op
		}

		@Override
		@SuppressWarnings("rawtypes")
		public void onEvent(CometEvent event) throws IOException {
			if (CometEvent.NOTIFY == event.getType()) {
				response.setContentType("application/json;charset=UTF-8");

				PrintWriter writer = response.getWriter();
				writer.print(event.attachment());
				writer.flush();

				event.getCometContext().resumeCometHandler(this);
			}

		}

		@Override
		@SuppressWarnings("rawtypes")
		public void onInitialize(CometEvent event) throws IOException {
			// no-op
		}

		@Override
		@SuppressWarnings("rawtypes")
		public void onTerminate(CometEvent event) throws IOException {
			// no-op
		}

		@Override
		@SuppressWarnings("rawtypes")
		public void onInterrupt(CometEvent event) throws IOException {
			// no-op
		}

	}	
}
