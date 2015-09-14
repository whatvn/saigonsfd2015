/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package utils;

import com.netflix.hystrix.HystrixCommandKey;
import com.netflix.hystrix.HystrixCommandMetrics;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 *
 * @author hungnguyen
 */
public class ServiceMonitor {

    private static final Logger logger = LoggerFactory.getLogger(ServiceMonitor.class);
    private final String command;

    public ServiceMonitor(String command) {
        this.command = command;
    }

    public  void startMetricsMonitor() {
        Thread t = new Thread(new Runnable() {
            @Override
            public void run() {
                while (true) {
                    try {
                        Thread.sleep(5000);
                    } catch (InterruptedException e) {
                        // ignore
                    }
                    HystrixCommandMetrics graphiteCommandMetrics = HystrixCommandMetrics.getInstance(HystrixCommandKey.Factory.asKey(command));
                    // print out metrics
                    StringBuilder out = new StringBuilder();
                    out.append(
                            "\n");
                    out.append(
                            "#####################################################################################").append("\n");
                    out.append(
                            "# GraphiteCommand : ").append(getStatsStringFromMetrics(graphiteCommandMetrics)).append("\n");
                    out.append(
                            "#####################################################################################").append("\n");
                    logger.info(out.toString());
                }
            }
        }
        );
        t.setDaemon(true);
        t.start();
    }

    private  String getStatsStringFromMetrics(HystrixCommandMetrics metrics) {
        StringBuilder m = new StringBuilder();
        if (metrics != null) {
            HystrixCommandMetrics.HealthCounts health = metrics.getHealthCounts();
            m.append("Requests: ").append(health.getTotalRequests()).append(" ");
            m.append("Errors: ").append(health.getErrorCount()).append(" (").append(health.getErrorPercentage()).append("%)   ");
            m.append("Mean: ").append(metrics.getExecutionTimePercentile(50)).append(" ");
            m.append("75th: ").append(metrics.getExecutionTimePercentile(75)).append(" ");
            m.append("90th: ").append(metrics.getExecutionTimePercentile(90)).append(" ");
            m.append("99th: ").append(metrics.getExecutionTimePercentile(99)).append(" ");
        }
        return m.toString();

    }

}
