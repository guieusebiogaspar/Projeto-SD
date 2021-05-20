package FrontEnd.action;

import FrontEnd.model.ProjectBean;
import RMI.Eleição;
import RMI.Lista;
import com.opensymphony.xwork2.ActionSupport;
import org.apache.struts2.interceptor.SessionAware;

import java.io.IOException;
import java.util.Map;


public class votarListaAction extends ActionSupport implements SessionAware {
    private static final long serialVersionUID = 4L;
    private Map<String, Object> session;
    private String lista = null;

    @Override
    public String execute() throws IOException {
        if(session.get("loggedin").equals("eleitor") == true){
            if(this.lista != null && session.get("searchEleicao") != null) {
                Eleição el = (Eleição) session.get("searchEleicao");
                this.getProjectBean().setLista(this.lista);

                if(el != null && this.getProjectBean().verificaLista(el, lista)) {
                    if(this.getProjectBean().adicionaVoto(el)) {
                        session.remove("lista");
                        session.remove("searchEleicao");
                        return SUCCESS;
                    } else {
                        return ERROR;
                    }
                }
                else
                {
                    return ERROR;
                }
            }

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
