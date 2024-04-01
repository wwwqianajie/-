package com.example.demo.IOHbase;
import org.apache.hadoop.conf.Configuration;
import org.apache.hadoop.hbase.HBaseConfiguration;
import org.apache.hadoop.hbase.client.Connection;
import org.apache.hadoop.hbase.client.ConnectionFactory;
import org.apache.hadoop.hbase.client.Get;
import org.apache.hadoop.hbase.client.Result;
import org.apache.hadoop.hbase.client.Table;
import org.apache.hadoop.hbase.TableName;
import org.apache.hadoop.hbase.Cell;
import org.apache.hadoop.hbase.CellUtil;
import org.apache.hadoop.hbase.client.Scan;
import org.apache.hadoop.hbase.client.ResultScanner;
import org.apache.hadoop.hbase.util.Bytes;
import org.apache.spark.sql.Dataset;
import org.apache.spark.sql.Row;
import org.apache.spark.sql.SparkSession;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
public class SparkHbase {
    private Configuration configuration;
    private Connection connection;
    public SparkHbase() throws IOException {
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
            String value1 = Bytes.toString(result.getValue(Bytes.toBytes("id"), Bytes.toBytes("item_price")));
            String value2 = Bytes.toString(result.getValue(Bytes.toBytes("id"), Bytes.toBytes("address")));
            String value3 = Bytes.toString(result.getValue(Bytes.toBytes("id"), Bytes.toBytes("order_status")));
            orderData.add(value1);
            orderData.add(value2);
            orderData.add(value3);
            results.add(orderData);
        }
//        for (List<String> result : results) {
//            System.out.println(result);
//        }

        return results;
    }

}
