from flask import render_template, Blueprint
from flask_login import LoginManager, UserMixin, login_user, logout_user, login_required, current_user
from link import *
from api.sql import Analysis

analysis = Blueprint('analysis', __name__, template_folder='../templates')

@analysis.route('/dashboard')
@login_required
def dashboard():
    revenue = []
    dataa = []
    care_revenue = []
    care_count = []

    for i in range(1,13):
        row = Analysis.month_price(i)

        if not row:
            revenue.append(0)
        else:
            for j in row:
                revenue.append(j[1])
        
        row = Analysis.month_count(i)
        
        if not row:
            dataa.append(0)
        else:
            for k in row:
                dataa.append(k[1])

        row = Analysis.month_cprice(i)

        if not row:
            care_revenue.append(0)
        else:
            for l in row:
                care_revenue.append(l[1])
        
        row = Analysis.month_ccount(i)
        
        if not row:
            care_count.append(0)
        else:
            for m in row:
                care_count.append(m[1])



    row = Analysis.category_sale()
    datab = []
    for i in row:
        temp = {
            'value': i[0],
            'name': i[1]
        }
        datab.append(temp)


    #care_category
    row = Analysis.category_care()
    care_category = []
    for i in row:
        temp = {
            'value': i[0],
            'name': i[1]
        }
        care_category.append(temp)

    
    row = Analysis.member_sale()
    
    datac = []
    nameList = []
    counter = 0
    
    for i in row:
        counter = counter + 1
        datac.append(i[0])
    for j in row:
        nameList.append(j[2])
    
    counter = counter - 1
    
    row = Analysis.member_sale_count()
    countList = []
    
    for i in row:
        countList.append(i[0])




        
    return render_template('dashboard.html', counter = counter, revenue = revenue, dataa = dataa, care_revenue = care_revenue, care_count = care_count, datab = datab, care_category = care_category, datac = datac, nameList = nameList, countList = countList)