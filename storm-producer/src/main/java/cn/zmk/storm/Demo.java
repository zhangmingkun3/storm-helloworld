package cn.zmk.storm;

import com.alibaba.fastjson.JSON;

public class Demo {

    public static void main(String[] args) {

        ArgsParam argsParam = new ArgsParam("localhost","rabbitMQ.test.queue","admin","admin",5672,"");

        System.out.println(JSON.toJSONString(argsParam));

    }

}
