package directupdate;
/**
 * @author Mark van der Tol
 */
public interface DirectUpdateAdapter {
	public void sendMessageToAll(String messageContent);
	
	public void addIncomingMessageListener(DirectUpdateTextMessageListener messageListener);
	public void removeIncomingMessageListener(DirectUpdateTextMessageListener messageListener);
}
