package FrontEnd.action;

import java.util.ArrayList;
import java.util.List;

import com.opensymphony.xwork2.ActionSupport;

public class CheckBoxDepartamentosAction extends ActionSupport {

    private List<String> colors;

    private String yourColor;

    public String getYourColor() {
        return yourColor;
    }

    public void setYourColor(String yourColor) {
        this.yourColor = yourColor;
    }

    public CheckBoxDepartamentosAction(){
        colors = new ArrayList<String>();
        colors.add("DEI");
        colors.add("DEEC");
        colors.add("DEM");
        colors.add("DEC");
        colors.add("DF");
        colors.add("DQ");
        colors.add("DARQ");
    }

    public String[] getDefaultColor(){
        return new String [] {"DEI"};
    }

    public List<String> getColors() {
        return colors;
    }


    public String execute() {
        return SUCCESS;
    }

    public String display() {
        return NONE;
    }
}
