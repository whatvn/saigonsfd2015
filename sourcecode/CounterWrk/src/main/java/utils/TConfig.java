/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package utils;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;
import java.util.logging.Level;
import org.ini4j.Ini;
import org.ini4j.Options;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 *
 * @author hungnguyen
 */
public class TConfig {

    private static final Logger logger_ = LoggerFactory.getLogger(TConfig.class);
    private static Options opt = null;
    private static Ini ini = null;
    private static ConcurrentHashMap<String, String> _hashConfig = null;

    static {
        init();
    }

    public static void reloadConfig() {
        init();
    }

    synchronized private static void init() {
        try {
            String path = System.getProperty("appconfig");
            String pathFile = path + File.separator + "metrics.ini";
            System.out.println("pathFile:" + pathFile);
            File file = new File(pathFile);
            opt = new Options(file);
            opt.load();
            ini = new Ini(file);
            ini.load();
            _hashConfig = new ConcurrentHashMap<>();
        } catch (FileNotFoundException ex) {
            System.err.println("Configuration file not found " + ex.toString());
            System.exit(2);
        } catch (IOException ex) {
            System.err.println("cannot read metrics.ini file, reason is " + ex.toString());
        }
    }

    public static ArrayList getMetrics() {

        ArrayList<String> listParam = new ArrayList<>();
        ArrayList<String> listTopic = new ArrayList<>();
        Set<String> paramSet = opt.keySet();
        // add all param and kafka topic to its lists
        for (String param : paramSet) {
            String t = param.split("\\.")[0];
            if (!listTopic.contains(t)) {
                listTopic.add(t);
            }
            listParam.add(param);
        }
        // add all value to list
        ArrayList<String> listValue = new ArrayList<>();
        Collection<String> values = opt.values();
        for (String string : values) {
            listValue.add(string);
        }
        for (int i = 0; i < listParam.size(); i++) {
            _hashConfig.put(listParam.get(i), listValue.get(i));
        }

        for (String metric : listTopic) {
            System.out.println(metric);
            String topic = _hashConfig.get(metric + ".scribe_source");
            if (topic == null) {
                logger_.warn("Configuration for [{}] has not scribe source, ignore!!", metric);
                listTopic.remove(metric);
            }
        }
        return listTopic;
    }

    public static Set getSections() {
        return ini.keySet();
    }

    public static String getParam(String section, String name, Object defaultValue) {
        String key = section + "." + name;
        if (_hashConfig.contains(key)) {
            return _hashConfig.get(key);
        }
        String value = opt.get(key);
        if (value == null) {
            logger_.warn("WARNING: Configuration for section key [{}] does not exist, use [{}] as default value",
                    key, String.valueOf(defaultValue));
            value = String.valueOf(defaultValue);
        }
        // cache result  
        _hashConfig.put(key, value);
        return value;
    }

    public static Integer getParamInt(String section, String name, int defaultValue) {
        String value = getParam(section, name, defaultValue);
        // return data
        return Integer.parseInt(value);

    }

    public static boolean getParamBoolean(String section, String name, boolean defaultValue) {
        String value = getParam(section, name, defaultValue);
        // return data
        return Boolean.parseBoolean(value);

    }

    public static void main(String[] args) {
//        [log.click]
//counter=c1,c2,c3
//counter.c1.name =
//counter.c1.field = 
//sum=s1,s2
//sum.s1.name = fdsfsdf
//sum.s1.field = 1,8
//sum.s1.value = 5
//sum.s1.if = 8:win
//sum.s2.name = ffff
       String section = "log.click";
       String sectionCounters = ini.get(section, "counter");
       for (String s : sectionCounters.split(",")) {
           s = s.trim();
           String counterNameParam = "counter" + "." + s + ".name";
           String counterNameValue = ini.get(section, counterNameParam );
           String counterFieldParam = "counter" + "." + s + ".field";
           String counterFieldValue = ini.get(section, counterFieldParam);
           _hashConfig.put(section + "." + counterNameParam, counterNameValue );
           _hashConfig.put(section + "." + counterFieldParam, counterFieldValue);
           
           
       }
       String sectionSums = ini.get(section, "sum");
    }
}
