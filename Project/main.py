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

#Pushing Data
# data = {
#     "username": "admin",
#     "password": "admin"
# }
# db.push(data)

#Creating Key
#Admin Account
# data = {"username": "admin", "password": "admin"}
# db.child("AdminAccount").set(data)

#Test User Account
# data = {"username": "test", "password": "test"}
# db.child("TestUserAccount").set(data)

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


@app.route('/manage', methods=['POST', 'GET'])
def manage():
    return render_template('manage.html', error=None)


@app.route('/create', methods=['POST', 'GET'])
def create():
    user = session["user"]
    return render_template('create.html', username=user)


@app.route('/all_themes', methods=['POST', 'GET'])
def themes():
    return render_template('all_themes.html', error=None)


@app.route('/one_theme', methods=['POST', 'GET'])
def search():
    return render_template('one_theme.html', error=None)


if __name__ == "__main__":
    app.run(debug=True)
