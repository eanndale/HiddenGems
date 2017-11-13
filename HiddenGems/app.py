from chalice import Chalice
import rds_config
import pymysql
import googlemaps

app = Chalice(app_name='HiddenGems')

rds_host = rds_config.db_endpoint
name = rds_config.db_username
password = rds_config.db_password
db_name = rds_config.db_name

@app.route('/')
def index():
    return {'hello': 'world'}

@app.route('/route/{start}/{end}', methods=['POST'])
def route(start, end):
    try:
        conn = pymysql.connect(rds_host, user=name, passwd=password, db=db_name, connect_timeout=5)
    except:
        raise ConflictError("Could not connect to MySQL database")

    gmaps = googlemaps.Client(key='AIzaSyBold8BEkdg3qQosQk7Fz1uWuyl0GJ9JOk')

    directions = gmaps.directions(start, end, mode="driving")

    return "Hello World"

    # use google maps to find route between points
    # choose intermediate points on route every hundred? miles
    # make searches around those places for cool stuff
    # add those locations to the database provided they are unique
    # return the list of locs

    # deletion can be done from frontend (just delete point marked)

    # getting a different point just involves searching again.


# The view function above will return {"hello": "world"}
# whenever you make an HTTP GET request to '/'.
#
# Here are a few more examples:
#
# @app.route('/hello/{name}')
# def hello_name(name):
#    # '/hello/james' -> {"hello": "james"}
#    return {'hello': name}
#
# @app.route('/users', methods=['POST'])
# def create_user():
#     # This is the JSON body the user sent in their POST request.
#     user_as_json = app.current_request.json_body
#     # We'll echo the json body back to the user in a 'user' key.
#     return {'user': user_as_json}
#
# See the README documentation for more examples.
