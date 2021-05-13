package FrontEnd.action;

import FrontEnd.model.ProjectBean;
import com.opensymphony.xwork2.ActionSupport;
import org.apache.struts2.interceptor.SessionAware;

import java.io.IOException;
import java.util.Map;

public class AdminMenuAction extends ActionSupport implements SessionAware {
    private static final long serialVersionUID = 4L;
    private Map<String, Object> session;

    @Override
    public String execute() {
        return SUCCESS;
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

}
