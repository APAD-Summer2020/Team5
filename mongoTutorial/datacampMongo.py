import pymongo
from pymongo import MongoClient

#client = MongoClient()
#client = MongoClient('mongodb://localhost:27017/')
client = MongoClient("localhost", 27017)

# use the MongoClient instance and specify a database name
db = client['datacampdb']

article = {"author": "Derrick Mwiti",
            "about": "Introduction to MongoDB and Python",
            "tags":
                ["mongodb", "python", "pymongo"]}

# Insert a document
articles = db.articles
result = articles.insert_one(article)

#Clear a collection
articles.drop()

#print("First article key is: {}".format(result.inserted_id))

#print(db.list_collection_names())


# Insert multiple documents
article1 = {"author": "Emmanuel Kens",
            "about": "Knn and Python",
            "tags":
                ["Knn","pymongo"]}
article2 = {"author": "Daniel Kimeli",
            "about": "Web Development and Python",
            "tags":
                ["web", "design", "HTML"]}
new_articles = articles.insert_many([article1, article2])
#print("The new article IDs are {}".format(new_articles.inserted_ids))

# Retrieve a single document with find_one()
#print(articles.find_one())

# Finding all Documents in a collection
for article in articles.find():
  print(article)


#When building web applications, we usually get document IDs
# from the URL and try to retrieve them from our MongoDB collection

# Convert the obtained string ID into an ObjectID
from bson.objectid import ObjectId
def get(post_id):
    document = client.db.collection.find_one({'_id': ObjectId(post_id)})


# Return Some Fields Only
for article in articles.find({},{ "_id": 0, "author": 1, "about": 1}):
  print(article)

# Sorting the Results
doc = articles.find().sort("author", -1)

for x in doc:
  print(x)

# Update ONE document
query = { "author": "Derrick Mwiti" }
new_author = { "$set": { "author": "John David" } }

articles.update_one(query, new_author)

for article in articles.find():
  print(article)

# Limit results
limited_result = articles.find().limit(1)
for x in limited_result:
    print(x)

# Delete a document
#db.articles.delete_one({"_id":ObjectId("5f1795fa80548e660a576ffa")})

# Delete many/all (Empty = all)
delete_articles = articles.delete_many({})
print(delete_articles.deleted_count, " articles deleted.")


#____________________________________________________________

#OBJECT DOCUMENT MAPPER
from mongoengine import *
connect('datacampdb', host='localhost', port=27017)

# Defining the User
class User(Document):
    email = StringField(required=True)
    first_name = StringField(max_length=30)
    last_name = StringField(max_length=30)

# Defining the Post
class Post(Document):
    # make reference from one document to another in mongoengine
    title = StringField(max_length=120, required=True)
    author = ReferenceField(User)

# Save
user = User(email="connect@derrickmwiti.com", first_name="Derrick", last_name="Mwiti")
user.save()

print(user.id, user.email, user.first_name, user.last_name)
