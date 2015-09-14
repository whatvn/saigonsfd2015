/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.tinydream.scribeproxy.services;

import java.util.List;
import scribe.thrift.LogEntry;
import scribe.thrift.ResultCode;
import com.tinydream.scribeproxy.utils.LogProducer;
import com.tinydream.scribeproxy.utils.Config;

/**
 *
 * @author hungnguyen
 */
public class ScriberProxyHandler implements scribe.thrift.scribe.Iface {

    private static final String topic = Config.getParamStr("kafka", "topic");

    @Override
    synchronized public ResultCode Log(List<LogEntry> messages) {
        try {
            for (LogEntry logEntry : messages) {
                System.out.println(logEntry.message);
                LogProducer.getInstance().produce(topic, logEntry.message);
            }
        } finally {
            return ResultCode.OK;
        }
    }
}
