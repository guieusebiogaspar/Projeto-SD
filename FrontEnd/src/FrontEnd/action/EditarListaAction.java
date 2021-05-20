package FrontEnd.action;

import FrontEnd.model.ProjectBean;
import RMI.Eleição;
import com.opensymphony.xwork2.ActionSupport;
import org.apache.struts2.interceptor.SessionAware;

import java.io.IOException;
import java.util.Map;

public class EditarListaAction extends ActionSupport implements SessionAware{
    private static final long serialVersionUID = 4L;
    private Map<String, Object> session;
    private String adicionaLista = null, removeLista = null;
    private String nomeLista = null, adicionaPess = null, removePess = null;

    @Override
    public String execute() throws IOException {
        if (session.get("loggedin").equals("admin")) {
            Eleição el = (Eleição) session.get("searchEleicao");

            if(this.nomeLista != null && !this.nomeLista.equals("")) {
                this.getProjectBean().setNomeLista(this.nomeLista);

                if(this.getProjectBean().verificaLista(el, nomeLista)) {
                    return ERROR;
                } else {
                    this.getProjectBean().mudaNomeLista(el, session.get("lista").toString());
                }
            }

            if(this.adicionaPess != null && !this.adicionaPess.equals("")) {
                this.getProjectBean().setPessoaLista(this.adicionaPess);

                this.getProjectBean().adicionaPessoaLista(el, session.get("lista").toString());
            }

            if(this.removePess != null && !this.removePess.equals("")) {
                this.getProjectBean().setPessoaLista(this.removePess);

                this.getProjectBean().removePessoaLista(el, session.get("lista").toString());
            }

            session.remove("lista");
            return SUCCESS;
        }
        return LOGIN;
    }

    public String addRmv() throws IOException{
        if (session.get("loggedin").equals("admin")) {
            Eleição el = (Eleição) session.get("searchEleicao");

            if (this.adicionaLista != null && !this.adicionaLista.equals("")) {
                this.getProjectBean().setLista(this.adicionaLista);

                if (this.getProjectBean().verificaLista(el, adicionaLista)) {
                    return ERROR;
                } else {
                    this.getProjectBean().adicionaListaEleicao(el.getTitulo());
                }
            }

            if (this.removeLista != null && !this.removeLista.equals("")) {
                this.getProjectBean().setLista(this.removeLista);

                this.getProjectBean().removeListaEleicao(el.getTitulo());
            }

            return SUCCESS;
        }

        return LOGIN;
    }

    public void setAdicionaLista(String adicionaLista) {
        this.adicionaLista = adicionaLista;
    }

    public void setRemoveLista(String removeLista) {
        this.removeLista = removeLista;
    }

    public void setNomeLista(String nomeLista) {
        this.nomeLista = nomeLista;
    }

    public void setAdicionaPess(String adicionaPess) {
        this.adicionaPess = adicionaPess;
    }

    public void setRemovePess(String removePess) {
        this.removePess = removePess;
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
