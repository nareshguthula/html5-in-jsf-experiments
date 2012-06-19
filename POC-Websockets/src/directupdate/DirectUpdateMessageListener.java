package directupdate;

public interface DirectUpdateMessageListener<T> {

	public void onMessage(IncomingMessage<T> action);
}
