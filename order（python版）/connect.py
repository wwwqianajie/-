import pymysql
class Mysql(object):
    def __init__(self):
        try:
            self.db = pymysql.connect(host="192.168.10.105",user="root",password="123456",database="e_commerce")
            #游标对象
            self.cursor = self.db.cursor()
            print("连接成功！")
        except:
            print("连接失败！")
 	#查询数据函数
    def getdata(self):
        sql = "select * from tb_price_range1"
        #执行sql语句
        self.cursor.execute(sql)
        #获取所有的记录
        results = self.cursor.fetchall()
        return results
    def getdata2(self):
        sql = "select * from tb_time_order"
        self.cursor.execute(sql)
        results = self.cursor.fetchall()
        return results
    def getdata3(self):
        sql = "select * from succeed_goods_top20"
        self.cursor.execute(sql)
        results = self.cursor.fetchall()
        return results
    def getdata4(self):
        sql = "select * from tb_status_contrast where date_day='8'"
        self.cursor.execute(sql)
        results = self.cursor.fetchall()
        return results
    def getdata4_5(self):
        sql1 = "select * from tb_status_contrast where date_day='9'"
        self.cursor.execute(sql1)
        results = self.cursor.fetchall()
        return results
    def getdata5(self):
        sql="select * from tb_province_order"
        self.cursor.execute(sql)
        results = self.cursor.fetchall()
        return results
    def insert(self,column,content):
        sql="insert into tb_price_range values({},{})".format(column,content)
        self.cursor.execute(sql)
        self.db.commit()
    def delete(self,column1,content1):
        sql="DELETE FROM tb_price_range WHERE price_range={} and count={}".format(column1,content1)
        self.cursor.execute(sql)
        self.db.commit()
    def getdata6(self):
        sql = "select product,amount from meizhuang"
        self.cursor.execute(sql)
        results = self.cursor.fetchall()
        return results
    def getdata7(self):
        sql = "select product,sale_count from meizhuang"
        self.cursor.execute(sql)
        results = self.cursor.fetchall()
        return results
    def __del__(self):
        self.db.close()
