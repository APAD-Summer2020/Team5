from flask import Flask, render_template, request, redirect, url_for, session
from collections import OrderedDict
#import pyrebase
import firebase_admin
from firebase_admin import credentials, firestore, initialize_app
from flask_googlemaps import GoogleMaps, Map
import random

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
# you can set key as config
app.config['GOOGLEMAPS_KEY'] = "AIzaSyA03QZ0mnq5LSurSiSyzCWowGi0_R85mPc"

#test
# Initialize the extension
GoogleMaps(app)

@app.route('/', methods=['POST', 'GET'])
def login():
    if request.method == 'POST':

        username = request.form['username']
        password = request.form['password']
        try:
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
                else:
                    message = "Password is Incorrect"
                    return render_template('login.html', message=message)
            else:
                message = "Login Info is incorrect"
                return render_template('login.html', message=message)
        except:
            message = "An Error Occured When logging in"
            return render_template('login.html', message=message)
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
        try:
            # NEW CODE USING FIRESTORE
            doc_ref = db_firestore.collection(u'users').document(username)
            doc = doc_ref.get()
            if doc.exists:
                # If there is, check if the passwords match
                db_pass = doc.get('password')
                if db_pass == password:
                    message = "This account already exists"
                    return render_template('signup.html', message=message)
            else:
                if username != "" and email != "" and password != "":
                    doc_ref.set({
                        u'username': username,
                        u'email': email,
                        u'password': password,
                        u'usertype': usertype
                    })
                    successmessage="Account Successfully Created"
                    return render_template('login.html', error=None, successmessage=successmessage)
                else:
                    message = "An Error Occurred When Signing Up"
                    return render_template('signup.html', message=message)
        except:
            message = "An Error Occurred When Signing Up"
            return render_template('signup.html', message=message)
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

        if posttitle == "":
            message = "post title is empty"
            return render_template('createP.html', message=message, usertype=db_usertype, all_themes=all_themes1)
            # Check if there is a document with the same name
        else:
            doc_ref = db_firestore.collection(u'posts').document(posttitle)
            doc = doc_ref.get()
            if doc.exists:
                message = "post title exists"
                return render_template('createP.html', message=message, usertype = db_usertype, all_themes= all_themes1)

            # if not in the document, add it.
            else:
                message = "post created successfully"

                #RANDOM LOCATION
                lat = random.uniform(-180, 180)
                long = random.uniform(-90,90)
                location = [round(lat, 4), round(long, 4)]

                doc_ref.set({
                    u'title': posttitle,
                    u'category': postcategory,
                    u'content': postcontent,
                    u'tags': posttags,
                    u'author': db_username,
                    u'location': location
                })
            return redirect(url_for('createP',usertype = db_usertype,message = message))

    return render_template('createP.html',usertype = db_usertype, all_themes=all_themes1)


@app.route('/createT', methods=['POST', 'GET'])
def createT():
    db_usertype = session['db_usertype']
    if request.method == 'POST':
        catename = request.form['c-name']
        catedescription = request.form['c-descri']
        cateimage = request.form['img']

        if catename =="":
            message = "category name is empty"
            return render_template('createT.html', message=message, usertype=db_usertype)
        else:
            doc_ref = db_firestore.collection(u'categories').document(catename)
            doc = doc_ref.get()
            if doc.exists:
                message = "category name already exists"
                return render_template('createT.html', message=message, usertype=db_usertype)
            else:
                message = "category created successfully"
                doc_ref.set({
                    u'name': catename,
                    u'description': catedescription,
                    u'image': cateimage
                })

                return redirect(url_for('createT',usertype = db_usertype, message = message))

    return render_template('createT.html',usertype = db_usertype)


@app.route('/all_categories', methods=['POST', 'GET'])
def categories():
    db_usertype = session['db_usertype']
    all_categories = db_firestore.collection("categories").stream()

    return render_template('all_categories.html', all_categories=all_categories, usertype = db_usertype,error=None)


@app.route('/results', methods=['POST', 'GET'])
def results():
    db_usertype = session['db_usertype']
    if request.method == 'POST':
        '''
        TODO: Get elif for categories to work.
        To get geopoint from Firestore: "location = firestore.GeoPoint(latitude, longitude)"
        '''

        if 'tags' in request.form:
            tags = request.form['filterValue']
            tagsSplit = tags.split(", ")

            #GET DATA STREAM
            posts = db_firestore.collection("posts").where("tags", "array_contains_any", tagsSplit).stream()


            return render_template('results.html', type='tags', posts=posts, usertype=db_usertype)

        elif 'category' in request.form:
            filterValue = request.form['category']

            #GET DATA STREAM
            posts = db_firestore.collection("posts").where("category", "==", filterValue).stream()

            return render_template('results.html', type='category', posts=posts, filterValue=filterValue, usertype=db_usertype)

    return render_template('results.html', error=None)

@app.route("/map")
def mapview():
    # creating a map in the view
    mymap = Map(
        identifier="view-side",
        lat=37.4419,
        lng=-122.1419,
        markers=[(37.4419, -122.1419)]
    )
    sndmap = Map(
        identifier="sndmap",
        lat=37.4419,
        lng=-122.1419,
        markers=[
          {
             'icon': 'http://maps.google.com/mapfiles/ms/icons/green-dot.png',
             'lat': 37.4419,
             'lng': -122.1419,
             'infobox': "<b>Hello World</b>"
          },
          {
             'icon': 'http://maps.google.com/mapfiles/ms/icons/blue-dot.png',
             'lat': 37.4300,
             'lng': -122.1400,
             'infobox': "<b>Hello World from other place</b>"
          }
        ]
    )
    return render_template('map.html', mymap=mymap, sndmap=sndmap)

if __name__ == "__main__":
    app.run(debug=True)
