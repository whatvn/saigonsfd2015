/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package model;

import java.util.List;

/**
 *
 * @author hungnguyen
 */
public class MetricInfomation {
    
    private boolean counterEnabled;
    private boolean sumEnabled;    
    private List<MetricDetail> metricDetails;


    public boolean isCounterEnabled() {
        return counterEnabled;
    }

    public void setCounterEnabled(boolean counterEnabled) {
        this.counterEnabled = counterEnabled;
    }

    public boolean isSumEnabled() {
        return sumEnabled;
    }

    public void setSumEnabled(boolean sumEnabled) {
        this.sumEnabled = sumEnabled;
    }

    public List<MetricDetail> getMetricDetails() {
        return metricDetails;
    }

    public void setMetricDetails(List<MetricDetail> metricDetails) {
        this.metricDetails = metricDetails;
    }
    
    
}
