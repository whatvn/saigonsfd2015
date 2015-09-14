
package sg.sfd2015.demo.utils.scribe;

import com.ethicconsultant.common.ThriftPool;
import com.ethicconsultant.common.ThriftPoolableFactory;
import org.apache.commons.pool2.impl.GenericObjectPoolConfig;
import org.apache.thrift.TException;
import org.apache.thrift.protocol.TBinaryProtocol;
import org.apache.thrift.protocol.TProtocol;
import org.apache.thrift.transport.TTransport;
import scribe.thrift.scribe.Client;

/**
 *
 * @author hungnguyen
 */
public class ScribeClient {

    private volatile static ScribeClient instance = null;
    private static ThriftPool thriftPool;

    public static ScribeClient getInstance(String host, int port, int timeOut) {
        if (instance == null) {
            synchronized (ScribeClient.class) {
                if (instance == null) {
                    instance = new ScribeClient(host, port, timeOut);
                }
            }
        }
        return instance;
    }

    public ScribeClient(String host, int port, int timeOut) {
        thriftPool = new ThriftPool();
        // timeout is time taking to make connection to thrift server
        ThriftPoolableFactory thriftPoolableFactory = new ThriftPoolableFactory(host, port, timeOut);
        GenericObjectPoolConfig poolConfig = new GenericObjectPoolConfig();
        // set max connection in the pool
        // usually < 20 is enough

        poolConfig.setMaxTotal(10);
        // should setMaxIdle to max connection
        // to persistent all connections in the pool
        poolConfig.setMaxIdle(10);
        poolConfig.setMaxWaitMillis(500);
        poolConfig.setTestWhileIdle(true);
        thriftPool.initPool(poolConfig, thriftPoolableFactory);
    }

    public Client  getClient() {
        TTransport resource = thriftPool.getResource();
        TProtocol protocol = new TBinaryProtocol(resource);
        return new Client(protocol);
    }

    public void returnClient(Client client) {
        TProtocol inputProtocol = client.getOutputProtocol();
        TTransport transport = inputProtocol.getTransport();
        try {
            thriftPool.returnResource(transport);
        } catch (Exception ex) {
            System.out.println("Cannot return");
            thriftPool.returnBrokenResource(transport);
        }
    }

    public static void main(String[] args) throws TException {
        ScribeClient myInstance = ScribeClient.getInstance("192.168.1.135", 9090, 1000);
        int i = 0;
        while (i < 1000) {
            Client client = myInstance.getClient();
        }

    }
}
