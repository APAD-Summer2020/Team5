from flask import Flask, render_template, request, redirect, url_for, session
import pyrebase

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

db = firebase.database()

# Creating Keys
# data = {"AdminAccount": {"username": "admin", "password": "admin"},
#         "TestUserAccount": {"username": "test", "password": "test"}
#         }

# db.child("UserAccounts").set(data)

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


@app.route('/signup', methods=['POST','GET'])
def signup():
    return render_template('signup.html', error = None)


@app.route('/manage', methods=['POST', 'GET'])
def manage():

    return render_template('manage.html', error = None)


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
