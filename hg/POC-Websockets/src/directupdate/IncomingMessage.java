package directupdate;
/**
 * @author Mark van der Tol
 */
public class IncomingMessage<T> {
	private T data;
	private String action;
	private Integer rowId;

	public T getData() {
		return data;
	}

	public void setData(T data) {
		this.data = data;
	}

	public String getAction() {
		return action;
	}

	public void setAction(String action) {
		this.action = action;
	}

	public Integer getRowId() {
		return rowId;
	}

	public void setRowId(Integer rowId) {
		this.rowId = rowId;
	}
}