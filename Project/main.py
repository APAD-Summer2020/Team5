from flask import Flask, render_template, request, redirect, url_for, session
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
                db_usertype = doc.get('type')
                session['usertype'] = doc.get('type')
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
        permissions = request.form['permissions']

        # NEW CODE USING FIRESTORE
        doc_ref = db_firestore.collection(u'users').document()
        doc_ref.set({
            u'_id': doc_ref.id,
            u'username': username,
            u'email': email,
            u'password': password,
            u'permissions': permissions
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
        postcategory = request.form['p-category']
        posttitle = request.form['p-title']
        postcontent = request.form['post-content']
        posttag = request.form['p-tag']
        categoryname = request.form['c-name']
        categorydescription = request.form['c-descri']


        # NEW CODE USING FIRESTORE
        doc_ref = db_firestore.collection(u'posts').document(posttitle)
        doc_ref.set({
            u'category': postcategory,
            u'title': posttitle,
            u'content': postcontent,
            u'tags': posttag

        })
        return redirect(url_for('create.html', username=user))


    return render_template('create.html', username=user)



@app.route('/all_themes', methods=['POST', 'GET'])
def themes():
    all_themes = db.child("themes").get()
    return render_template('all_themes.html', all_themes=all_themes.val(), error=None)

# db.child("companies/data").order_by_child("id").equal_to(company_id).limit_to_first(1).get()
# https://stackoverflow.com/questions/50893423/how-to-get-single-item-in-pyrebase



@app.route('/one_theme', methods=['POST', 'GET'])
def search():
    return render_template('one_theme.html', error=None)



if __name__ == "__main__":
    app.run(debug=True)
