package com.cryptoserver.composer.models;

import java.io.Serializable;

/**
 * Created by devesh on 20/4/18.
 */

public class MetricModel implements Serializable {

     String MetricTrackKeyName = "";
     String MetricTrackValue = "";
     String DateTime = "";
     String Tag = "";
     boolean isSelected=false;

    public MetricModel(String datetime, String keyname, String keyValue, boolean isSelected) {
        setDateTime(datetime);
        setMetricTrackKeyName(keyname);
        setMetricTrackValue(keyValue);
        setSelected(isSelected);
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

    public MetricModel(String MetricTrackKeyName, String MetricTrackValue, boolean isSelected) {
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
