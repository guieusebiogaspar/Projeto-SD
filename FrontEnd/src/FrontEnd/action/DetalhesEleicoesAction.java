package FrontEnd.action;

import FrontEnd.model.ProjectBean;
import RMI.Eleição;
import RMI.Pessoa;
import com.opensymphony.xwork2.ActionSupport;
import org.apache.struts2.interceptor.SessionAware;

import java.io.IOException;
import java.sql.SQLOutput;
import java.util.ArrayList;
import java.util.Map;

public class DetalhesEleicoesAction extends ActionSupport implements SessionAware {
    private static final long serialVersionUID = 4L;
    private Map<String, Object> session;
    private String eleicao = null;

    @Override
    public String execute() throws IOException {
        if(session.get("loggedin").equals("admin") == true){
            if(this.eleicao != null) {
                this.getProjectBean().setEleicao(this.eleicao);
                Eleição el = this.getProjectBean().getEleição();

                if(el != null) {
                    session.put("searchEleicao", el);
                    if(el.getTerminada()){
                        session.put("vencedora", this.getProjectBean().getVencedora());
                    }
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
        } else {
            return LOGIN;
        }
    }

    public String listasVotar() throws IOException {
        if(session.get("loggedin").equals("eleitor")){
            if(this.eleicao != null) {
                this.getProjectBean().setEleicao(this.eleicao);
                Eleição el = this.getProjectBean().getEleição();
                System.out.println(el.getTitulo());
                ArrayList<String> eleiçõesNome = new ArrayList<>();
                for(int i = 0; i < this.getProjectBean().getAtivasVoto().size(); i++) {
                    eleiçõesNome.add(this.getProjectBean().getAtivasVoto().get(i).getTitulo());
                }
                // Se a eleição escolhida pertence as eleições que o user pode votar
                if(el != null && eleiçõesNome.contains(el.getTitulo())) {
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
        } else {
            return LOGIN;
        }
    }

    public void setEleicao(String eleicao) {
        this.eleicao = eleicao;
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
