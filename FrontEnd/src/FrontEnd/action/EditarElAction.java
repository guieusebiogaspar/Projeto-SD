package FrontEnd.action;

import FrontEnd.model.ProjectBean;
import RMI.Eleição;
import com.opensymphony.xwork2.ActionSupport;
import org.apache.struts2.interceptor.SessionAware;

import java.io.IOException;
import java.util.Map;

public class EditarElAction extends ActionSupport implements SessionAware{
    private static final long serialVersionUID = 4L;
    private Map<String, Object> session;
    private Integer diaInicio = null, mesInicio = null, anoInicio = null, horaInicio = null, minutoInicio = null, diaFim = null, mesFim = null, anoFim = null, horaFim = null, minutoFim = null;
    private String titulo = null, descricao = null;
    private String adicionaDep = null, removeDep = null, adicionaLista = null, removeLista = null, adicionaMesa = null, removeMesa = null;

    @Override
    public String execute() throws IOException {
        if (session.get("loggedin").equals("admin") == true) {
            Eleição el = (Eleição) session.get("searchEleicao");

            // atualiza os dados introduzidos da eleição

            if(this.diaInicio != null && this.mesInicio != null && this.anoInicio != null && this.horaInicio != null && this.minutoInicio != null) {
                this.getProjectBean().setDataInicio(this.diaInicio, this.mesInicio, this.anoInicio, this.horaInicio, this.minutoInicio);
                this.getProjectBean().atualizaDataInicio(el.getTitulo());
            }
            if( this.diaFim != null && this.mesFim != null && this.anoFim != null && this.horaFim != null && this.minutoFim != null) {
                this.getProjectBean().setDataFim(this.diaFim, this.mesFim, this.anoFim, this.horaFim, this.minutoFim);
                this.getProjectBean().atualizaDataFim(el.getTitulo());
            }

            if(this.descricao != null && !this.descricao.equals("")) {
                this.getProjectBean().setDescricao(this.descricao);
                this.getProjectBean().atualizaDescricao(el.getTitulo());
            }

            if(this.adicionaDep != null && !this.adicionaDep.equals("")) {
                this.getProjectBean().setGrupoVotar(this.adicionaDep);

                this.getProjectBean().adicionaDepartamentoEleicao(el.getTitulo());
            }

            if(this.removeDep != null && !this.removeDep.equals("")) {
                this.getProjectBean().setGrupoVotar(this.removeDep);
                this.getProjectBean().removeDepartamentoEleicao(el.getTitulo());
            }

            if(this.adicionaMesa != null && !this.adicionaMesa.equals("")) {
                this.getProjectBean().setMesa(this.adicionaMesa);

                this.getProjectBean().adicionaMesaEleicao(el.getTitulo());
            }

            if(this.removeMesa != null && !this.removeMesa.equals("")) {
                this.getProjectBean().setMesa(this.removeMesa);
                this.getProjectBean().removeMesaEleicao(el.getTitulo());
            }

            // Verifica se não existe nenhuma eleição com o mesmo titulo
            if(this.titulo != null && !this.titulo.equals("")) {
                this.getProjectBean().setTitulo(this.titulo);
                if (this.getProjectBean().verificaEleicao()) {
                    return ERROR;
                }
                this.getProjectBean().atualizaTitulo(el.getTitulo());
            }



            session.remove("searchEleicao");
            return SUCCESS;
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

    public void setAdicionaMesa(String adicionaMesa){
        this.adicionaMesa = adicionaMesa;
    }

    public void setRemoveMesa(String removeMesa){
        this.removeMesa = removeMesa;
    }

    public void setAdicionaDep(String adicionaDep) {
        this.adicionaDep = adicionaDep;
    }

    public void setRemoveDep(String removeDep) {
        this.removeDep = removeDep;
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
