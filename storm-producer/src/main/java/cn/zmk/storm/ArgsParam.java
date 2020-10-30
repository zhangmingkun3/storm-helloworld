package cn.zmk.storm;
import	java.io.Serializable;

/**
 * @Author: zhangmingkun3
 * @Description:
 * @Date: 2020/10/30 16:55
 */
public class ArgsParam implements Serializable {

    private String host= null;
    private String queueName = null;
    private String userName= null;
    private String password = null;
    private int port = 5672;

    private String exchangeName;


    public String getHost() {
        return host;
    }

    public void setHost(String host) {
        this.host = host;
    }

    public String getQueueName() {
        return queueName;
    }

    public void setQueueName(String queueName) {
        this.queueName = queueName;
    }

    public String getUserName() {
        return userName;
    }

    public void setUserName(String userName) {
        this.userName = userName;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public int getPort() {
        return port;
    }

    public void setPort(int port) {
        this.port = port;
    }

    public String getExchangeName() {
        return exchangeName;
    }

    public void setExchangeName(String exchangeName) {
        this.exchangeName = exchangeName;
    }
}