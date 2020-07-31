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

#test


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
                session['db_usertype'] = db_usertype
                session['db_username'] = db_username

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
        return render_template('login.html', error=None)

    return render_template('signup.html', error=None)



@app.route('/manage', methods=['POST', 'GET'])
def manage():

    db_usertype = session['db_usertype']
    return render_template('manage.html',usertype = db_usertype)



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



@app.route('/createP', methods=['POST', 'GET'])
def createP():
    db_usertype = session['db_usertype']
    db_username = session['db_username']
    error = None
    all_themes1 = db_firestore.collection("categories").stream()
    if request.method == 'POST':
        postcategory = request.form['p-category']
        posttitle = request.form['p-title']
        postcontent = request.form['post-content']
        posttaginput = request.form['p-tag']
        posttags = posttaginput.split(", ")
 #   getCategory = db_firestore.collection(u'categories').stream()

   #     for doc in getCategory:
   #         print(f'{doc.id}')

        # NEW CODE USING FIRESTORE
        doc_ref = db_firestore.collection(u'posts').document(posttitle)
        doc_ref.set({
            u'title': posttitle,
            u'category': postcategory,
            u'content': postcontent,
            u'tags': posttags,
            u'author': db_username
        })
        return redirect(url_for('createP',usertype = db_usertype,error = error))

    return render_template('createP.html',usertype = db_usertype, all_themes=all_themes1,error = error)


@app.route('/createT', methods=['POST', 'GET'])
def createT():
    db_usertype = session['db_usertype']
    if request.method == 'POST':
        catename = request.form['c-name']
        catedescription = request.form['c-descri']
        cateimage = request.form['img']

        # NEW CODE USING FIRESTORE
        doc_ref = db_firestore.collection(u'categories').document(catename)
        doc_ref.set({
            u'name': catename,
            u'description': catedescription,
            u'image': cateimage
        })

        return redirect(url_for('createT',usertype = db_usertype))

    return render_template('createT.html',usertype = db_usertype)


@app.route('/all_categories', methods=['POST', 'GET'])
def categories():
    db_usertype = session['db_usertype']
    all_categories = db_firestore.collection("categories").stream()

    return render_template('all_categories.html', all_categories=all_categories, usertype = db_usertype,error=None)


@app.route('/results', methods=['POST', 'GET'])
def search():
    db_usertype = session['db_usertype']
    if request.method == 'POST':
        '''
        TODO: Get elif for categories to work.
        '''

        if 'tags' in request.form:
            searchInput = request.form['filterValue']
            tags = searchInput.split("#")

            #GET DATA STREAM
            posts = db_firestore.collection("posts").where("tags", "array_contains_any", tags).stream()
            

            return render_template('results.html', type='tags', tags=tags, posts=posts,usertype = db_usertype)
            
        elif 'category' in request.form:
            filterValue = request.form['category']

            #GET DATA STREAM
            posts = db_firestore.collection("posts").where("category", "==", filterValue)

            return render_template('results.html', type='category', filterValue=filterValue,usertype = db_usertype)

    return render_template('results.html', error=None)

if __name__ == "__main__":
    app.run(debug=True)
