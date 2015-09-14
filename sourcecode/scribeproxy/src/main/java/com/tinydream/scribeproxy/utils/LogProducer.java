/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.tinydream.scribeproxy.utils;

import kafka.producer.KeyedMessage;
import kafka.javaapi.producer.Producer;
import kafka.producer.ProducerConfig;
import java.util.Properties;

/**
 *
 * @author hungnguyen
 */
public class LogProducer {

    public static String kafkaList = "192.168.10.221:9092";
    private Producer<String, String> producer = null;
    private volatile static LogProducer instance = null;

    protected LogProducer() {
        kafkaList = Config.getParam("kafka", "list");
//        kafkaHost = "192.168.10.221";
//        kafkaPort = "9092";
        Properties props = new Properties();
            props.put("producer.type", "async");
            props.put("queue.enqueue.timeout.ms", "-1");
            props.put("batch.num.messages", "1");
            props.put("request.required.acks", "0");
            props.put("queue.buffering.max.messages", "2000");
            props.put("brokerid", "0");
            props.put("metadata.broker.list", kafkaList);
            props.put("serializer.class", "kafka.serializer.StringEncoder");
            props.put("topic.metadata.refresh.interval.ms", "1");
            props.put("partitioner.class", "kafka.producer.DefaultPartitioner");
        ProducerConfig config = new ProducerConfig(props);
        producer = new Producer<String, String>(config);
    }

    public static LogProducer getInstance() {
        if (instance == null) {
            synchronized (LogProducer.class) {
                if (instance == null) {
                    instance = new LogProducer();
                }
            }
        }
        return instance;
    }

    public void produce(String topic, String message) {
        KeyedMessage<String, String> keyedMessage = new KeyedMessage<String, String>(topic, message);
        producer.send(keyedMessage);
    }

    public static void main(String[] args) {
        LogProducer.getInstance().produce("hihi34", "hehe234");
    }

}
