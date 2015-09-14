/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package utils;

import java.io.File;
import java.io.IOException;
import java.util.concurrent.ConcurrentHashMap;
import org.slf4j.Logger;
import org.ini4j.Ini;
import org.slf4j.LoggerFactory;

/**
 *
 * @author hungnguyen
 */
public class INIConfig {

    
    private static ConcurrentHashMap<String, String> _hashConfig = null;
    private static final Logger _logger = LoggerFactory.getLogger(Config.class);

    private static Ini config = null;

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
        config = new Ini();
        File iniFile = new File(pathFile);
        _hashConfig = new ConcurrentHashMap<>();
        if (iniFile.exists()) {
            try {
                config.setFile(iniFile);
                config.load();
            } catch (IOException ex) {
                _logger.error("Configuration file error: " + ex.getMessage());
            }
        }
    }

    public static String getParam(String section, String name, Object defaultValue) {
        String key = section + "." + name;
        if (_hashConfig.contains(key)) {
            return _hashConfig.get(key);
        }
        String value = config.get(section, name);
        if (value == null) {
            _logger.error("WARNING: Configuration for section key [{}] does not exist, use [{}] as default value",
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

    public static boolean isConfigured(String section) {
        for (String s : config.keySet()) {
            if (s.equalsIgnoreCase(section)) {
                return true;
            }
        }
        return false;
    }
}
