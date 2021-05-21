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
        // limpa a session
        if(session.get("loggedin").equals("admin") == true) {
            session.remove("searchEleicao");
            session.remove("searchPessoa");
            session.remove("lista");
            session.remove("vencedora");
            return SUCCESS;
        }
        return LOGIN;
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
