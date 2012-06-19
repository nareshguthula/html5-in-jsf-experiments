package entities;

import javax.persistence.Entity;
import javax.persistence.Id;

/**
 * @author Mark van der Tol
 */
@Entity
public class DataPoint {
	@Id
	private Integer id;
	
	private String name;
	
	private int value;

	public Integer getId() {
		return id;
	}

	public void setId(Integer id) {
		this.id = id;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public int getValue() {
		return value;
	}

	public void setValue(int value) {
		this.value = value;
	}
	
	
}
