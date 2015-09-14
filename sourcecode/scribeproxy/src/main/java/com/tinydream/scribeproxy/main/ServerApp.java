/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package main.java.com.tinydream.scribeproxy.main;

import com.tinydream.scribeproxy.services.ScriberProxyThreadPool;
import com.tinydream.scribeproxy.utils.LogUtil;

/**
 *	
 * @author hungnv
 */
public class ServerApp {

    public static void main(String[] args) {
        // init log4j
        LogUtil.init();
        ScriberProxyThreadPool thriftServer = new ScriberProxyThreadPool();
        thriftServer.init();
    }
  
}
