from flask import Flask, render_template, request, redirect, url_for

app = Flask(__name__)

@app.route('/', methods=['POST', 'GET'])
def login():
    if request.method == 'POST':
        username = request.form['username']
        password = request.form['password']
        if username == 'admin' and password == 'admin':
            return redirect(url_for('index'))
        else:
            return render_template('login.html',error = "wrong username or password")
    return  render_template('login.html', error = None)
    return render_template('login.html')


@app.route('/index',methods = ['POST','GET'])
def index():
    return render_template('all_themes.html', error = None)


if __name__ == "__main__":
    app.run(debug=True)