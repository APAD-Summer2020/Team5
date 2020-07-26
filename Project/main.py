from flask import Flask, render_template, request, redirect, url_for, session
import pyrebase

#Database configuration
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
#initializing pyrebase
firebase = pyrebase.initialize_app(config)
#initializing database
db = firebase.database()
auth = firebase.auth()

app = Flask(__name__)
app.secret_key = "hello"


@app.route('/', methods=['POST', 'GET'])
def login():
    if request.method == 'POST':
        username = request.form['username']
        password = request.form['password']
        session["user"] = username

        if username == 'admin' and password == 'admin':
            return redirect(url_for('manage'))

        elif username == 'test' and password == 'test':
            return redirect(url_for('manage'))

        else:
            return render_template('login.html', error="wrong username or password")
        return render_template('login.html', error=None)
    return render_template('login.html')


""" FOR SEARCHING BY TAG
From https://dev.to/gogamic/introduction-to-pyrebase-database-2mif
# reterving data using loops
all_users = db.child("users").get()
for user in all_users.each():
    print(user.key()) # Morty
    print(user.val()) # {name": "Mortimer 'Morty' Smith"}
"""


@app.route('/signup', methods=['POST', 'GET'])
def signup():
    if request.method == 'POST':
        username = request.form['username']
        email = request.form['email']
        password = request.form['password']
        user = auth.create_user_with_email_and_password(email, password)
        data = {"email": email, "password": password}
        db.child("users").child(username).set(data)
        return redirect(url_for('manage'))

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
    user = session["user"]

    if request.method == 'POST':
        themename = request.form['t-name']
        themedes = request.form['theme-description']
        data = {"theme-description": themedes}
        db.child("themes").child(themename).set(data)

     #   theme = request.form['theme']
    #    reportname = request.form['r-title']
   #     reportdes = request.form['report-description']
  #      reporttag = request.form['r-tag']
 #       data = {"report-description": reportdes, "report-tags":reporttag}
#        db.child("themes").child(theme).child(reportname).set(data)

        return redirect(url_for('create'))

    return render_template('create.html', username=user)


@app.route('/all_themes', methods=['POST', 'GET'])
def themes():

    return render_template('all_themes.html', error=None)

# db.child("companies/data").order_by_child("id").equal_to(company_id).limit_to_first(1).get()
# https://stackoverflow.com/questions/50893423/how-to-get-single-item-in-pyrebase
@app.route('/one_theme', methods=['POST', 'GET'])
def search():
    return render_template('one_theme.html', error=None)


if __name__ == "__main__":
    app.run(debug=True)
