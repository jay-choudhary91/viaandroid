package com.cryptoserver.composer.models;

import java.io.Serializable;

/**
 * Created by devesh on 20/4/18.
 */

public class metricmodel implements Serializable {

     String MetricTrackKeyName = "";
     String MetricTrackValue = "";
     String DateTime = "";
     String Tag = "";
     boolean isSelected=false;
     boolean isupdated=false;

    public metricmodel(String datetime, String keyname, String keyValue, boolean isSelected) {
        setDateTime(datetime);
        setMetricTrackKeyName(keyname);
        setMetricTrackValue(keyValue);
        setSelected(isSelected);
    }
    public metricmodel() {

    }

    public boolean isIsupdated() {
        return isupdated;
    }

    public void setIsupdated(boolean isupdated) {
        this.isupdated = isupdated;
    }

    public String getTag() {
        return Tag;
    }

    public void setTag(String tag) {
        Tag = tag;
    }

    public String getDateTime() {
        return DateTime;
    }

    public void setDateTime(String dateTime) {
        DateTime = dateTime;
    }

    public metricmodel(String MetricTrackKeyName, String MetricTrackValue, boolean isSelected) {
        this.MetricTrackKeyName = MetricTrackKeyName;
        this.MetricTrackValue = MetricTrackValue;
        this.isSelected = isSelected;
    }

    public String getMetricTrackKeyName() {
        return MetricTrackKeyName;
    }

    public void setMetricTrackKeyName(String metricTrackKeyName) {
        MetricTrackKeyName = metricTrackKeyName;
    }

    public String getMetricTrackValue() {
        return MetricTrackValue;
    }

    public void setMetricTrackValue(String metricTrackValue) {
        MetricTrackValue = metricTrackValue;
    }

    public boolean isSelected() {
        return isSelected;
    }

    public void setSelected(boolean selected) {
        isSelected = selected;
    }
}
