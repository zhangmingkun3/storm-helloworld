package cn.zmk.storm;
import	java.io.Serializable;

/**
 * @Author: zhangmingkun3
 * @Description:
 * @Date: 2020/10/30 17:40
 */
public class Message implements Serializable {

    private String id;
    private String name;
    private Long time;

    public Message() {
    }

    public Message(String id, String name, Long time) {
        this.id = id;
        this.name = name;
        this.time = time;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public Long getTime() {
        return time;
    }

    public void setTime(Long time) {
        this.time = time;
    }
}