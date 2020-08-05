from flask import Flask, render_template, request, redirect, url_for, session, jsonify
from collections import OrderedDict
import pyrebase
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

                    json_post = {
                        'username': doc.get('username'),
                        'email': doc.get('email'),
                        'usertype': doc.get('usertype')
                    }
                    
                    '''
                    if 'Content-Type' in request.headers:
                        if 'application/json' in request.headers['Content-Type']:
                            json_list = [
                                {'posts': [json_post]}
                            ]
                            return jsonify(json_list)
                    '''

                    return redirect(url_for('manage'))
                else:
                    message = "Password is Incorrect"

                    json_post = {
                        'message': message
                    }

                    return render_template('login.html', message=message)
            else:
                message = "Login Info is incorrect"

                json_post = {
                    'message': message
                }

                return render_template('login.html', message=message)
        except:
            message = "An Error Occured When logging in"

            json_post = {
                'message': message
            }

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

                    json_post = {
                        'message': message
                    }
                    
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

                    json_post = {
                        'successmessage': successmessage
                    }

                    return render_template('login.html', error=None, successmessage=successmessage)
                else:
                    message = "An Error Occurred When Signing Up"
                    
                    json_post = {
                        'message': message
                    }
                    
                    return render_template('signup.html', message=message)
        except:
            message = "An Error Occurred When Signing Up"

            json_post = {
                'message': message
            }

            return render_template('signup.html', message=message)
    return render_template('signup.html', error=None)



@app.route('/manage', methods=['POST', 'GET'])
def manage():
    db_username = session['db_username']
    db_usertype = session['db_usertype']
    posts = db_firestore.collection('posts').where('author', '==', db_username).stream()
    #categories = db_firestore.collection('posts').where('author', '==', db_username).where('category', '==', True).stream()

    json_post = {
        'usertype': db_usertype,
        'posts': posts
    }
    
    return render_template('manage.html',usertype=db_usertype, posts=posts)



def uploadImage(imgName, imgPath):
    config = {
    "apiKey": "AIzaSyBSuBwrJF_Z76sjL0bcUzPXloEOPHFQ5bc",
    "authDomain": "apad-team5.firebaseapp.com",
    "databaseURL": "https://apad-team5.firebaseio.com",
    "projectId": "apad-team5",
    "storageBucket": "apad-team5.appspot.com",
    "messagingSenderId": "311004038430",
    "appId": "1:311004038430:web:e70bcb7c84b0e96075750f",
    "measurementId": "G-FSQ5JBDS95"
    }

    firebase = pyrebase.initialize_app(config)
    storage = firebase.storage()
    storage.child(f"images/{imgName}").put(imgPath)
    imageurl = storage.child(f"images/{imgName}").get_url(None)
    return imageurl



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

        if posttitle == "":
            message = "post title is empty"

            json_post = {
                'usertype': db_usertype,
                'message': message,
                'all_themes': all_themes1
            }

            return render_template('createP.html', message=message, usertype=db_usertype, all_themes=all_themes1)
            # Check if there is a document with the same name
        else:
            doc_ref = db_firestore.collection(u'posts').document(posttitle)
            doc = doc_ref.get()
            if doc.exists:
                message = "post title exists"

                json_post = {
                    'usertype': db_usertype,
                    'message': message,
                    'all_themes': all_themes1
                }

                return render_template('createP.html', message=message, usertype = db_usertype, all_themes= all_themes1)

            # if not in the document, add it.
            else:
                message = "post created successfully"
                image = request.files['img']
                imageName = image.filename
                imageURL = uploadImage(imageName, image)

                #RANDOM LOCATION
                lat = random.uniform(-90,90)
                long = random.uniform(-180, 180)
                location = [round(lat, 15), round(long, 15)]

                doc_ref.set({
                    u'title': posttitle,
                    u'category': postcategory,
                    u'content': postcontent,
                    u'tags': posttags,
                    u'author': db_username,
                    u'location': location,
                    u'imgURL': imageURL
                })

                json_post = {
                    'usertype': db_usertype,
                    'message': message,
                }
                
            return redirect(url_for('createP',usertype = db_usertype,message = message))

    json_post = {
        'usertype': db_usertype,
        'all_themes': all_themes1
    }

    return render_template('createP.html',usertype = db_usertype, all_themes=all_themes1)



@app.route('/createT', methods=['POST', 'GET'])
def createT():
    db_usertype = session['db_usertype']
    if request.method == 'POST':
        catename = request.form['c-name']
        catedescription = request.form['c-descri']

        if catename =="":
            message = "category name is empty"

            json_post = {
                'usertype': db_usertype,
                'message': message
            }

            return render_template('createT.html', message=message, usertype=db_usertype)
        else:
            doc_ref = db_firestore.collection(u'categories').document(catename)
            doc = doc_ref.get()

            if doc.exists:
                message = "category name already exists"

                json_post = {
                    'usertype': db_usertype,
                    'message': message
                }

                return render_template('createT.html', message=message, usertype=db_usertype)
            else:
                image = request.files['img']
                imageName = image.filename
                imageURL = uploadImage(imageName, image)
                message = "category created successfully"
                doc_ref.set({
                    u'name': catename,
                    u'description': catedescription,
                    u'imgURL': imageURL
                })

                json_post = {
                    'usertype': db_usertype,
                    'message': message
                }

                return redirect(url_for('createT', usertype=db_usertype, message=message))

    json_post = {
        'usertype': db_usertype
    }

    return render_template('createT.html', usertype=db_usertype)



@app.route('/all_categories', methods=['POST', 'GET'])
def categories():
    db_usertype = session['db_usertype']
    all_categories = db_firestore.collection("categories").stream()

    json_post = {
        'all_categories': all_categories,
        'usertype': db_usertype
    }

    return render_template('all_categories.html', all_categories=all_categories, usertype = db_usertype,error=None)



@app.route('/results', methods=['POST', 'GET'])
def results():
    db_usertype = session['db_usertype']
    if request.method == 'POST':

        def createMap(posts):

            markers = []
            temp_posts = []

            for post in posts:
                coords = []
                for point in post.to_dict()["location"]:
                    coords.append(point)

                markers.append(
                    {
                    'lat': coords[0],
                    'lng': coords[1],
                    'infobox': "<b>" + str(post.to_dict()["title"]) + "</b>" + "<br><img width=50px height=50px src=\'" + str(post.to_dict()["imgURL"]) + "\'></img>"+ "\'></img>"
                    }
                )
                temp_posts.append(post)

            map = Map(
                identifier="sndmap",
                lat=30.2672,
                lng=-97.7431,
                markers=markers,
                cluster=True,
                cluster_imagepath="https://developers.google.com/maps/documentation/javascript/examples/markerclusterer/m"
            )

            return [map, temp_posts]

        if 'tags' in request.form:
            tags = request.form['filterValue']
            tagsSplit = tags.split(", ")

            #GET DATA STREAM
            posts = db_firestore.collection("posts").where("tags", "array_contains_any", tagsSplit).stream()

            ourContent = createMap(posts)

            json_post = {
                'type': 'tags',
                'tags': tags,
                'posts': ourContent[1],
                'map': ourContent[0],
                'usertype': db_usertype
            }

            '''
            if 'Content-Type' in request.headers:
                if 'application/json' in request.headers['Content-Type']:
                    json_list = [
                        {'posts': [json_post]}
                    ]
                    return jsonify(json_list)
            '''

            return render_template('results.html', type='tags', tags=tags, posts=ourContent[1], map=ourContent[0], usertype=db_usertype)

        elif 'category' in request.form:
            filterValue = request.form['category']

            #GET DATA STREAM
            posts = db_firestore.collection("posts").where("category", "==", filterValue).stream()

            ourContent = createMap(posts)

            json_post = {
                'type': 'category',
                'posts': ourContent[1],
                'map': ourContent[0],
                'filterValue': filterValue,
                'usertype': db_usertype
            }

            '''
            if 'Content-Type' in request.headers:
                if 'application/json' in request.headers['Content-Type']:
                    json_list = [
                        {'posts': [json_post]}
                    ]
                    return jsonify(json_list)
            '''

            return render_template('results.html', type='category', posts=ourContent[1], map=ourContent[0], filterValue=filterValue, usertype=db_usertype)

    return render_template('results.html' ,error=None)



if __name__ == "__main__":
    app.run(debug=True)
