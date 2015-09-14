/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.tinydream.scribeproxy.services;

import com.tinydream.scribeproxy.utils.Config;
import org.apache.thrift.server.TServer;
import org.apache.thrift.server.TThreadPoolServer;
import org.apache.thrift.server.TThreadPoolServer.Args;
import org.apache.thrift.transport.*;

/**
 * server thread pool
 *
 * @author hungnv
 */
public class ScriberProxyThreadPool {

    public ScriberProxyThreadPool() {
    }

    public void init() {
        // load config
        int port = Config.getParamInt("system", "port_thrift");
        int minThread = Config.getParamInt("system", "minthread");
        int maxThread = Config.getParamInt("system", "maxthread");
        int stopTimeoutVal = Config.getParamInt("system", "timeout"); 
        System.out.println("port:" + port + " minthread:" + minThread + " maxthread" + maxThread + "stopTimeoutVal:" + stopTimeoutVal);
        try {
            TServerTransport serverTransport = new TServerSocket(port);
            ScriberProxyHandler scriberProxyHandler = new ScriberProxyHandler();
            scribe.thrift.scribe.Processor processor = new scribe.thrift.scribe.Processor(scriberProxyHandler);
            // Use this for a multithreaded server
            Args options = new TThreadPoolServer.Args(serverTransport);
            options.processor(processor);
            options.transportFactory(new TFramedTransport.Factory());
            options.minWorkerThreads = minThread;
            options.maxWorkerThreads = maxThread;
            TServer server = new TThreadPoolServer(options);
            System.out.println("Starting the server thrift port:" + port);
            server.serve();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

}
