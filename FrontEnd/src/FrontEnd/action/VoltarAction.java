package FrontEnd.action;

import FrontEnd.model.ProjectBean;
import com.opensymphony.xwork2.ActionSupport;
import org.apache.struts2.interceptor.SessionAware;

import java.io.IOException;
import java.util.Map;

public class VoltarAction extends ActionSupport implements SessionAware {
    private static final long serialVersionUID = 4L;
    private Map<String, Object> session;

    @Override
    public String execute() throws IOException {
        String username = session.get("username").toString();
        String tipo = session.get("loggedin").toString();
        session.remove("searchEleicao");
        session.remove("searchPessoa");
        session.put("loggedin", tipo); // this marks the user as logged in
        session.put("username", username);
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
