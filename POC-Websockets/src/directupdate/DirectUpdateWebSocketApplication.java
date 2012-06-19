package directupdate;

import java.util.Collections;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;
import java.util.logging.Level;
import java.util.logging.Logger;

import com.sun.grizzly.tcp.Request;
import com.sun.grizzly.websockets.WebSocket;
import com.sun.grizzly.websockets.WebSocketApplication;
import com.sun.grizzly.websockets.WebSocketEngine;
import com.sun.grizzly.websockets.WebSocketException;
/**
 * @author Mark van der Tol
 */
public class DirectUpdateWebSocketApplication extends WebSocketApplication implements DirectUpdateAdapter {
	private Logger logger = Logger.getLogger("directupdate.DirectUpdateWebSocketApplication");
	private Set<DirectUpdateTextMessageListener> messageListeners = 
			Collections.newSetFromMap(new ConcurrentHashMap<DirectUpdateTextMessageListener, Boolean>());

	public void register() {
		WebSocketEngine.getEngine().register(this);
	}
	
	public void unregister() {
		WebSocketEngine.getEngine().unregister(this);
	}
	
	@Override
	public boolean isApplicationRequest(Request request) {
		return true;
	}

	@Override
	public void onMessage(WebSocket socket, String text) {
		try {
			logger.fine("Received: " + text);		
			for (DirectUpdateTextMessageListener listener : messageListeners) {
				listener.onMessage(text);
			}
		} catch (Exception e) {
			logger.log(Level.SEVERE, "Error in onMessage", e);
		}
	}
	
	
	@Override
	public void sendMessageToAll(String messageContent) {
		for(WebSocket socket : getWebSockets()) {
			try {
				socket.send(messageContent);
			}
			catch (WebSocketException e) {
				logger.log(Level.FINE, "sendMessageToAll", e);
			}
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
}
