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

			// Verifica os dados inseridos no rmi server
			if (this.getProjectBean().getUserMatchesPassword().equals("admin")) {
				session.put("loggedin", "admin"); // this marks the user as logged in
				return "successadmin";
			} else if (this.getProjectBean().getUserMatchesPassword().equals("eleitor")) {
				session.put("loggedin", "eleitor"); // this marks the user as logged in
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

	public String logout() {
		session.clear();
		return SUCCESS;
	}
	
	public ProjectBean getProjectBean() {
		if(!session.containsKey("projectBean"))
			this.setProjectBean(new ProjectBean());
		return (ProjectBean) session.get("projectBean");
	}

	public void setProjectBean(ProjectBean projectBean) {
		this.session.put("projectBean", projectBean);
	}

	@Override
	public void setSession(Map<String, Object> session) {
		this.session = session;
	}

}
