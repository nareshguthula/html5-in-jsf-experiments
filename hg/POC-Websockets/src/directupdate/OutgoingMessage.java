package directupdate;
/**
 * @author Mark van der Tol
 */
public class OutgoingMessage {
	private String action;
	private Integer rowId;
	private String html;
	
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
	
	public String getHtml() {
		return html;
	}
	public void setHtml(String html) {
		this.html = html;
	}
}