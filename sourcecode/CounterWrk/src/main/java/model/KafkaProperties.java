package model;

import utils.INIConfig;
/**
 *
 * @author hungnguyen
 */
public interface KafkaProperties
{
  final static String zkConnect = INIConfig.getParam("main", "zookeeper", "127.0.0.1:2181");
  final static  String groupId = "2015";
  final static int connectionTimeOut = 100000;
  final static int reconnectInterval = 10000;
  final static String clientId = "saigonsfd";
}