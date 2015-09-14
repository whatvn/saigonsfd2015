/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package model;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import kafka.consumer.ConsumerIterator;
import kafka.consumer.KafkaStream;
import kafka.javaapi.consumer.ConsumerConnector;
import kafka.message.MessageAndMetadata;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 *
 * @author hungnguyen
 */
public class MetricProcessor {

    private static final Logger _logger = LoggerFactory.getLogger(MetricProcessor.class);
    private volatile static MetricProcessor instance = null;

    /**
     *
     */
    protected MetricProcessor() {
    }

    /**
     *
     * @return
     */
    public static MetricProcessor getInstance() {
        if (instance == null) {
            synchronized (MetricProcessor.class) {
                if (instance == null) {
                    instance = new MetricProcessor();
                }
            }
        }
        return instance;
    }

    public void process(String topic, MetricInfomation metricInfomation, ConsumerConnector consumer, DetailCounter counter) {

        Map<String, Integer> topicCountMap = new HashMap<>();
        topicCountMap.put(topic, new Integer(1));
        Map<String, List<KafkaStream<byte[], byte[]>>> consumerMap = consumer.createMessageStreams(topicCountMap);
        KafkaStream<byte[], byte[]> stream = consumerMap.get(topic).get(0);
        ConsumerIterator<byte[], byte[]> it = stream.iterator();

        while (it.hasNext()) {
            MessageAndMetadata<byte[], byte[]> next = it.next();
            try {
                // check if both counter and sum are disabled
                if (!metricInfomation.isCounterEnabled() && !metricInfomation.isSumEnabled()) {
                    counter.increaseHashCounter(topic, 1);
                } else {
                    // Begin complex business
                    String[] msg = new String(next.message()).split("\t");
                    List<MetricDetail> metricDetails = metricInfomation.getMetricDetails();
                    for (MetricDetail metricDetail : metricDetails) {
                        // if counter is enabled & metric type is counter 
                        if (metricInfomation.isCounterEnabled()) {
                            // type is counter
                            if ("counter".equals(metricDetail.getMetricType())) {
                                // if any of metricName or MetricFields is null fall to next if
                                if (metricDetail.getMetricName() != null || metricDetail.getMetricFields() != null) {
                                    String keyCounter = topic;
                                    if (metricDetail.getMetricName() != null) {
                                        keyCounter += "." + metricDetail.getMetricName();
                                    }
                                    // check if enable counter but set counter field = 0
                                    if ("0".equals(metricDetail.getMetricFields())) {
                                        counter.increaseHashCounter(keyCounter, 1);
                                    } else {
                                        // counter field is not 0
                                        String[] countFieldList = metricDetail.getMetricFields().split(",");
                                        for (String s : countFieldList) {
                                            int fieldID = Integer.valueOf(s);
                                            keyCounter += "." + msg[fieldID - 1];
                                        }
                                        // increase counter 
                                        counter.increaseHashCounter(keyCounter, 1);
                                    }
                                } // set counter to true but did not specify counter field and name
                                else {
                                    counter.increaseHashCounter(topic, 1);
                                }
                            }
                            // counter is not enabled
                        } else {
                            counter.increaseHashCounter(topic, 1);
                        }
                        // if metric type is sum
                        if (metricInfomation.isSumEnabled()) {
                            String keySum = topic;
                            // metric type is sum and metric name is not null
                            if ("sum".equals(metricDetail.getMetricType()) && (metricDetail.getMetricName() != null)
                                    && (metricDetail.getMetricValue() != 0)) {
                                String[] sumFieldList = metricDetail.getMetricFields().split(",");
                                Double numberToSum = Double.valueOf(msg[metricDetail.getMetricValue() - 1]);
                                keySum += "." + metricDetail.getMetricName();
                                for (String s : sumFieldList) {
                                    int fieldID = Integer.valueOf(s);
                                    keySum += "." + msg[fieldID - 1];
                                }
                                // check sum.if available
                                if (!"None".equals(metricDetail.getMetricSumIf())) {
                                    int ifFieldNumber = Integer.valueOf(metricDetail.getMetricSumIf().split(":")[0]);
                                    String ifFieldValue = metricDetail.getMetricSumIf().split(":")[1];
                                    if (msg[ ifFieldNumber - 1].equals(ifFieldValue)) {
                                        counter.increaseSumCounter(keySum, numberToSum);
                                    }
                                } else {
                                    // not sum.if: sum every field 
                                    counter.increaseSumCounter(keySum, numberToSum);
                                } //check sum.if
                            }
                        } //check sum enabled
                    } // for through list metricDetails
                } //check if counting more than just lines 
            } catch (NumberFormatException ex) {
                _logger.error("[{}] got error for [{}]", MetricProcessor.class, topic);
            }
        }
    }
}
