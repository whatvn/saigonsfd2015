package model;

import com.google.common.util.concurrent.AtomicDouble;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicLong;
import java.util.concurrent.locks.ReentrantLock;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import utils.INIConfig;

/**
 * @author hungnguyen
 *
 */
public class DetailCounter implements Runnable {

    private static final Logger _logger = LoggerFactory.getLogger(DetailCounter.class);
    public static int INTERVAL_TIME = 1000;
    private ConcurrentHashMap<String, AtomicLong> hashCounter;
    private ConcurrentHashMap<String, AtomicDouble> hashSum;
    private AtomicLong realtime_counter;
    private ReentrantLock _lock;
    public String serviceName;
    private String topic;

        
    private void putCounter(String key, AtomicLong value) {
        _lock.lock();
        try {
            if (!hashCounter.containsKey(key)) {
                hashCounter.put(key, value);
            }
        } finally {
            _lock.unlock();
        }
    }

    public void increaseHashCounter(String key, int value) {
        _lock.lock();
        try {
            putCounter(key, new AtomicLong(0));
            hashCounter.get(key).addAndGet(value);
        } finally {
            _lock.unlock();
        }
    }

    public void resetHashCounter(String key) {
        _lock.lock();
        try {
            putCounter(key, new AtomicLong(0));
            hashCounter.get(key).set(0);
        } finally {
            _lock.unlock();
        }
    }

    private void putSumCounter(String key, AtomicDouble value) {
        _lock.lock();
        try {
            if (!hashSum.containsKey(key)) {
                hashSum.put(key, value);
            }
        } finally {
            _lock.unlock();
        }
    }

    public void increaseSumCounter(String key, Double value) {
        _lock.lock();
        try {
            putSumCounter(key, new AtomicDouble(0));
            hashSum.get(key).addAndGet(value);
        } finally {
            _lock.unlock();
        }
    }

    public void resetSumCounter(String key) {
        _lock.lock();
        try {
            putSumCounter(key, new AtomicDouble(0));
            hashSum.get(key).set(0);
        } finally {
            _lock.unlock();
        }
    }

    public DetailCounter(String serviceName, String topic) {
        this.hashCounter = new ConcurrentHashMap<>();
        this.hashSum = new ConcurrentHashMap<>();
        this.realtime_counter = new AtomicLong(0);
        this._lock = new ReentrantLock();
        INTERVAL_TIME = INIConfig.getParamInt("main", "interval", 5000);
        _logger.info("Detail Counter for topic: " + topic + " started!");
        this.serviceName = serviceName;
        this.topic = topic;
    }

    @Override
    synchronized public void run() {
        for (;;) {
            try {
                Set<Map.Entry<String, AtomicLong>> counterSet = hashCounter.entrySet();
                Set<Map.Entry<String, AtomicDouble>> sumSet = hashSum.entrySet();
                for (Map.Entry<String, AtomicLong> counterEntry : counterSet) {
                    GraphiteClient.getInstance().logToGraphite(serviceName + counterEntry.getKey(), counterEntry.getValue().get());
//                    _logger.info("Sent data to graphite [{}] with value: [{}]", counterEntry.getKey(), counterEntry.getValue().get());
                    resetHashCounter(counterEntry.getKey());
                }
                for (Map.Entry<String, AtomicDouble> sumEntry : sumSet) {
                    GraphiteClient.getInstance().logToGraphite(serviceName + sumEntry.getKey(), sumEntry.getValue().get());
//                    _logger.info("Sent data to graphite [{}] with value: [{}]", sumEntry.getKey(), sumEntry.getValue().get());
                    resetSumCounter(sumEntry.getKey());
                }
                Thread.sleep(INTERVAL_TIME);
            } catch (InterruptedException | NullPointerException ie) {
                _logger.error(ie.toString());
            } catch (Exception ex) {
                _logger.error("Detail counter for topic [{}] got exception [{}]", topic, ex.toString());
            }
        }
    }

}
