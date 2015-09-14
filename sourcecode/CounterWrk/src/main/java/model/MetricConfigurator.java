/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package model;

import com.sun.org.apache.xerces.internal.impl.xpath.regex.RegularExpression;
import java.util.ArrayList;
import utils.INIConfig;

/**
 *
 * @author hungnguyen
 */
public class MetricConfigurator {

    private volatile static MetricConfigurator instance = null;

    protected MetricConfigurator() {

    }

    /**
     *
     * @return
     */
    public static MetricConfigurator getInstance() {
        if (instance == null) {
            synchronized (MetricConfigurator.class) {
                if (instance == null) {
                    instance = new MetricConfigurator();
                }
            }

        }
        return instance;
    }

    public MetricInfomation get(String section) {
        MetricInfomation metricInformation = new MetricInfomation();
        ArrayList<MetricDetail> listMetricDetail = new ArrayList<>();
        try {
            if (INIConfig.isConfigured(section)) {
                metricInformation.setCounterEnabled(INIConfig.getParamBoolean(section, "counter_enabled", false));
                String counterParams = INIConfig.getParam(section, "counter", "None");
                if (!"None".equals(counterParams)) {
                    String[] counterParamList = counterParams.split(",");
                    for (String counter : counterParamList) {
                        MetricDetail metricDetail = new MetricDetail();
                        metricDetail.setMetricName(INIConfig.getParam(section, "counter." + counter + ".name", null));
                        metricDetail.setMetricType("counter");
                        metricDetail.setMetricFields(INIConfig.getParam(section, "counter." + counter + ".groupby", "0"));
                        listMetricDetail.add(metricDetail);
                    }
                } else {
                    metricInformation.setCounterEnabled(false);
                }

                metricInformation.setSumEnabled(INIConfig.getParamBoolean(section, "sum_enabled", false));
                if (metricInformation.isSumEnabled()) {
                    String sumParams = INIConfig.getParam(section, "sum", "None");
                    if (!"None".equals(sumParams)) {
                        String[] sumParamList = sumParams.split(",");
                        for (String sum : sumParamList) {
                            MetricDetail metricDetail = new MetricDetail();
                            metricDetail.setMetricName(INIConfig.getParam(section, "sum." + sum + ".name", null));
                            metricDetail.setMetricType("sum");
                            metricDetail.setMetricValue(INIConfig.getParamInt(section, "sum." + sum + ".value", 0));
                            metricDetail.setMetricFields(INIConfig.getParam(section, "sum." + sum + ".groupby", "None"));
                            metricDetail.setMetricSumIf(INIConfig.getParam(section, "sum." + sum + ".if", "None"));
                            listMetricDetail.add(metricDetail);
                        }
                    } else {
                        metricInformation.setSumEnabled(false);
                    }
                }
                metricInformation.setMetricDetails(listMetricDetail);
                return metricInformation;
            }
            else {
                return null;
            }
        } catch (Exception ex) {
            System.err.println("Configuration for " + section + " got error." + ex.toString());
            return null;
        }
    }   
    
    public static void main(String[] args) {
        String a = "THis is a samsung";
        String p = "[samsung]";
        
        if (a.matches(p)) {
            System.out.println("a");
        }
    }

}
