package model;

import java.util.Properties;
import kafka.consumer.ConsumerConfig;
import kafka.javaapi.consumer.ConsumerConnector;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import utils.INIConfig;

public class ParseConsumer extends Thread {

    private static final Logger _logger = LoggerFactory.getLogger(ParseConsumer.class);

    private DetailCounter counter = null;
    private final ConsumerConnector consumer;
    private MetricInfomation metricInfomation;
    private String topic;
    private String service_name;
    public static int groupID = 1;

    public ParseConsumer(String topic, MetricInfomation metricInfomation) {
        consumer = kafka.consumer.Consumer.createJavaConsumerConnector(
                createConsumerConfig());
        this.topic = topic;
        this.metricInfomation = metricInfomation;
    }

    public void Shutdown() {
        consumer.shutdown();
    }

    synchronized private static ConsumerConfig createConsumerConfig() {
        Properties props = new Properties();
        props.put("zookeeper.connect", KafkaProperties.zkConnect);
        props.put("group.id", "group" + String.valueOf(groupID));
        props.put("zookeeper.session.timeout.ms", "9000");
        props.put("zookeeper.sync.time.ms", "200");
        props.put("auto.commit.interval.ms", "1000");
        return new ConsumerConfig(props);
    }

    @Override
    public void run() {
        service_name = INIConfig.getParam("main", "service_name", "AmbientCounter");
        counter = new DetailCounter(service_name, this.topic);
        new Thread(counter).start();
        while (true) {
              MetricProcessor.getInstance().process(this.topic, this.metricInfomation, consumer, counter);
        } // run while loops
    } // run function
} // start class
