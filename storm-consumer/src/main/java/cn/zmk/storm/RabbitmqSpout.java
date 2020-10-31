package cn.zmk.storm;

import com.rabbitmq.client.*;
import org.apache.storm.spout.SpoutOutputCollector;
import org.apache.storm.task.TopologyContext;
import org.apache.storm.topology.IRichSpout;
import org.apache.storm.topology.OutputFieldsDeclarer;
import org.apache.storm.tuple.Fields;
import org.apache.storm.tuple.Values;
import org.apache.storm.utils.Utils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;
import java.util.concurrent.TimeoutException;

/**
 * @Author: zhangmingkun3
 * @Description: 定义数据源
 * RabbitmqSpout继承自org.apache.storm.topology.IRichSpout接口，实现对应的方法：open(),close(),activate(),deactivate(),nextTuple(),ack(),fail()。
    unconfirmedMap对象存储了MQ所有发射出去等待确认的消息唯一标识deliveryTag，当storm系统回调ack、fail方法后进行MQ消息的成功确认或失败重回队列操作（Storm系统回调方法会在bolt操作中主动调用ack、fail方法时触发）。
 * @Date: 2020/10/30 18:03
 */
public class RabbitmqSpout implements IRichSpout {
    private final Logger LOGGER = LoggerFactory.getLogger(RabbitmqSpout.class);

    private Map map;
    private TopologyContext topologyContext;
    private SpoutOutputCollector spoutOutputCollector;

    private Connection connection;
    private Channel channel;

    private static final String QUEUE_NAME = "rabbitMQ.test.queue";
    private final Map<String, Long> unconfirmedMap = Collections.synchronizedMap(new HashMap<String, Long>());

    //连接mq服务
    private void connect() throws IOException, TimeoutException {
        ConnectionFactory factory = new ConnectionFactory();
        factory.setHost("localhost");
        factory.setUsername("admin");
        factory.setPassword("admin");
        factory.setVirtualHost("/");

        connection = factory.newConnection();
        channel = connection.createChannel();
        channel.queueDeclare(QUEUE_NAME, true, false, false, null);
    }

    @Override
    public void open(Map map, TopologyContext topologyContext, SpoutOutputCollector spoutOutputCollector) {
        this.map = map;
        this.topologyContext = topologyContext;
        this.spoutOutputCollector = spoutOutputCollector;

        try {
            this.connect();
        } catch (IOException e) {
            e.printStackTrace();
        } catch (TimeoutException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void close() {
        try {
            channel.close();
            connection.close();
        } catch (IOException e) {
            e.printStackTrace();
        } catch (TimeoutException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void nextTuple() {
        try {
            GetResponse response = channel.basicGet(QUEUE_NAME, false);
            if (response == null) {
                Utils.sleep(3000);
            } else {
                AMQP.BasicProperties props = response.getProps();
                String messageId = UUID.randomUUID().toString();
                Long deliveryTag = response.getEnvelope().getDeliveryTag();
                String body = new String(response.getBody());

                unconfirmedMap.put(messageId, deliveryTag);
                LOGGER.info("RabbitmqSpout: {}, {}, {}, {}", body, messageId, deliveryTag, props);

                this.spoutOutputCollector.emit(new Values(body), messageId);
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void ack(Object o) {
        String messageId = o.toString();
        Long deliveryTag = unconfirmedMap.get(messageId);
        LOGGER.info("ack: {}, {}, {}\n\n", messageId, deliveryTag, unconfirmedMap.size());
        try {
            unconfirmedMap.remove(messageId);
            channel.basicAck(deliveryTag, false);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void fail(Object o) {
        String messageId = o.toString();
        Long deliveryTag = unconfirmedMap.get(messageId);
        LOGGER.info("fail: {}, {}, {}\n\n", messageId, deliveryTag, unconfirmedMap.size());
        try {
            unconfirmedMap.remove(messageId);
            channel.basicNack(deliveryTag, false, true);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void declareOutputFields(OutputFieldsDeclarer outputFieldsDeclarer) {
        outputFieldsDeclarer.declare(new Fields("body"));
    }

    @Override
    public Map<String, Object> getComponentConfiguration() {
        return null;
    }

    @Override
    public void activate() {

    }

    @Override
    public void deactivate() {

    }
}