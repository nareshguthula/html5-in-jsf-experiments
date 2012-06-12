package directupdate;

import java.lang.reflect.Type;
import java.util.Collections;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonDeserializationContext;
import com.google.gson.JsonDeserializer;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParseException;

/**
 * @author Mark van der Tol
 */
public class DirectUpdate<T> {
	
	private final DirectUpdateWebSocketApplication webSocketApplication;
	private final DirectUpdateCometServlet cometServlet;
	private final Set<DirectUpdateMessageListener<T>> messageListeners = Collections.newSetFromMap(new ConcurrentHashMap<DirectUpdateMessageListener<T>, Boolean>());
	
	private final Class<T> actionType;
	private final Gson gson;
	
	private DirectUpdateTextMessageListener incomingMessageListener = new DirectUpdateTextMessageListener() {
		
		@Override
		public void onMessage(String message) {
			@SuppressWarnings("unchecked")
			IncomingMessage<T> action = (IncomingMessage<T>) gson.fromJson(message, IncomingMessage.class);
			
			for (DirectUpdateMessageListener<T> listener : messageListeners) {
				listener.onMessage(action);
			}
		}
	};
	
	public DirectUpdate(Class<T> actionType) {
		this.actionType = actionType;
		
		webSocketApplication = new DirectUpdateWebSocketApplication();
		webSocketApplication.register();
		
		cometServlet = DirectUpdateCometServlet.getInstance();
		
		webSocketApplication.addIncomingMessageListener(incomingMessageListener);
		cometServlet.addIncomingMessageListener(incomingMessageListener);
		
		gson = new GsonBuilder().registerTypeAdapter(IncomingMessage.class, new ActionDeserializer()).create();
	}
	
	public void close() {
		webSocketApplication.unregister();
	}
		
	public void sendMessageToAll(OutgoingMessage result) {
		String messageContent = gson.toJson(result);
		
		cometServlet.sendMessageToAll(messageContent);
		webSocketApplication.sendMessageToAll(messageContent);
	}


	public void addIncomingMessageListener(DirectUpdateMessageListener<T> messageListener) {
		messageListeners.add(messageListener);
	}


	public void removeIncomingMessageListener(DirectUpdateMessageListener<T> messageListener) {
		messageListeners.remove(messageListener);	
	}

	
	private class ActionDeserializer implements JsonDeserializer<IncomingMessage<T>> {
		//A deserializer is required, because the field Data is a generic type and the type
		//can be picked during runtime
		
		@Override
		@SuppressWarnings("unchecked")
		public IncomingMessage<T> deserialize(JsonElement json, Type typeOf, JsonDeserializationContext context) throws JsonParseException {
			JsonObject object = json.getAsJsonObject();
			IncomingMessage<T> result = new IncomingMessage<>();
			
			JsonElement actionElement = object.get("action");
			if (actionElement != null) {
				result.setAction(actionElement.getAsString());
			}
			JsonElement rowIdElement = object.get("rowId");
			if (rowIdElement != null) {
				result.setRowId(rowIdElement.getAsInt());
			}
			JsonElement dataElement = object.get("data");
			if (dataElement != null) {
				result.setData((T) context.deserialize(dataElement, actionType));
			}
			
			return result;
		}
		
	}
}
