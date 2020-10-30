package cn.zmk.storm;

import org.apache.storm.Config;
import org.apache.storm.LocalCluster;
import org.apache.storm.StormSubmitter;
import org.apache.storm.generated.AlreadyAliveException;
import org.apache.storm.topology.TopologyBuilder;

/**
 * @Author: zhangmingkun3
 * @Description:
 * @Date: 2020/10/30 18:02
 */
public class MessageTopology {


    public static void main(String[] args) throws Exception {
        //组装topology
        TopologyBuilder topologyBuilder = new TopologyBuilder();
        topologyBuilder.setSpout("RabbitmqSpout", new RabbitmqSpout());
        topologyBuilder.setBolt("FilterBolt", new FilterBolt()).shuffleGrouping("RabbitmqSpout");

        Config conf = new Config ();
        try {
            if (args.length > 0) {
                StormSubmitter.submitTopology(args[0], conf, topologyBuilder.createTopology());
            } else {
                LocalCluster localCluster = new LocalCluster();
                localCluster.submitTopology("messageTopology", conf, topologyBuilder.createTopology());
            }
        } catch (AlreadyAliveException e) {
            e.printStackTrace();
        }
    }

}