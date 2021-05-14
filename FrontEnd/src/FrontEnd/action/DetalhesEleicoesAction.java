package FrontEnd.action;

import FrontEnd.model.ProjectBean;
import RMI.Eleição;
import RMI.Pessoa;
import com.opensymphony.xwork2.ActionSupport;
import org.apache.struts2.interceptor.SessionAware;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Map;

public class DetalhesEleicoesAction extends ActionSupport implements SessionAware {
    private static final long serialVersionUID = 4L;
    private Map<String, Object> session;
    private String eleicao = null;

    @Override
    public String execute() throws IOException {
        if(this.eleicao != null) {
            this.getProjectBean().setEleicao(this.eleicao);
            Eleição el = this.getProjectBean().getEleição(this.eleicao);

            if(el != null) {
                session.put("searchEleicao", el);
                return SUCCESS;
            }
            else
            {
                session.remove("searchEleicao");
                return ERROR;
            }
        }

        session.remove("searchEleicao");
        return INPUT;
    }

    public void setEleicao(String eleicao) {
        this.eleicao = eleicao;
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
