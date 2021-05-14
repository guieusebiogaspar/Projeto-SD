package FrontEnd.action;

import FrontEnd.model.ProjectBean;
import RMI.Pessoa;
import com.opensymphony.xwork2.ActionSupport;
import org.apache.struts2.interceptor.SessionAware;

import java.io.IOException;
import java.util.Map;

public class DetalhesPessoasAction extends ActionSupport implements SessionAware {
    private static final long serialVersionUID = 4L;
    private Map<String, Object> session;
    private String cc = null;

    @Override
    public String execute() throws IOException {
        if(this.cc != null) {
            this.getProjectBean().setCc(this.cc);
            Pessoa p = this.getProjectBean().getPessoa(this.cc);

            if(p != null) {
                session.put("searchPessoa", p);
                return SUCCESS;
            }
            else
            {
                session.remove("searchPessoa");
                return ERROR;
            }
        }

        session.remove("searchPessoa");
        return INPUT;
    }

    public void setCc(String cc) {
        this.cc = cc;
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
