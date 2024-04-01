package com.example.demo.hbase;

import org.apache.hadoop.conf.Configuration;
import org.apache.hadoop.hbase.HBaseConfiguration;
import org.apache.hadoop.hbase.TableName;
import org.apache.hadoop.hbase.client.*;
import org.apache.hadoop.hbase.util.Bytes;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class SparkHbaseExample {
    private Configuration configuration;
    private Connection connection;
    public SparkHbaseExample() throws IOException {
        this.configuration = HBaseConfiguration.create();
        this.configuration.set("hbase.zookeeper.quorum", "192.168.10.105");
        this.configuration.set("hbase.zookeeper.property.clientPort", "2181");
        this.connection = ConnectionFactory.createConnection(configuration);
    }
    public List<List<String>> getDataFromTable() throws IOException {

        Table table = connection.getTable(TableName.valueOf("bilibili_order"));
        Scan scan = new Scan();
        ResultScanner scanner = table.getScanner(scan);
        List<List<String>> results = new ArrayList<>();

        for (Result result : scanner) {
            List<String> orderData = new ArrayList<>();
            String value = Bytes.toString(result.getValue(Bytes.toBytes("id"), Bytes.toBytes("item_name")));
            String value1 = Bytes.toString(result.getValue(Bytes.toBytes("id"), Bytes.toBytes("item_price")));
            String value2 = Bytes.toString(result.getValue(Bytes.toBytes("id"), Bytes.toBytes("address")));
            String value3 = Bytes.toString(result.getValue(Bytes.toBytes("id"), Bytes.toBytes("order_status")));
            String value4 = Bytes.toString(result.getValue(Bytes.toBytes("id"), Bytes.toBytes("pay_time")));
            orderData.add(value);
            orderData.add(value1);
            orderData.add(value2);
            orderData.add(value3);
            orderData.add(value4);
            results.add(orderData);
        }
//        for (List<String> result : results) {
//            System.out.println(result);
//        }

        return results;
    }
}