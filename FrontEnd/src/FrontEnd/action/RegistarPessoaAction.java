package FrontEnd.action;

import FrontEnd.model.ProjectBean;
import RMI.Pessoa;
import com.opensymphony.xwork2.ActionSupport;
import org.apache.struts2.interceptor.SessionAware;

import java.io.IOException;
import java.util.Map;

public class RegistarPessoaAction extends ActionSupport implements SessionAware {
    private static final long serialVersionUID = 4L;
    private Map<String, Object> session;
    private String tipo = null, nome = null, usernameRegisto = null, passwordRegisto = null, morada = null, validade = null, grupo = null;
    private Integer phone = null, ccRegisto = null;

    @Override
    public String execute() throws IOException {
        if(session.get("loggedin").equals("admin") == true) {
            if(this.tipo != null && this.nome != null && this.usernameRegisto != null && this.passwordRegisto != null
                && this.morada != null && this.validade != null && this.grupo != null && this.phone != null && this.ccRegisto != null)
            {
                if(this.getProjectBean().verificaCc(this.ccRegisto) || this.getProjectBean().verificaUsername(this.usernameRegisto)) {
                    return ERROR;
                }

                this.getProjectBean().setTipo(this.tipo);
                this.getProjectBean().setNome(this.nome);
                this.getProjectBean().setUsernameRegisto(this.usernameRegisto);
                this.getProjectBean().setPasswordRegisto(this.passwordRegisto);
                this.getProjectBean().setMorada(this.morada);
                this.getProjectBean().setValidade(this.validade);
                this.getProjectBean().setGrupo(this.grupo);
                this.getProjectBean().setPhone(this.phone);
                this.getProjectBean().setCcRegisto(this.ccRegisto);

                this.getProjectBean().registarPessoa();

                session.remove("searchPessoa");
                return SUCCESS;
            }
        }

        return LOGIN;
    }

    public void setTipo(String tipo) {
        this.tipo = tipo;
    }

    public void setNome(String nome) {
        this.nome = nome;
    }

    public void setUsernameRegisto(String usernameRegisto) {
        this.usernameRegisto = usernameRegisto;
    }

    public void setPasswordRegisto(String passwordRegisto) {
        this.passwordRegisto = passwordRegisto;
    }

    public void setMorada(String morada) {
        this.morada = morada;
    }

    public void setValidade(String validade) {
        this.validade = validade;
    }

    public void setGrupo(String grupo) {
        this.grupo = grupo;
    }

    public void setPhone(int phone) {
        this.phone = phone;
    }

    public void setCcRegisto(Integer ccRegisto) {
        this.ccRegisto = ccRegisto;
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
