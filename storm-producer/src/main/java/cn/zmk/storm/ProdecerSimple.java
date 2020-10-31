package cn.zmk.storm;

import com.rabbitmq.client.Channel;
import com.rabbitmq.client.Connection;
import com.rabbitmq.client.ConnectionFactory;

import java.io.IOException;
import java.util.UUID;
import java.util.concurrent.TimeoutException;

public class ProdecerSimple {

    private final static String QUEUE_NAME = "rabbitMQ.test.queue";

    public static void main(String[] args) throws IOException, TimeoutException {

        //创建连接工厂
        ConnectionFactory factory = new ConnectionFactory();

        // 设置RabbitMQ相关信息
        factory.setHost("localhost");
        factory.setUsername("admin");
        factory.setPassword("admin");
        factory.setPort(5672);

        // 创建新的连接
        Connection connection = null;
        Channel channel = null;
        try {
            connection = factory.newConnection();
            // 创建一个通道
            channel = connection.createChannel();

            // 声明队列
            //queueDeclare（队列名称，是否持久化（true表示是，队列将在服务器重启时生存），是否是独占队列（创建者可以使用的私有队列，断开后自动删除），
            //                        当所有消费者客户端连接断开时是否自动删除队列，队列的其他参数）
            channel.queueDeclare(QUEUE_NAME,true,false,false,null);

            while (true){
                String message = "Hello RabbitMQ " + UUID.randomUUID();

                // 发送消息到队列中  basicPublish（交换机名称，队列映射的路由key，消息的其他属性，发送信息的主体）
                channel.basicPublish("",QUEUE_NAME,null,message.getBytes("UTF-8"));
                Thread.sleep(10);
            }

        } catch (IOException e) {
            e.printStackTrace();
        } catch (TimeoutException e) {
            e.printStackTrace();
        } catch (InterruptedException e) {
            e.printStackTrace();
        } finally {
            if (channel != null){
                channel.close();
            }
            if (connection != null){
                connection.close();
            }
        }






    }

}
