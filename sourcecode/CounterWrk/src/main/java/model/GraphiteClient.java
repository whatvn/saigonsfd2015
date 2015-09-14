package model;

/**
 *
 * @author hungnguyen
 */
import com.netflix.hystrix.HystrixCommand;
import com.netflix.hystrix.HystrixCommandGroupKey;
import java.io.OutputStreamWriter;
import java.io.Writer;
import java.net.Socket;
import java.util.HashMap;
import java.util.Map;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import utils.INIConfig;
import utils.ServiceMonitor;

class graphiteCommand extends HystrixCommand<StringBuffer> {

    private StringBuffer lines;
    private static String graphiteHost;
    private static Integer graphitePort;

    private static final Logger logger = LoggerFactory.getLogger(graphiteCommand.class);

    static {
        ServiceMonitor serviceMonitor = new ServiceMonitor(graphiteCommand.class.getSimpleName());
        serviceMonitor.startMetricsMonitor();
        graphiteHost = INIConfig.getParam("graphite", "host", "127.0.0.1");
        graphitePort = INIConfig.getParamInt("graphite", "port", 2003);
//        graphiteHost2 = "192.168.10.225";
//        graphitePort2 = 2005;
    }

    public graphiteCommand(StringBuffer lines) {
        super(HystrixCommandGroupKey.Factory.asKey(graphiteCommand.class.getName()));
        this.lines = lines;

    }

    @Override
    protected StringBuffer run() throws Exception {
        Socket socket = new Socket(graphiteHost, graphitePort);
        String msg = lines.toString();
        try (Writer writer = new OutputStreamWriter(socket.getOutputStream())) {
            writer.write(msg);
            logger.info("Write [{}] ok to graphite", msg);
            writer.flush();
        }
        return lines;
    }

    @Override
    protected StringBuffer getFallback() {
        logger.error("Cannot put metric to graphite host [{}] : [{}]", graphiteHost, graphitePort);
        return new StringBuffer().append("Carbon server went away, please check!");
    }

}

public class GraphiteClient {

    private static final Logger logger = LoggerFactory.getLogger(GraphiteClient.class);
    private volatile static GraphiteClient instance = null;

    /**
     *
     */
    protected GraphiteClient() {
    }

    /**
     *
     * @return
     */
    public static GraphiteClient getInstance() {
        if (instance == null) {
            synchronized (GraphiteClient.class) {
                if (instance == null) {
                    instance = new GraphiteClient();
                }
            }
        }
        return instance;
    }

    public void logToGraphite(String key, double value) {
        HashMap stats = new HashMap();
        stats.put(key, value);
        logToGraphite(stats);
    }

    public void logToGraphite(HashMap<String, String> stats) {
        if (stats.isEmpty()) {
            return;
        }
        try {
            Long curTimeInSec = System.currentTimeMillis() / 1000;
            StringBuffer lines = new StringBuffer();
            for (Map.Entry entry : stats.entrySet()) {
                lines.append(entry.getKey()).append(" ").append(entry.getValue()).append(" ").append(curTimeInSec).append("\n"); //even the last line in graphite 
            }
//            logToGraphite(lines);
            new graphiteCommand(lines).execute();
        } catch (Exception t) {
            System.out.println(t);
            logger.warn("Can't log to graphite", t.getMessage());
        }
    }
}
//    public static void main(String[] args) throws Exception {
//        String host = "192.168.10.225";
//        int port = 2003;
//        String nodeIdentifier = "tomcat.UI.planck_8080";
//        int i = 900;
//        int metricNumber;
//        while (true) {
//            if (i % 2 > 0) {
//                metricNumber = (i / 3);
//            } else {
//                metricNumber = i;
//            }
//            HashMap stats = new HashMap();
//            stats.put("memcache_calls", metricNumber);
//            stats.put("num_threads", metricNumber / 2);
//            GraphiteClient graphiteClient = new GraphiteClient();
//            graphiteClient.setGraphiteHost(host);
//            graphiteClient.setGraphitePort(port);
//            graphiteClient.logToGraphite(nodeIdentifier, stats);
//            Thread.sleep(1000);
//            ++i;
//        }

