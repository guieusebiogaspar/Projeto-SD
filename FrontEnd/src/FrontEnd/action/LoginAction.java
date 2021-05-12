/**
 * Raul Barbosa 2014-11-07
 */
package FrontEnd.action;

import FrontEnd.model.ProjectBean;
import com.opensymphony.xwork2.ActionSupport;
import org.apache.struts2.interceptor.SessionAware;

import java.io.IOException;
import java.util.Map;

public class LoginAction extends ActionSupport implements SessionAware {
	private static final long serialVersionUID = 4L;
	private Map<String, Object> session;
	private String username = null, password = null;

	@Override
	public String execute() throws IOException {
		// any username is accepted without confirmation (should check using RMI)
		if(this.username != null && !username.equals("") && this.password != null && !password.equals("")) {
			this.getProjectBean().setUsername(this.username);
			this.getProjectBean().setPassword(this.password);
			session.put("username", username);
			session.put("password", username);

			// Verifica os dados inseridos no rmi server
			if (this.getProjectBean().getUserMatchesPassword().equals("admin")) {
				session.put("loggedin", true); // this marks the user as logged in
				session.put("admin", true);
				return "successadmin";
			} else if (this.getProjectBean().getUserMatchesPassword().equals("eleitor")) {
				session.put("loggedin", true); // this marks the user as logged in
				session.put("admin", false);
				return "successeleitor";
			} else {
				return LOGIN;
			}
		}
		return LOGIN;
	}
	
	public void setUsername(String username) {
		this.username = username; // will you sanitize this input? maybe use a prepared statement?
	}

	public void setPassword(String password) {
		this.password = password; // what about this input? 
	}
	
	public ProjectBean getProjectBean() {
		if(!session.containsKey("projectBean"))
			this.setHeyBean(new ProjectBean());
		return (ProjectBean) session.get("projectBean");
	}

	public void setHeyBean(ProjectBean projectBean) {
		this.session.put("projectBean", projectBean);
	}

	@Override
	public void setSession(Map<String, Object> session) {
		this.session = session;
	}

	public String logout() {
		session.clear();
		return SUCCESS;
	}
}