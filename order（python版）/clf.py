import pandas as pd
import numpy as np
import random
from sklearn.tree import DecisionTreeClassifier
from sklearn.model_selection import GridSearchCV, train_test_split
from sklearn.metrics import accuracy_score

class dtclf(object):
    def __init__(self):
        data = pd.read_csv('orders.csv')
        # 从地址字段中提取省份、城市和区域信息
        data['province'] = data['address'].str.split(' ', expand=True)[0]
        data['city'] = data['address'].str.split(' ', expand=True)[1]
        data['district'] = data['address'].str.split(' ', expand=True)[2]

        # 删除原始的地址列
        del data['address']
        data['province'] = data['province'].replace('--', np.nan)
        data['city'] = data['city'].replace('None', np.nan)
        data['district'] = data['district'].replace('None', np.nan)
        unique_status = data['order_status'].drop_duplicates()

        #结果编码化
        data['order_status'].replace('已取消', 0, inplace=True)
        data['order_status'].replace('待支付', 1, inplace=True)
        data['order_status'].replace('待发货', 2, inplace=True)
        data['order_status'].replace('待收货', 3, inplace=True)
        data['order_status'].replace('已完成', 4, inplace=True)

        #获取订单状态
        unique_status = data['order_status'].drop_duplicates()

        #独热编码将地区编码化
        province_dummy = pd.get_dummies(data['province'])
        df = pd.concat([data, province_dummy], axis=1)

        # 确定用于训练模型的特征
        features = list(province_dummy.columns)
        features.append("item_price")
        X = df[features]
        Y=data['order_status']

        # 划分训练集和测试集
        X_train, X_test, y_train, y_test = train_test_split(X, Y, test_size=0.00085, random_state=32)

        # 创建决策树分类器对象
        clf = DecisionTreeClassifier()

        # 定义超参数空间
        param_grid = {'max_depth': [1, 8, 12],
                    'min_samples_split': [2, 4, 6],
                        'criterion': ['gini', 'entropy'],  # 分裂节点时使用的度量
            'splitter': ['best', 'random']}   # 选择分裂节点的策略

        # 使用网格搜索寻找最优超参数组合
        grid_search = GridSearchCV(clf, param_grid=param_grid, scoring='accuracy', cv=5)
        grid_search.fit(X_train, y_train)

        # 输出最优超参数组合
        print("Best parameters:", grid_search.best_params_)

        # 在测试集上进行预测
        y_pred = grid_search.predict(X_test)

        # 计算精度得分
        accuracy = accuracy_score(y_test, y_pred)

        # 输出精度得分
        print("Accuracy:", accuracy)

        self.clf = grid_search

    def predict(self,price,province):
        # 测试样例设置
        new_data = [['0', '0', '0', '0', '0', '0', '0', '0', '0', '0', '0', '0', '0', '0', '0', '0', '0', '0', '0', '0', '0', '0', '0', '0', '0', '0', '0', '0', '0', '0']]
        new_data[0][province]='1'
        new_data[0].append(price)
        
        # 进行预测
        prediction = self.clf.predict(new_data)

        return prediction
