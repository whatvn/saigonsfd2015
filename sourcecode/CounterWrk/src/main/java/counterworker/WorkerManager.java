
package main.java.counterworker;

import java.util.ArrayList;
import java.util.List;
import model.KafkaProperties;
import model.MetricConfigurator;
import model.MetricInfomation;
import model.ParseConsumer;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import utils.INIConfig;
import utils.LogUtil;

/**
 *
 * @author hungnguyen
 */
public class WorkerManager implements KafkaProperties {

    private static final Logger _logger = LoggerFactory.getLogger(WorkerManager.class);

    public static void main(String[] args) {
        ArrayList<ParseConsumer> consumers = new ArrayList<>();

        String logCategories = INIConfig.getParam("main", "log_category", null);
        LogUtil.init();
        if (logCategories != null) {
            String[] topicList = logCategories.split(",");
            for (String topic : topicList) {
                topic = topic.trim();
                System.out.println("Topic: " + topic);
                MetricInfomation metricInfomation = MetricConfigurator.getInstance().get(topic);
                if (metricInfomation != null) {
                    ParseConsumer consumer = new ParseConsumer(topic, metricInfomation);
                    consumers.add(consumer);
                } else {
                    // Configuration for this topic was not set, or is not set properly 
                    _logger.error("ERROR: Configuration for [{}] is not configured or is set to wrong options\n"
                            + "[{}] still run without processing this topic", topic, WorkerManager.class);
                }
            }
        }
        ShutdownWorkerThread obj = new ShutdownWorkerThread(consumers);
        Runtime.getRuntime().addShutdownHook(obj);
        for (ParseConsumer c : consumers) {
            new Thread(c).start();
        }

    }
}

class ShutdownWorkerThread extends Thread {

    private List<ParseConsumer> consumers;
    private static final Logger _logger = LoggerFactory.getLogger(ShutdownWorkerThread.class);

    public ShutdownWorkerThread(List c) {
        this.consumers = c;
    }

    @Override
    public void run() {
        _logger.info("Waiting for shut down!");
        try {
            for (ParseConsumer consumer : consumers) {
                consumer.Shutdown();
            }

        } catch (Exception ex) {
            _logger.error(ex.getMessage());
        }
        _logger.info("Server shutted down!");
    }
}


