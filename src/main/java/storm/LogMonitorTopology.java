package storm;

import org.apache.storm.Config;
import org.apache.storm.LocalCluster;
import org.apache.storm.StormSubmitter;
import org.apache.storm.kafka.*;
import org.apache.storm.spout.SchemeAsMultiScheme;
import org.apache.storm.topology.TopologyBuilder;
import org.apache.storm.tuple.Fields;

public class LogMonitorTopology {

    private static String zkConnStr = "nn1-ha:2181,nn2:2181,nn2-ha:2181";
    private static String topicName = "monitor_realtime_javaxy";

    public static void main(String[] args) throws Exception {
        BrokerHosts hosts = new ZkHosts(zkConnStr);
        SpoutConfig spoutConfig = new SpoutConfig(hosts, topicName, "/" + topicName, "kafkaSpout");
        spoutConfig.scheme = new SchemeAsMultiScheme(new StringScheme());
        KafkaSpout kafkaSpout = new KafkaSpout(spoutConfig);

        TopologyBuilder topologyBuilder = new TopologyBuilder();
        topologyBuilder.setSpout("kafkaSpout", kafkaSpout, 3);
        topologyBuilder.setBolt("filterBolt", new FilterBolt(), 3).shuffleGrouping("kafkaSpout");
        topologyBuilder.setBolt("prepareBolt1", new PrepareRecordBolt(), 2).fieldsGrouping("filterBolt", new Fields("appId"));
        topologyBuilder.setBolt("saveBolt", new SaveBolt(), 2).shuffleGrouping("prepareBolt1");

        Config config = new Config();
        config.setNumWorkers(3);
        if(args.length > 0 ){
            StormSubmitter.submitTopology(args[0], config, topologyBuilder.createTopology());
        } else {
            LocalCluster localCluster = new LocalCluster();
            localCluster.submitTopology(LogMonitorTopology.class.getSimpleName(), config, topologyBuilder.createTopology());
        }

    }
}
