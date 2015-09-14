/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package model;

/**
 *
 * @author hungnguyen
 */
public class MetricDetail {

//    [log.saigonsfd]
//    counter_enabled = true
//    counter=c1,c2,c3
//    counter.c1.name = 
//    counter.c1.field = 
//    sum_enabled = true
//    sum=s1,s2
//    sum.s1.name = fdsfsdf
//    sum.s1.field = 1,8
//    sum.s1.value = 5
//    sum.s1.if = 8:win
//    sum.s2.name = ffff
    private String metricName;
    private String metricType;
    private String metricFields;
    private int metricValue;
    private String metricSumIf;

    public String getMetricName() {
        return metricName;
    }

    public void setMetricName(String metricName) {
        this.metricName = metricName;
    }

    public String getMetricType() {
        return metricType;
    }

    public void setMetricType(String metricType) {
        this.metricType = metricType;
    }

    public String getMetricFields() {
        return metricFields;
    }

    public void setMetricFields(String metricFields) {
        this.metricFields = metricFields;
    }

    public int getMetricValue() {
        return metricValue;
    }

    public void setMetricValue(int metricValue) {
        this.metricValue = metricValue;
    }

    public String getMetricSumIf() {
        return metricSumIf;
    }

    public void setMetricSumIf(String metricSumIf) {
        this.metricSumIf = metricSumIf;
    }

}
