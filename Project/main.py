from flask import Flask, render_template, request, redirect, url_for, session
#import pyrebase
import firebase_admin
from firebase_admin import credentials, firestore, initialize_app

'''
Firestore documentation:
https://firebase.google.com/docs/firestore
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
        '''
        TODO: 
        - Add alerts if the user didn't enter in a valid username or password.
        - Create a session key.
        - Return user information.
        '''

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
            u'type': usertype
        })
        return render_template('login.html', error=None)

    return render_template('signup.html', error=None)



@app.route('/manage', methods=['POST', 'GET'])
def manage():
    return render_template('manage.html', error=None)



@app.route('/create', methods=['POST', 'GET'])
def create():

    user = session["user"]
    if request.method == 'POST':
        themename = request.form['t-name']
        themedes = request.form['theme-description']
        data = {"theme-description": themedes}
        db.child("themes").child(themename).set(data)
        return redirect(url_for('create'))

    return render_template('create.html', username=user)



@app.route('/all_themes', methods=['POST', 'GET'])
def themes():
    #all_themes = db.child("themes").get()
    return render_template('all_themes.html', error=None)



@app.route('/one_theme', methods=['POST', 'GET'])
def search():
    return render_template('one_theme.html', error=None)



if __name__ == "__main__":
    app.run(debug=True)
