package com.example.demo.IOHbase;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import org.apache.hadoop.conf.Configuration;
import org.apache.hadoop.hbase.HBaseConfiguration;
import org.apache.hadoop.hbase.TableName;
import org.apache.hadoop.hbase.client.Connection;
import org.apache.hadoop.hbase.client.ConnectionFactory;
import org.apache.hadoop.hbase.client.Put;
import org.apache.hadoop.hbase.client.Table;
import org.apache.hadoop.hbase.util.Bytes;

public class CsvToHbase {
    public static Connection connection = null;

    static {
        try {
            // 1、获取 HBase 配置信息
            Configuration configuration = HBaseConfiguration.create();
            configuration.set("hbase.zookeeper.property.clientPort", "2181");
            configuration.set("hbase.zookeeper.quorum", "192.168.10.105");

            // 2、创建连接对象
            connection = ConnectionFactory.createConnection(configuration);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public static void insertDataToHBase(String tableName, String csvFilePath) throws IOException {
        try (Table table = connection.getTable(TableName.valueOf(tableName))) {
            List<Put> puts = new ArrayList<>();
            try (BufferedReader br = new BufferedReader(new FileReader(csvFilePath))) {
                String line;
                line = br.readLine();
                while ((line = br.readLine()) != null) {
                    String[] values = line.split(",");
                    String rowKey = values[2]; // 假设 rank_id 是主键兼行键
                    Put put = new Put(Bytes.toBytes(rowKey));
                    put.addColumn(Bytes.toBytes("rank_id"), Bytes.toBytes("price_range"), Bytes.toBytes(values[0])); // price_range 列
                    put.addColumn(Bytes.toBytes("rank_id"), Bytes.toBytes("count"), Bytes.toBytes(values[1])); // count 列
                    puts.add(put);
                }
            }
            System.out.println(puts);
            table.put(puts);
        }
    }
    public static void insertDataToHBase1(String tableName, String csvFilePath) throws IOException {
        try (Table table = connection.getTable(TableName.valueOf(tableName))) {
            List<Put> puts = new ArrayList<>();
            try (BufferedReader br = new BufferedReader(new FileReader(csvFilePath))) {
                String line;
                line = br.readLine();
                while ((line = br.readLine()) != null) {
                    String[] values = line.split(",");
                    String rowKey = values[0]; // 假设 rank_id 是主键兼行键
                    Put put = new Put(Bytes.toBytes(rowKey));
                    put.addColumn(Bytes.toBytes("pv_id"), Bytes.toBytes("province"), Bytes.toBytes(values[1])); // price_range 列
                    put.addColumn(Bytes.toBytes("pv_id"), Bytes.toBytes("count"), Bytes.toBytes(values[2])); // count 列
                    puts.add(put);
                }
            }
            System.out.println(puts);
            table.put(puts);
        }
    }
    public static void insertDataToHBase2(String tableName, String csvFilePath) throws IOException {
        try (Table table = connection.getTable(TableName.valueOf(tableName))) {
            List<Put> puts = new ArrayList<>();
            try (BufferedReader br = new BufferedReader(new FileReader(csvFilePath))) {
                String line;
                line = br.readLine();
                while ((line = br.readLine()) != null) {
                    String[] values = line.split(",");
                    String rowKey = values[0]; // 假设 rank_id 是主键兼行键
                    Put put = new Put(Bytes.toBytes(rowKey));
                    put.addColumn(Bytes.toBytes("st_id"), Bytes.toBytes("date_day"), Bytes.toBytes(values[1])); // price_range 列
                    put.addColumn(Bytes.toBytes("st_id"), Bytes.toBytes("order_status"), Bytes.toBytes(values[2])); // count 列
                    put.addColumn(Bytes.toBytes("st_id"), Bytes.toBytes("count"), Bytes.toBytes(values[3]));
                    puts.add(put);
                }
            }
            System.out.println(puts);
            table.put(puts);
        }
    }
    public static void insertDataToHBase3(String tableName, String csvFilePath) throws IOException {
        try (Table table = connection.getTable(TableName.valueOf(tableName))) {
            List<Put> puts = new ArrayList<>();
            try (BufferedReader br = new BufferedReader(new FileReader(csvFilePath))) {
                String line;
                line = br.readLine();
                while ((line = br.readLine()) != null) {
                    String[] values = line.split(",");
                    String rowKey = values[0]; // 假设 rank_id 是主键兼行键
                    Put put = new Put(Bytes.toBytes(rowKey));
                    put.addColumn(Bytes.toBytes("time_id"), Bytes.toBytes("time_point"), Bytes.toBytes(values[1])); // price_range 列
                    put.addColumn(Bytes.toBytes("time_id"), Bytes.toBytes("count"), Bytes.toBytes(values[2]));
                    puts.add(put);
                }
            }
            System.out.println(puts);
            table.put(puts);
        }
    }
    public static void insertDataToHBase4(String tableName, String csvFilePath) throws IOException {
        try (Table table = connection.getTable(TableName.valueOf(tableName))) {
            List<Put> puts = new ArrayList<>();
            try (BufferedReader br = new BufferedReader(new FileReader(csvFilePath))) {
                String line;
                line = br.readLine();
                while ((line = br.readLine()) != null) {
                    String[] values = line.split(",");
                    String rowKey = values[0]; // 假设 rank_id 是主键兼行键
                    Put put = new Put(Bytes.toBytes(rowKey));
                    put.addColumn(Bytes.toBytes("goods_id"), Bytes.toBytes("item_name"), Bytes.toBytes(values[1])); // price_range 列
                    put.addColumn(Bytes.toBytes("goods_id"), Bytes.toBytes("count"), Bytes.toBytes(values[2]));
                    puts.add(put);
                }
            }
            System.out.println(puts);
            table.put(puts);
        }
    }
    public static void close() {
        if (connection != null) {
            try {
                connection.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    public static void main(String[] args) throws IOException {
        close();
    }
}