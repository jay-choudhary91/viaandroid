package com.cryptoserver.composer.models;

import java.io.Serializable;
import java.util.ArrayList;

/**
 * Created by devesh on 20/4/18.
 */

public class arraycontainer implements Serializable {

    ArrayList<metricmodel> metricItemArraylist = new ArrayList<>();
    boolean isupdated=false;

    public arraycontainer(ArrayList<metricmodel> metricItemArraylist)
    {
        setMetricItemArraylist(metricItemArraylist);
    }

    public ArrayList<metricmodel> getMetricItemArraylist() {
        return metricItemArraylist;
    }

    public void setMetricItemArraylist(ArrayList<metricmodel> metricItemArraylist) {
        this.metricItemArraylist = metricItemArraylist;
    }

    public boolean isIsupdated() {
        return isupdated;
    }

    public void setIsupdated(boolean isupdated) {
        this.isupdated = isupdated;
    }


}
