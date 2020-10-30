package cn.zmk.storm;

import com.alibaba.fastjson.JSON;
import com.rabbitmq.client.Channel;
import com.rabbitmq.client.Connection;
import com.rabbitmq.client.ConnectionFactory;
import java.io.IOException;
import java.util.UUID;
import java.util.concurrent.TimeoutException;

/**
 * @Author: zhangmingkun3
 * @Description:
 * @Date: 2020/10/30 16:41
 */
public class Producer {

    public static void main(String[] args) throws IOException, TimeoutException {

        if (args != null && args.length > 0) {

            ArgsParam argsParam = JSON.parseObject(args[0],ArgsParam.class);
            System.out.println(JSON.toJSONString(argsParam));

            //创建连接工厂
            ConnectionFactory factory = new ConnectionFactory();
            //设置RabbitMQ相关信息
            factory.setHost(argsParam.getHost());
            factory.setUsername(argsParam.getUserName());
            factory.setPassword(argsParam.getPassword());
             factory.setPort(argsParam.getPort());
            //创建一个新的连接
            Connection connection = factory.newConnection();
            //创建一个通道
            Channel channel = connection.createChannel();

            // queueDeclare第一个参数表示队列名称、第二个参数为是否持久化（true表示是，队列将在服务器重启时生存）
            // 第三个参数为是否是独占队列（创建者可以使用的私有队列，断开后自动删除）、第四个参数为当所有消费者客户端连接断开时是否自动删除队列、第五个参数为队列的其他参数
            channel.queueDeclare(argsParam.getQueueName(),false,false,false,null);

            //发送消息到队列  第一个参数为交换机名称、第二个参数为队列映射的路由key、第三个参数为消息的其他属性、第四个参数为发送信息的主体

            for (int i = 0; i < 100000; i ++){
                channel.basicPublish(argsParam.getExchangeName(),argsParam.getQueueName(),null,JSON.toJSONString(new Message(UUID.randomUUID().toString(),"rabbitmq " + i,System.currentTimeMillis())).getBytes("UTF-8"));
            }

            //关闭通道和连接
            channel.close();
            connection.close();


        } else {
            System.out.println("需要命令行参数...");
        }


    }

}