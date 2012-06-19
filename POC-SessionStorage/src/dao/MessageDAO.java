package dao;

import java.util.List;

import javax.ejb.Stateless;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;

import entities.Message;

/**
 * @author Mark van der Tol
 */
@Stateless
public class MessageDAO {

	@PersistenceContext(unitName = "message")
	private EntityManager entityManager;
	
	public Message getMessage(int id) {
		return entityManager.find(Message.class, id);
	}
	
	public List<Message> getAllMessages() {
		return entityManager.createNamedQuery("Message.getAll", Message.class).getResultList();
	}
	
	public void persist(Message message) {
		entityManager.persist(message);
	}
}
