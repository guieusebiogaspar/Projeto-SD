package FrontEnd.action;

import FrontEnd.model.ProjectBean;
import RMI.Lista;
import RMI.Pessoa;
import com.opensymphony.xwork2.ActionSupport;
import org.apache.struts2.interceptor.SessionAware;

import java.io.IOException;
import java.util.Map;

public class CriarElAction extends ActionSupport implements SessionAware{
    private static final long serialVersionUID = 4L;
    private Map<String, Object> session;
    private Integer diaInicio = null, mesInicio = null, anoInicio = null, horaInicio = null, minutoInicio = null, diaFim = null, mesFim = null, anoFim = null, horaFim = null, minutoFim = null;
    private String titulo = null, descricao = null, opcao = null, mesa = null, grupoVotar = null;

    @Override
    public String execute() throws IOException {
        if(session.get("loggedin").equals("admin") == true) {
            if(this.diaInicio != null && this.mesInicio != null && this.anoInicio != null && this.horaInicio != null
                    && this.minutoInicio != null && this.diaFim != null && this.mesFim != null && this.anoFim != null
                    && this.horaFim != null && this.minutoFim != null && this.titulo != null && this.descricao != null
                    && this.mesa != null && this.opcao != null && this.grupoVotar != null)
            {

                if(this.getProjectBean().verificaEleicao(this.titulo)) {
                    return ERROR;
                }

                this.getProjectBean().setDataInicio(this.diaInicio, this.mesInicio, this.anoInicio, this.horaInicio, this.horaFim);
                this.getProjectBean().setDataFim(this.diaFim, this.mesFim, this.anoFim, this.horaFim, this.minutoFim);
                this.getProjectBean().setTitulo(this.titulo);
                this.getProjectBean().setDescricao(this.descricao);
                this.getProjectBean().setMesa(this.mesa);
                this.getProjectBean().adicionaMesa();
                this.getProjectBean().setOpcao(this.opcao);
                this.getProjectBean().setGrupoVotar(this.grupoVotar);
                this.getProjectBean().adicionaGrupo();

                this.getProjectBean().criarEleicao();

                session.remove("searchEleicao");
                return SUCCESS;
            } else {
                return ERROR;
            }
        }

        return LOGIN;
    }


    public void setDiaInicio(Integer diaInicio) {
        this.diaInicio = diaInicio;
    }

    public void setMesInicio(Integer mesInicio) {
        this.mesInicio = mesInicio;
    }

    public void setAnoInicio(Integer anoInicio) {
        this.anoInicio = anoInicio;
    }

    public void setHoraInicio(Integer horaInicio) {
        this.horaInicio = horaInicio;
    }

    public void setMinutoInicio(Integer minutoInicio) {
        this.minutoInicio = minutoInicio;
    }

    public void setDiaFim(Integer diaFim) {
        this.diaFim = diaFim;
    }

    public void setMesFim(Integer mesFim) {
        this.mesFim = mesFim;
    }

    public void setAnoFim(Integer anoFim) {
        this.anoFim = anoFim;
    }

    public void setHoraFim(Integer horaFim) {
        this.horaFim = horaFim;
    }

    public void setMinutoFim(Integer minutoFim) {
        this.minutoFim = minutoFim;
    }

    public void setTitulo(String titulo) {
        this.titulo = titulo;
    }

    public void setDescricao(String descricao) {
        this.descricao = descricao;
    }

    public void setMesa(String mesa){
        this.mesa = mesa;
    }

    public void setOpcao(String opcao) {
        this.opcao = opcao;
    }

    public void setGrupoVotar(String grupoVotar) {
        this.grupoVotar = grupoVotar;
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
