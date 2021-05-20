package FrontEnd.action;

import FrontEnd.model.ProjectBean;
import RMI.Eleição;
import com.opensymphony.xwork2.ActionSupport;
import org.apache.struts2.interceptor.SessionAware;

import java.io.IOException;
import java.util.Map;

public class DetalhesListaAction extends ActionSupport implements SessionAware {
    private static final long serialVersionUID = 4L;
    private Map<String, Object> session;
    private String lista = null;

    @Override
    public String execute() throws IOException {
        if(session.get("loggedin").equals("admin") == true){
            if(this.lista != null && session.get("searchEleicao") != null) {
                Eleição el = (Eleição) session.get("searchEleicao");
                this.getProjectBean().setEleicao(el.getTitulo());


                if(el != null && !this.getProjectBean().verificaLista(el, lista)) {
                    session.put("lista", lista);
                    return SUCCESS;
                }
                else
                {
                    session.remove("lista");
                    return ERROR;
                }
            }

            session.remove("lista");
            return INPUT;
        } else {
            return LOGIN;
        }
    }

    public void setLista(String lista) {
        this.lista = lista;
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
