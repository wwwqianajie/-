package com.example.demo.IOHbase;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;
import com.example.demo.IOHbase.SparkHbase;
import org.apache.spark.ml.classification.BinaryLogisticRegressionSummary;
import org.apache.spark.ml.classification.LogisticRegression;
import org.apache.spark.ml.classification.LogisticRegressionModel;
import org.apache.spark.ml.evaluation.BinaryClassificationEvaluator;
import org.apache.spark.ml.feature.StringIndexer;
import org.apache.spark.ml.feature.VectorAssembler;
import org.apache.spark.ml.param.ParamMap;
import org.apache.spark.ml.tuning.CrossValidator;
import org.apache.spark.ml.tuning.CrossValidatorModel;
import org.apache.spark.ml.tuning.ParamGridBuilder;
import org.apache.spark.sql.Dataset;
import org.apache.spark.sql.Row;
import org.apache.spark.sql.RowFactory;
import org.apache.spark.sql.SparkSession;
import org.apache.spark.sql.functions;
import org.apache.spark.sql.types.DataTypes;
import org.apache.spark.sql.types.StructField;
import scala.Option;
import org.apache.spark.sql.types.StructType;
public class machine_learning {
    private final LogisticRegressionModel model;

    public machine_learning() throws Exception {
        SparkSession spark = SparkSession.builder()
                .appName("MySparkApp")
                .master("local[*]")
                .appName("Save CSV")
                .getOrCreate();

        SparkHbase sparkhbase = new SparkHbase();
        List<List<String>> datalist = sparkhbase.getDataFromTable();

        // 创建DataFrame
        List<Row> rows = new ArrayList<>();
        StructType schema = getSchema();
        for (List<String> row : datalist) {
            Object[] values = new Object[row.size()];
            for (int i = 0; i < row.size(); i++) {
                String value = row.get(i);
                if (i == 0) { // Price列为Double类型
                    values[i] = Double.parseDouble(value);
                } else { // 其他列为String类型
                    values[i] = value;
                }
            }
            rows.add(RowFactory.create(values));
        }
        Dataset<Row> df = spark.createDataFrame(rows, schema);

        // 处理Address列，只保留省份信息
        df = df.withColumn("Address", functions.split(df.col("Address"), " ").getItem(0));

        df = df.filter(df.col("Address").notEqual("--"));
        df = df.withColumn("Status", functions.expr("case when Status = '已取消' then 0 " +
                "when Status = '待支付' then 1 " +
                "when Status = '待发货' then 2 " +
                "when Status = '待收货' then 3 " +
                "when Status = '已完成' then 4 " +
                "else null end"))
                .na().drop();
        df.show();
        StringIndexer indexer = new StringIndexer()
                .setInputCol("Address")
                .setOutputCol("AddressIndex");
        df = indexer.fit(df).transform(df);

        // 构建特征向量
        VectorAssembler assembler = new VectorAssembler()
                .setInputCols(new String[] {"Price", "AddressIndex"})
                .setOutputCol("features");
        Dataset<Row> inputData = assembler.transform(df).select("features", "Status");
        inputData.show();

        // 训练模型
        double[] weights = {0.6, 0.4};
        Dataset<Row>[] datasets = inputData.randomSplit(weights, 12);
        Dataset<Row> trainingData = datasets[0];
        Dataset<Row> testData = datasets[1];
        LogisticRegression lr = new LogisticRegression()
                .setMaxIter(100)
                .setRegParam(0.1)  // 最佳参数
                .setElasticNetParam(0.0)
                .setFamily("auto")
                .setLabelCol("Status")
                .setFeaturesCol("features")
                .setFitIntercept(true)
                .setStandardization(true)
                .setThreshold(0.5)
                .setTol(1e-6)
                .setAggregationDepth(2);

        model = lr.fit(trainingData);
    }

    public String predict(double price, double province) {
        // 构造输入数据
        List<Row> rows = new ArrayList<>();
        StructType schema = getSchema();
        Object[] values = new Object[3];
        values[0] = price;
        values[1] = Double.toString(province);
        values[2] = "待支付";  // 这里的状态值并不会被使用，随便填一个即可
        rows.add(RowFactory.create(values));
        Dataset<Row> inputData = SparkSession.getActiveSession().get().createDataFrame(rows, schema);

        // 处理Address列，只保留省份信息
        inputData = inputData.withColumn("Address", functions.split(inputData.col("Address"), " ").getItem(0));

        StringIndexer indexer = new StringIndexer()
                .setInputCol("Address")
                .setOutputCol("AddressIndex");
        inputData = indexer.fit(inputData).transform(inputData);

        // 构建特征向量
        VectorAssembler assembler = new VectorAssembler()
                .setInputCols(new String[]{"Price", "AddressIndex"})
                .setOutputCol("features");
        inputData = assembler.transform(inputData).select("features");

        // 做出预测
        Dataset<Row> predictions = model.transform(inputData);
        double predictedStatus = predictions.select("prediction").first().getDouble(0);

        if (predictedStatus == 0.0) {
            return "已取消";
        } else if (predictedStatus == 1.0) {
            return "待支付";
        } else if (predictedStatus == 2.0) {
            return "待发货";
        } else if (predictedStatus == 3.0) {
            return "待收货";
        } else
            return "已完成";

    }

    private static StructType getSchema() {
        return DataTypes.createStructType(new StructField[]{
                DataTypes.createStructField("Price", DataTypes.DoubleType, true),
                DataTypes.createStructField("Address", DataTypes.StringType, true),
                DataTypes.createStructField("Status", DataTypes.StringType, true)
        });
    }
}

