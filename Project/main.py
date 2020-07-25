from flask import Flask, render_template, request, redirect, url_for

app = Flask(__name__)

@app.route('/', methods=['POST', 'GET'])
def login():
    if request.method == 'POST':
        username = request.form['username']
        password = request.form['password']
        if username == 'admin' and password == 'admin':
            return redirect(url_for('manage'))
        else:
            return render_template('login.html',error = "wrong username or password")
    return  render_template('login.html', error = None)
    return render_template('login.html')


@app.route('/manage',methods = ['POST','GET'])
def manage():

    return render_template('manage.html', error = None)


@app.route('/create',methods = ['POST','GET'])
def create():

    return render_template('create.html', error = None)


@app.route('/all_themes',methods = ['POST','GET'])
def themes():

    return render_template('all_themes.html', error = None)


@app.route('/one_theme',methods = ['POST','GET'])
def search():

    return render_template('one_theme.html', error = None)


if __name__ == "__main__":
    app.run(debug=True)