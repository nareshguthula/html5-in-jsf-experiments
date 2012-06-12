package ejb;
import java.util.List;

import javax.ejb.Stateless;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;

import entities.User;
/**
 * @author Mark van der Tol
 */
@Stateless
public class UserDAO {
	
	@PersistenceContext(unitName = "POC-Websockets")
	private EntityManager entityManager;
	
	public void save(User user) {
		entityManager.persist(user);
	}
	
	public void update(User user) {
		entityManager.merge(user);
	}
	
	public void delete(User user) {
		user = entityManager.find(User.class, user.getId());
		entityManager.remove(user);
	}

	public User getByID(int userId) {
		return entityManager.find(User.class, userId);
	}
	
	public List<User> getAll() {
		return entityManager.createNamedQuery("User.getAll", User.class).getResultList();
	}

}
