import requests
from bs4 import BeautifulSoup 
import os 
from docx import Document
import pandas as pd
#!flask/bin/python
import json
import pandas as pd
from flask import Flask, jsonify, request,render_template
import os


app = Flask(__name__)
if not os.path.isfile('town.docx'):
	url=requests.get('https://www.mass.gov/info-details/covid-19-response-reporting') 
	soup = BeautifulSoup(url.text, 'html.parser') 
	download_url='https://www.mass.gov'+soup.select("a[href*=citytown]")[0]['href'] 
	os.system("curl "+download_url+">> town.docx")   
document=Document('town.docx')
r=[]
for table in document.tables: 
	for row in table.rows:   
		t=[] 
		for cell in row.cells: 
			t.append(cell.text) 
		r.append(t) 

df=pd.DataFrame(r)
df.columns=['City','Count','rate']
df=df.drop([0, len(df)-1])  
df['Count']=df['Count'].str.replace('[^0-9]','').astype(int)                            
df=df.reset_index(drop=True) 
df['rate']=df['rate'].str.replace('*','0').astype(float)
df['label']=pd.qcut(df['rate'], [0, .25, .5, .75, 1.],labels=['low','medium','high','very high'])
loc=pd.read_pickle('location.pkl')
loc=loc.merge(df, left_on='Town', right_on='City',how='left')

@app.route('/query', methods=['GET'])
def get_data():
	return loc.to_json(orient='records')

@app.route('/show', methods=['GET'])
def get_score():
	score=request.args.get('score', type=int)
	return render_template('show.html', variable=str(score))

@app.route('/send', methods=['POST'])
def calculate():
	s=request.json
	score=0
	score_map={'low':1,'medium':2,'high':3,'very high':4}
	for i in range(len(s['location'])):
		address=s['location'][i]['address'].split(',')[1].strip()
		level=loc[loc.Town==address]['label'].values[0]
		score=score+int(s['ble'][i]['count'])*score_map[level]
	score=min(score,100)
	return jsonify({'score':score})

if __name__ == '__main__':
    app.run(host='0.0.0.0', port=8888, debug=True)
