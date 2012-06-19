package backing;

import java.util.List;
import java.util.Random;
import java.util.logging.Logger;

import javax.ejb.EJB;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.RequestScoped;

import dao.MessageDAO;
import entities.Message;

/**
 * @author Mark van der Tol
 */
@RequestScoped
@ManagedBean
public class MessageBacking {

	@EJB
	private MessageDAO messageDAO;
	
	private Message currentMessage;
	
	
	public void fillTable() {
		Random rand = new Random();
		Logger.getLogger("").info("Fill");
		for (int i = 1; i <= 50; i++) {
			
			Message message = new Message();
			message.setFromName("From user #" + i);
			
			StringBuilder messageBuilder = new StringBuilder();
			
			messageBuilder.append("Testmessage: ");
			messageBuilder.append(i);
			
			messageBuilder.append("\n\n");
			messageBuilder.append("Some testdata: ");
			
			for (int j = 0; j < 10; j++) {
				messageBuilder.append("\n");
				messageBuilder.append(rand.nextLong());
			}
			message.setMessage(messageBuilder.toString());
			
			messageDAO.persist(message);
		}
	}
	
	public List<Message> getMessages() {
		return messageDAO.getAllMessages();
	}

	public Message getCurrentMessage() {
		return currentMessage;
	}

	public void setCurrentMessage(Message currentMessage) {
		this.currentMessage = currentMessage;
	}
	
}
