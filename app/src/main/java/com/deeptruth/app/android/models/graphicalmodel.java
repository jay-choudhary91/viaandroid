package com.deeptruth.app.android.models;

import java.io.Serializable;

public class graphicalmodel implements Serializable {

    String graphicalkeyname="";
    String graphicalvalue="";

    public graphicalmodel(String graphicalkey, String graphicalvalue) {
        this.graphicalkeyname = graphicalkey;
        this.graphicalvalue = graphicalvalue;
    }

    public String getGraphicalkeyname() {
        return graphicalkeyname;
    }

    public void setGraphicalkeyname(String graphicalkeyname) {
        this.graphicalkeyname = graphicalkeyname;
    }

    public String getGraphicalvalue() {
        return graphicalvalue;
    }

    public void setGraphicalvalue(String graphicalvalue) {
        this.graphicalvalue = graphicalvalue;
    }
}
