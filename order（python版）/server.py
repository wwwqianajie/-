from flask import Flask,render_template,request
import pymysql
from clf import dtclf
#导入数据库操作类
from connect import Mysql
class price_province(object):
    result=0
    province=0
    price=0
app = Flask(__name__)
app.config['JSON_AS_ASCII'] = False
@app.route("/",methods=['GET','POST'])
def clf():
    return render_template("main.html")
@app.route("/main.html",methods=['GET','POST'])
def show():
    price=int(request.form.get("price"))
    province=int(request.form.get("province"))
    clf1=dtclf()
    result=clf1.predict(price,province)
    price_province.result=result
    if(int(result[0])==0):
        result='已取消'
    elif(int(result[0])==1):
        result='待支付'
    elif(int(result[0])==2):
        result='待发货'
    elif(int(result[0])==3):
        result='待收货'
    else:
        result='已完成'
    print(type(price_province.result[0]),price_province.result[0])
    return render_template("no2.html",result=result)

@app.route("/no2.html",methods=['GET','POST'])
def index():
    db = Mysql()
    results = db.getdata()
    results=dict(results)
    print(results)
    return render_template("order_price1.html",results=results)
@app.route("/order_price1.html",methods=['GET','POST'])
def info():
    db = Mysql()
    results = db.getdata2()
    results = dict(results)
    column_text=request.form.get("column")
    content=request.form.get("content")
    column1=request.form.get("column1")
    content1=request.form.get("content1")
    print(column_text)
    if(column_text!=None and content!=None):
        db.insert(column=column_text,content=content)
        results1 = db.getdata()
        results1=dict(results1)
        return render_template("order_price1.html",results=results1)
    elif(column1!=None and content1!=None):
        db.delete(column1=column1,content1=content1)
        results1 = db.getdata()
        results1=dict(results1)
        return render_template("order_price1.html",results=results1)
    else:
        return render_template("order_price2.html",results=results)
@app.route("/order_price2.html",methods=['GET','POST'])
def order3():
    db = Mysql()
    results = db.getdata3()
    results = dict(results)
    ylist=[]
    for i in results:
        ylist.append(i)
    xlist=[]
    for i in results:
        xlist.append(results[i])
    return render_template("order3.html",ylist=ylist,xlist=xlist)
@app.route("/order3.html",methods=['GET','POST'])
def order4():
    db = Mysql()
    results = db.getdata4()
    xlist1=[]
    for i in results:
        xlist1.append(i[1])
    ylist1=[]
    for i in results:
        ylist1.append(i[2])
    results2 = db.getdata4_5()
    xlist2=[]
    for i in results2:
        xlist2.append(i[1])
    ylist2=[]
    for i in results2:
        ylist2.append(i[2])
    return render_template("order4.html",ylist1=ylist1,xlist1=xlist1,xlist2=xlist2,ylist2=ylist2)
@app.route("/order4.html",methods=['GET','POST'])
def order5():
    db=Mysql()
    results = db.getdata5()
    results1=[]
    for i in results:
        i=list(i)
        i[0]=i[0][0]+i[0][1]
        results1.append(i)

    results1 = dict(results1)
    return render_template('order5.html',results1=results1)
@app.route("/order5.html",methods=['GET','POST'])
def meizhuang():
    db=Mysql()
    results=db.getdata6()
    results=dict(results)
    return render_template("meizhuang.html",results=results)
@app.route("/meizhuang.html",methods=['GET','POST'])
def meizhuang1():
    db=Mysql()
    results=db.getdata7()
    xlist=[]
    ylist=[]
    for i in results:
        xlist.append(i[0])
        ylist.append(i[1])
    return render_template("meizhuang1.html",xlist=xlist,ylist=ylist)
if __name__ == "__main__":
    app.run(debug=True)
