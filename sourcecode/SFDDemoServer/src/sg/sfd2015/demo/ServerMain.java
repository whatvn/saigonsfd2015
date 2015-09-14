package sg.sfd2015.demo;

import java.util.concurrent.ArrayBlockingQueue;
import java.util.logging.Level;
import org.apache.log4j.Logger;
import org.eclipse.jetty.server.Connector;
import org.eclipse.jetty.server.Handler;
import org.eclipse.jetty.server.HttpConfiguration;
import org.eclipse.jetty.server.HttpConnectionFactory;
import org.eclipse.jetty.server.Server;
import org.eclipse.jetty.server.ServerConnector;
import org.eclipse.jetty.server.handler.ContextHandler;
import org.eclipse.jetty.server.handler.ContextHandlerCollection;
import org.eclipse.jetty.server.handler.gzip.GzipHandler;
import org.eclipse.jetty.servlet.ServletHandler;
import org.eclipse.jetty.util.thread.QueuedThreadPool;
import sg.sfd2015.demo.controller.ApiHandler;
import sg.sfd2015.demo.utils.Config;
import sg.sfd2015.demo.utils.LogUtil;

/* ------------------------------------------------------------ */
/**
 * A Jetty server with multiple connectors.
 */
public class ServerMain {

    private static final Logger logger_ = Logger.getLogger(ServerMain.class);

    public static void main(String[] args) throws Exception {

        LogUtil.init();

        int serverPort = Integer.valueOf(System.getProperty("server_port"));
        if (serverPort == 0) {
            serverPort = Config.getParamInt("main", "server_port");
            if (serverPort == -1) {
                logger_.error("Cannot get server port's configuration, shutting down...\n");
                System.exit(-1);
            }
        }
        int maxThread = Config.getParamInt("main", "maxthread");
        int minThread = Config.getParamInt("main", "minthread");

        int idleTimeOut = 60000; // jetty default
        int maxQueueSize = 10000;
        ArrayBlockingQueue threadpoolQueue = new ArrayBlockingQueue(maxQueueSize);
        QueuedThreadPool threadPool = new QueuedThreadPool(maxThread, minThread,
                idleTimeOut, threadpoolQueue);
        Server server = new Server(threadPool);
        HttpConfiguration http_config = new HttpConfiguration();
        http_config.setSendServerVersion(false);
        http_config.setOutputBufferSize(32768);
        int cores = Runtime.getRuntime().availableProcessors();
        ServerConnector http = new ServerConnector(server, cores / 2, -1, new HttpConnectionFactory(http_config));
        http.setHost("127.0.0.1");
        http.setPort(serverPort);
        http.setIdleTimeout(30000);
        System.out.println("Number of acceptors: " + http.getAcceptors());
        server.setConnectors(new Connector[]{http});
        ApiHandler apiHandler = new ApiHandler();
        GzipHandler gzipHandler = new GzipHandler();
        ServletHandler handler = new ServletHandler();

        handler.addServletWithMapping(ApiHandler.class, "/*");
        server.setHandler(handler);
        // Start the server
        server.start();
        System.out.println("Server started at port: " + serverPort);
        server.join();
    }
    

}
