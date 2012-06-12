package backing;

import java.util.logging.Logger;

import javax.annotation.PostConstruct;
import javax.annotation.PreDestroy;
import javax.ejb.EJB;
import javax.faces.bean.ApplicationScoped;
import javax.faces.bean.ManagedBean;

import directupdate.IncomingMessage;
import directupdate.DirectUpdate;
import directupdate.DirectUpdateMessageListener;
import directupdate.HttpRequestHelper;
import directupdate.OutgoingMessage;
import ejb.UserDAO;
import entities.User;
/**
 * @author Mark van der Tol
 */
@ApplicationScoped
@ManagedBean
public class WebSocketData {
	private Logger logger = Logger.getLogger("backing.WebSocketData");
	private DirectUpdate<User> directUpdate;

	@EJB
	private UserDAO userDAO;

	public String init() {
		return "";
	}

	@PostConstruct
	public void setup() {
		logger.info("Setup DirectUpdate");

		directUpdate = new DirectUpdate<>(User.class);
		directUpdate.addIncomingMessageListener(new DirectUpdateMessageListener<User>() {

			@Override
			public void onMessage(IncomingMessage<User> action) {
				handleMessage(action);
			}
		});
	}

	public void handleMessage(IncomingMessage<User> action) {
		if (action.getData() != null && (action.getData().getId() == null || action.getData().getId() == 0)) {
			action.getData().setId(null);
		}

		if ("save".equalsIgnoreCase(action.getAction())) {
			if (action.getData().getId() == null) {
				userDAO.save(action.getData());
			} else {
				userDAO.update(action.getData());
			}

			int rowId = action.getData().getId();
			
			OutgoingMessage result = new OutgoingMessage();
			result.setAction("save");
			result.setRowId(rowId);
			
			String path = "http://localhost:8080/POC-Websockets/?render=testTable&renderOnly=" + rowId;
			result.setHtml(HttpRequestHelper.requestPage(path));

			directUpdate.sendMessageToAll(result);
		} else if ("delete".equalsIgnoreCase(action.getAction())) {
			User user = userDAO.getByID(action.getRowId());
			if (user != null) {
				userDAO.delete(user);
			}
			
			OutgoingMessage result = new OutgoingMessage();
			result.setAction("delete");
			result.setRowId(action.getRowId());

			directUpdate.sendMessageToAll(result);
		} else {
			logger.warning("Unknown message: " + action.getAction());
		}
	}

	@PreDestroy
	public void destroy() {
		directUpdate.close();
	}

	public UserDAO getUserDAO() {
		return userDAO;
	}
}
