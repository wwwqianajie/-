package com.example.demo.controller;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import java.util.LinkedHashSet;
import java.util.Set;
import java.util.List;
import org.springframework.web.bind.annotation.GetMapping;
import java.io.IOException;
import com.example.demo.hbase.HbaseConnector;
import com.example.demo.hbase.tb_time_order;
import java.util.Map;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import com.example.demo.hbase.tb_status;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import com.example.demo.hbase.succeed_goods;
import com.example.demo.hbase.tb_province_order;
import com.example.demo.IOHbase.CsvToHbase;
import com.example.demo.IOHbase.machine_learning;
/**
 * @Author wei
 * @Date 2022/6/1 21:02
 * @Version 1.0
 */
//@Controller
//@RequestMapping
@Controller
@RequestMapping
public class IndexController {
    @GetMapping("/main")
    public String page1(String[] args) throws IOException {
        return "main";
    }
    @PostMapping("/submit-form1")
    public String predict(@RequestParam("price") double price ,@RequestParam("province") double province, Model model) throws Exception {
        machine_learning mac=new machine_learning();
        model.addAttribute("result",mac.predict(price,province));
        return "predict";
    }
    @PostMapping("/predict")
    @GetMapping("/page2")
    public String page2(String[] args) throws IOException {
        CsvToHbase csvToHbase=new CsvToHbase();
        String tableName = "tb_price_range";
        String tableName1 = "tb_province_order";
        String tableName2 = "tb_status_contrast";
        String tableName3 = "tb_time_order";
        String tableName4 = "succeed_goods_top20";
        String csvFilePath = "C:\\Users\\HP\\Desktop\\csv\\result1\\result1.csv";
        String csvFilePath1 = "C:\\Users\\HP\\Desktop\\csv\\result2\\result2.csv";
        String csvFilePath2 = "C:\\Users\\HP\\Desktop\\csv\\result3\\result3.csv";
        String csvFilePath3 = "C:\\Users\\HP\\Desktop\\csv\\result4\\result4.csv";
        String csvFilePath4 = "C:\\Users\\HP\\Desktop\\csv\\result5\\result5.csv";
        csvToHbase.insertDataToHBase(tableName, csvFilePath);
        csvToHbase.insertDataToHBase1(tableName1, csvFilePath1);
        csvToHbase.insertDataToHBase2(tableName2, csvFilePath2);
        csvToHbase.insertDataToHBase3(tableName3, csvFilePath3);
        csvToHbase.insertDataToHBase4(tableName4, csvFilePath4);
        return "page2";
    }
    @PostMapping("/submit-form")
    public String getHBaseData(@RequestParam("username") String username, Model model) throws IOException {
        if (username.equals("tb_price_range")) {
            HbaseConnector hbaseConnector = new HbaseConnector();
            List<String> data = hbaseConnector.getDataFromTable(username);
            List<String> firstFiveRows = data.subList(0, Math.min(data.size(), 10));
            Map<String, Integer> dictionaryPairs = new LinkedHashMap<>();
            for (int i = 0; i < firstFiveRows.size() - 1; i += 2) {
                dictionaryPairs.put(firstFiveRows.get(i), Integer.parseInt(firstFiveRows.get(i + 1)));
            }
            model.addAttribute("data", dictionaryPairs);
            return "price_range";
        } else if (username.equals("tb_time_order")) {
            // Add logic for other_username here
            tb_time_order tb_time_order1 = new tb_time_order();
            List<String> data = tb_time_order1.getDataFromTable(username);
            Map<String, Integer> dictionaryPairs1 = new LinkedHashMap<>();
            for (int i = 0; i < data.size() - 1; i += 2) {
                dictionaryPairs1.put(data.get(i), Integer.parseInt(data.get(i + 1)));
            }
            model.addAttribute("data", dictionaryPairs1);
            return "time_order";
        }else if(username.equals("tb_status_contrast")){
            tb_status tb_status1 = new tb_status();
            List<String> data = tb_status1.getDataFromTable(username);
            Set<String> xset = new LinkedHashSet<>(); // 使用LinkedHashSet以保持插入顺序且不重复
            Map<String, Integer> ymap1 = new LinkedHashMap<>();
            Map<String, Integer> ymap2 = new LinkedHashMap<>();
            for (int i = 0; i < data.size(); i += 3) {
                String x = data.get(i + 2); // 获取中文状态
                String key = data.get(i); // 获取8或9
                try {
                    int y = Integer.parseInt(data.get(i + 1)); // 获取数值
                    if (key.equals("08")) {
                        ymap1.put(x, y); // 把y值加入ymap1
                    } else if (key.equals("09")) {
                        ymap2.put(x, y); // 把y值加入ymap2
                    }
                } catch (NumberFormatException e) {
                    // Handle the case where parseInt() fails
                    System.err.println("Failed to parse integer value: " + data.get(i + 1));
                }
                xset.add(x); // 把中文状态加入xset
            }
            List<String> xlist = new ArrayList<>(xset);
            List<Integer> ylist1 = new ArrayList<>(ymap1.values());
            List<Integer> ylist2 = new ArrayList<>(ymap2.values());
            System.out.println(xlist);
        System.out.println(ylist1);
        System.out.println(ylist2);
        model.addAttribute("xlist", xlist);
        model.addAttribute("ylist1", ylist1);
        model.addAttribute("ylist2", ylist2);
        return "tb_status_contrast";
    }
        else if(username.equals("succeed_goods_top20")){
            succeed_goods succeed_goods1 = new succeed_goods();
            List<String> data = succeed_goods1.getDataFromTable("succeed_goods_top20");
            List<String> ylist = new ArrayList<>();
            List<Integer> xlist = new ArrayList<>();

            for (int i = 0; i < data.size(); i++) {
                if (i % 2 == 0) {
                    // 插入到ylist
                    ylist.add(data.get(i));
                } else {
                    // 插入到xlist
                    xlist.add(Integer.parseInt(data.get(i)));
                }
            }

            model.addAttribute("ylist1", ylist);
            model.addAttribute("xlist1", xlist);
            return "top20";
        }
        else if(username.equals("tb_province_order")){
            tb_province_order tb_province_order1 = new tb_province_order();
            List<String> data = tb_province_order1.getDataFromTable(username);
            Map<String, Integer> dictionaryPairs = new LinkedHashMap<>();
            for (int i = 0; i < data.size() - 1; i += 2) {
                dictionaryPairs.put(data.get(i), Integer.parseInt(data.get(i + 1)));
            }
            model.addAttribute("data", dictionaryPairs);
            return "tb_province_order";
        }
        else {
            return "error";
        }

    }

}