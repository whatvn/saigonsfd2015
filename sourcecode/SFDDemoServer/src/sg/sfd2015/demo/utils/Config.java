/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package sg.sfd2015.demo.utils;

import java.util.concurrent.ConcurrentHashMap;
import java.io.File;
import java.util.concurrent.ConcurrentHashMap;
import org.apache.commons.configuration.CompositeConfiguration;
import org.apache.commons.configuration.ConfigurationException;
import org.apache.commons.configuration.HierarchicalINIConfiguration;
import org.apache.log4j.Logger;

/**
 *
 * @author hungnguyen
 */
public class Config {

    private static final Logger logger_ = Logger.getLogger(Config.class);
    private static CompositeConfiguration config = null;
    private static ConcurrentHashMap<String, String> _hashConfig = null;

    static {
        init();
    }

    public static void reloadConfig() {
        init();
    }

    synchronized private static void init() {
        String environment = System.getProperty("appenv") == null ? "development" : System.getProperty("appenv");
        String path = System.getProperty("appconfig");
        String pathFile = path + File.separator + environment + ".app.ini";

        System.out.println("pathFile:" + pathFile);
        // init configuration
        config = new CompositeConfiguration();
        _hashConfig = new ConcurrentHashMap<String, String>();
        try {
            config.addConfiguration(new HierarchicalINIConfiguration(pathFile));
        } catch (ConfigurationException ex) {
            logger_.error("Can't load configuration file", ex);
            System.err.println("Bad configuration; unable to start server");
            System.exit(1);
        }
    }

    public static String getParam(String section, String name) {
        String key = section + "." + name;
        if (_hashConfig.contains(key)) {
            return _hashConfig.get(key);
        }
        String value = config.getString(section + "." + name);
        if (value != null) {
            // cache result
            _hashConfig.put(key, value);
        }
        return value;
    }

   
    public static String getParamStr(String section, String name) {
        return getParam(section, name);
    }

    public static Integer getParamInt(String section, String name) {
        String value = getParam(section, name);
        if (value != null) {
            // return data
            return Integer.parseInt(value);
        }
        return -1;
    }
}
