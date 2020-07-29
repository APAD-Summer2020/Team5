from flask import Flask, render_template, request, redirect, url_for, session
from collections import OrderedDict
#import pyrebase
import firebase_admin
from firebase_admin import credentials, firestore, initialize_app

'''
Firestore documentation:
https://firebase.google.com/docs/firestore
API:
https://googleapis.dev/python/firestore/latest/index.html
'''

# Use the application default credentials
cred = credentials.Certificate("gc_privatekey.json")
firebase_admin.initialize_app(cred)
db_firestore = firestore.client()

app = Flask(__name__)
app.secret_key = "hello"


@app.route('/', methods=['POST', 'GET'])
def login():
    if request.method == 'POST':

        username = request.form['username']
        password = request.form['password']

        #Get the document with the entered username
        doc_ref = db_firestore.collection(u'users').document(username)
        doc = doc_ref.get()

        #Check if there is a document with that username in the database
        if doc.exists:
            #If there is, check if the passwords match
            db_pass = doc.get('password')
            if db_pass == password:
                #If passwords match, log in.
                #Send all user information to template.
                db_username = doc.get('username')
                db_email = doc.get('email')
                db_usertype = doc.get('usertype')
                session['usertype'] = doc.get('usertype')
                return render_template('manage.html', username=db_username, email=db_email, usertype=db_usertype)
        
    return render_template('login.html')



@app.route('/signup', methods=['POST', 'GET'])
def signup():
    '''
    TODO: 
    - Add validation to check if user already exists in the database.
    - Alert to let them know their account was successfully created.
    '''

    if request.method == 'POST':
        username = request.form['username']
        email = request.form['email']
        password = request.form['password']
        usertype = request.form['usertype']

        # NEW CODE USING FIRESTORE
        doc_ref = db_firestore.collection(u'users').document(username)
        doc_ref.set({
            u'username': username,
            u'email': email,
            u'password': password,
            u'usertype': usertype
        })

    return render_template('signup.html', error=None)



@app.route('/manage', methods=['POST', 'GET'])
def manage():

    return render_template('manage.html', error=None)

"""
To update data for an existing entry use the update() method.
db.child("users").child("Morty").update({"name": "Mortiest Morty"})

To create your own keys use the set() method. The key in the example below is "Morty".

data = {"name": "Mortimer 'Morty' Smith"}
db.child("users").child("Morty").set(data)

push
To save data with a unique, auto-generated, timestamp-based key, use the push() method.

data = {"name": "Mortimer 'Morty' Smith"}
db.child("users").push(data)

Source:
https://github.com/thisbejim/Pyrebase
"""



@app.route('/create', methods=['POST', 'GET'])
def create():

    user = session["usertype"]
    if request.method == 'POST':
        themename = request.form['t-name']
        themedes = request.form['theme-description']
        # NEW CODE USING FIRESTORE
        doc_ref = db_firestore.collection(u'themes').document(themename)
        doc_ref.set({
            u'theme-description': themedes

        })
        return redirect(url_for('create'))
    #theme = request.form['theme']
    #reportname = request.form['r-title']
    #reportdes = request.form['report-description']
    #reporttag = request.form['r-tag']
    #data = {"report-description": reportdes, "report-tags":reporttag}
    #db.child("themes").child(theme).child(reportname).set(data)

    return render_template('create.html', username=user)



@app.route('/all_categories', methods=['POST', 'GET'])
def categories():
    all_categories = db.child("themes").get()
    return render_template('all_categories.html', all_categories=all_categories.val(), error=None)

# db.child("companies/data").order_by_child("id").equal_to(company_id).limit_to_first(1).get()
# https://stackoverflow.com/questions/50893423/how-to-get-single-item-in-pyrebase



@app.route('/results', methods=['POST', 'GET'])
def search():
    if request.method == 'POST':
        '''
        TODO: Check if POST is coming from tags or categories.
        '''

        if 'tags' in request.form:
            filterType = 'tags'
            #filterValue = request.form['value']
            return render_template('results.html', filterType=filterType)
        #elif request.form['name'] == 'categories':
            #filterType = 'categories'
            #filterValue = request.form['value']

        return render_template('results.html', filterType=filterType, error=None)



if __name__ == "__main__":
    app.run(debug=True)
