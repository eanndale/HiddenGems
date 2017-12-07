from chalice import Chalice
import pymysql
import googlemaps
import json
import math
import requests
import pyowm
#from urllib.request import urlopen
from datetime import datetime
import datetime
#!/usr/bin/env python
# -*- coding: utf-8 -*-

app = Chalice(app_name='HiddenGems')
app.debug = True


@app.route('/')
def index():
    return {'hello': 'world'}

#returns 5 day every 3 hour forecast at coordinates
@app.route('/forecast')
def forecast(lat_, lon_):
    key = "5a61d0979d0298f923b45e24d5cfd6ee"

    url = 'http://api.openweathermap.org/data/2.5/forecast?lat=' + str(lat_) + '&lon=' + str(lon_) + '&APPID=' + key 
    
    response = requests.get(url)
    
    response_string = response.content.decode("utf-8")
    
    json_object = json.loads(response_string)
    
    return json_object
    
    
#return weather_object using pyowm library
def get_weather_object(lat_, lon_):
    #https://github.com/csparpa/pyowm
    #parameters = {"lat": , "lon": }
    key = "5a61d0979d0298f923b45e24d5cfd6ee"
    
    owm = pyowm.OWM(key)  # You MUST provide a valid API key
    
    #obs = owm.weather_at_place('Detriot,MI')                    # Toponym
    #obs = owm.weather_at_id(2643741)                           # City ID
    obs = owm.weather_at_coords(lat_, lon_)           # lat/lon
    #obs = owm.daily_forecast('London,uk', limit = 5)
    weather = obs.get_weather()
    
    return weather

#return the temperature in fahrenheit
@app.route('/temperature')
def get_temperature(lat_, lon_):
    weather_object = get_weather_object(lat_,lon_)
    
    return weather_object.get_temperature('fahrenheit');

#return the weather description: eg couldy, rainy 
@app.route('/status')
def get_status(lat_,lon_):
    weather_object = get_weather_object(lat_,lon_)
    
    return weather_object.get_status();

#return a detailed weather description: eg broken clouds
@app.route('/detailed_status')
def get_detailed_status(lat_,lon_):
    weather_object = get_weather_object(lat_,lon_)
    
    return weather_object.get_detailed_status();
    
    
# --------------------------------------------------------------------------------------------------
# The following was taken from this link
# https://gis.stackexchange.com/questions/157693/getting-all-vertex-lat-long-coordinates-every-1-meter-between-two-known-points

# Calculates the distance between two lat, long coordinate pairs, in meters
def getPathLength(lat1,lng1,lat2,lng2):
    R = 6371000 # radius of earth in m
    lat1rads = math.radians(lat1)
    lat2rads = math.radians(lat2)
    deltaLat = math.radians((lat2-lat1))
    deltaLng = math.radians((lng2-lng1))
    a = math.sin(deltaLat/2) * math.sin(deltaLat/2) + math.cos(lat1rads) * math.cos(lat2rads) * math.sin(deltaLng/2) * math.sin(deltaLng/2)
    c = 2 * math.atan2(math.sqrt(a), math.sqrt(1-a))
    d = R * c
    return d

# Returns the lat/long of a destination point given the start lat, long, aziuth, distance
def getDestinationLatLong(lat,lng,azimuth,distance):
    R = 6378.1 #Radius of the Earth in km
    brng = math.radians(azimuth) #Bearing is degrees converted to radians.
    d = distance/1000 #Distance m converted to km

    lat1 = math.radians(lat) #Current dd lat point converted to radians
    lon1 = math.radians(lng) #Current dd long point converted to radians

    lat2 = math.asin( math.sin(lat1) * math.cos(d/R) + math.cos(lat1)* math.sin(d/R)* math.cos(brng))

    lon2 = lon1 + math.atan2(math.sin(brng) * math.sin(d/R)* math.cos(lat1),
                        math.cos(d/R)- math.sin(lat1)* math.sin(lat2))

    #convert back to degrees
    lat2 = math.degrees(lat2)
    lon2 = math.degrees(lon2)

    return[lat2, lon2]

# End of first link's acquired code


# The following was taken from this link
# https://gist.github.com/jeromer/2005586

# Calculates the azimuth given two points
# Takes in a tuple, returns a float (between 0 and 360)
def calculate_initial_compass_bearing(pointA, pointB):

    lat1 = math.radians(pointA[0])
    lat2 = math.radians(pointB[0])

    diffLong = math.radians(pointB[1] - pointA[1])

    x = math.sin(diffLong) * math.cos(lat2)
    y = math.cos(lat1) * math.sin(lat2) - (math.sin(lat1)
                                           * math.cos(lat2) * math.cos(diffLong))

    initial_bearing = math.atan2(x, y)

    # Now we have the initial bearing but math.atan2 return values
    # from -180 to + 180 which is not what we want for a compass bearing
    # The solution is to normalize the initial bearing as shown below
    initial_bearing = math.degrees(initial_bearing)
    compass_bearing = (initial_bearing + 360) % 360

    return compass_bearing

# End of second link's acquired code
# --------------------------------------------------------------------------------------------------


# Creates a route and returns it to the user
@app.route('/route', methods=['POST'])
def route():


    rds_host = 'hiddengemsdb.cp1ydngf7sx0.us-east-1.rds.amazonaws.com'
    name = 'HiddenGems'
    password = 'Stargazing1'
    db_name = 'hiddengemsdb'

    conn = pymysql.connect( host=rds_host, user=name, passwd=password, db=db_name, autocommit=True, connect_timeout=15)
    cur = conn.cursor()

    # gmaps = googlemaps.Client(key='AIzaSyDTo1GrHUKKmtBiVw4xBQxD1Uv24R1ypvY')
    gmaps = googlemaps.Client(key='AIzaSyD_O6TM3vX-EbHpsSwVu-DPsfCxRar7xJo')

    request = app.current_request
    input = request.json_body

    phone_id = input["phone_id"]
    start_place_id = input["start_place_id"]
    start_date = input["start_date"]
    end_place_id = input["end_place_id"]
    end_date = input["end_date"]
    budget = float(input["budget"])
    radius = int(input["radius"]) / 0.62137119 * 1000 # this must be in meters
    keywords = ((input["keywords"])[1:-1]).split(', ')

    # Format multiple keywords for API search
    parsed_keywords = ""
    for i in range(len(keywords)):
        if i == 0:
            parsed_keywords = "(" + keywords[0] + ")"
        else:
            parsed_keywords = parsed_keywords + " OR (" + keywords[i] + ")"

    # Get place data on start and end locations
    start = gmaps.place(place_id = start_place_id)
    end = gmaps.place(place_id = end_place_id)

    start_address = start['result']['formatted_address']
    start_lat = start['result']['geometry']['location']['lat']
    start_long = start['result']['geometry']['location']['lng']
    end_address = end['result']['formatted_address']
    end_lat = end['result']['geometry']['location']['lat']
    end_long = end['result']['geometry']['location']['lng']

    # Calculate length of trip
    d = getPathLength(start_lat,start_long,end_lat,end_long)

    # Calculate time of trip (provided dates are given)
    if (start_date and end_date):
        strp_start_date = datetime.datetime.strptime(start_date, '%m%d%Y')
        strp_end_date = datetime.datetime.strptime(end_date, '%m%d%Y')
        days = abs((strp_end_date - strp_start_date).days)

        # Calculate number of stops needed
        if (days == 0):
            stops = int(d / 240000) - 1 # Basically a stop roughly every 300 km or something like that
        else:
            stops = (days + 1) * 2 # Start + end date inclusive, two stops per day


    # Calculate number of stops (provided no dates)
    else:
        stops = max(int(d / 240000) - 1, 1)

    # Generate midpoints

    interval = d / (stops + 1) # Amount of distance between any two midpoints on the trip
    azimuth = calculate_initial_compass_bearing((start_lat, start_long), (end_lat, end_long))

    coords = []
    remainder, dist = math.modf((d / interval))
    # coords.append([start_lat,start_long])
    # for distance in range(1, int(dist), int(interval)):
    distance = interval
    for i in range(stops):
        c = getDestinationLatLong(start_lat,start_long,azimuth,distance)
        coords.append(c)
        distance += interval
    # coords.append([end_lat,end_long])

    # Call Google Maps API at each midpoint for attractions and return them in the right format
    results = {
        'coords': coords,
        'keywords': keywords,
        'parsed_keywords': parsed_keywords,
        'places': []
    }

    # Insert start point information
    data = {'place_id': start_place_id,
            'name': start_address,
            'latitude': start_lat,
            'longitude': start_long,
            'rating': 0,
            'orig_lat': start_lat,
            'orig_long': start_long,
            'index': 0,
            'date': strp_start_date.strftime("%m%d%Y") if start_date else ''
    }
    results['places'].append(data)

    # sql = "INSERT INTO hiddengemsdb.Places(place_id, name, latitude, longitude) VALUES(%s, %s, %s, %s);"
    # cur.execute(sql, (start_place_id, start_address, start_lat, start_long))


    # Get midpoints information
    for i in range(len(coords)):
        nearby = gmaps.places_nearby(keyword = parsed_keywords, location = coords[i], radius = radius)
        if (len(nearby['results']) > 0) :
            # for j in range(len(nearby['results'])):
            nearest = nearby['results'][0]
            data = {'place_id': nearest['place_id'],
                    'name': nearest['name'],
                    'latitude': nearest['geometry']['location']['lat'],
                    'longitude': nearest['geometry']['location']['lng'],
                    'rating': nearest['rating'] if 'rating' in nearest else 0,
                    'orig_lat': coords[i][0],
                    'orig_long': coords[i][1],
                    'index': 0,
                    'date': (strp_start_date + datetime.timedelta(days=i/2)).strftime("%m%d%Y") if start_date else ''
                    }
            results['places'].append(data)

    # Insert end point information
    data = {'place_id': end_place_id,
            'name': end_address,
            'latitude': end_lat,
            'longitude': end_long,
            'rating': 0,
            'orig_lat': end_lat,
            'orig_long': end_long,
            'index': 0,
            'date': strp_end_date.strftime("%m%d%Y") if start_date else ''
    }
    results['places'].append(data)


    '''
    Deprecated code from when finding midpoints was really badly done
    
    if (stops < 1):
        mid_lat = (start_lat + end_lat) / 2.0
        mid_long = (start_long + end_long) / 2.0
        nearby = gmaps.places_nearby(keyword = 'attraction', location = [mid_lat, mid_long], radius = 30000)
        nearest = nearby['results'][0]
        data = {'place_id': nearest['place_id'],
                'name': nearest['name'],
                'latitude': nearest['geometry']['location']['lat'],
                'longitude': nearest['geometry']['location']['lng'],
                'rating': nearest['rating'] if 'rating' in nearest else 0,
                'orig_lat': mid_lat,
                'orig_long': mid_long,
                'index': 0
        }
        results['places'].append(data)

    else:
        mid_lat = start_lat
        mid_long = start_long
        inc_lat = (end_lat - start_lat) / float(stops + 1)
        inc_long = (end_long - start_long) / float(stops + 1)
        for i in range(0, stops):
            mid_lat += inc_lat
            mid_long += inc_long
            nearby = gmaps.places_nearby(keyword = 'attraction', location = [mid_lat, mid_long], radius = 30000)
            if (len(nearby['results']) > 0):
                nearest = nearby['results'][0]
                data = {'place_id': nearest['place_id'],
                        'name': nearest['name'],
                        'latitude': nearest['geometry']['location']['lat'],
                        'longitude': nearest['geometry']['location']['lng'],
                        'rating': nearest['rating'] if 'rating' in nearest else 0,
                        'orig_lat': mid_lat,
                        'orig_long': mid_long,
                        'index': 0
                        }
                results['places'].append(data)
    '''

    # cur.close()
    conn.close()
    return results


# Updates one particular point in the route with
@app.route('/update', methods=['POST'])
def update():
    # gmaps = googlemaps.Client(key='AIzaSyDTo1GrHUKKmtBiVw4xBQxD1Uv24R1ypvY')
    gmaps = googlemaps.Client(key='AIzaSyD_O6TM3vX-EbHpsSwVu-DPsfCxRar7xJo')

    request = app.current_request
    place = request.json_body

    nearby = gmaps.places_nearby(keyword = 'attraction', location = [place['orig_lat'], place['orig_long']], radius = 50000)
    # for i in range(0, len(nearby['results'])):
    # if (nearby['results'][i]['place_id'] != place['place_id']):

    nearest = nearby['results'][1] # [int(place['index']) + 1]
    data = {'place_id': nearest['place_id'],
            'name': nearest['name'],
            'latitude': nearest['geometry']['location']['lat'],
            'longitude': nearest['geometry']['location']['lng'],
            'rating': nearest['rating'] if 'rating' in nearest else 0,
            'orig_lat': place['orig_lat'],
            'orig_long': place['orig_long'],
            'index': place['index'] + 1
    }
    results['places'].append(data)
    return data


# Saves/updates/loads user preferences
@app.route('/prefs', methods=['GET','POST'])
def prefs():
    request = app.current_request
    prefs = request.json_body

    return 0


# Saves/updates route.
@app.route('/route/save', methods=['POST'])
def save():
    # accepting request format:
    #   {
    #       phone_id

    #       start_lat
            # start_long
            # end_lat
            # end_long

            # start_date
            # end_date

            # budget
            # radius
            # preferences
            # stops: [{ lat, long, place_id, stop_date}]
    #   }

    rds_host = 'hiddengemsdb.cp1ydngf7sx0.us-east-1.rds.amazonaws.com'
    name = 'HiddenGems'
    password = 'Stargazing1'
    db_name = 'HiddenGems'

    conn = pymysql.connect( host=rds_host, user=name, passwd=password, db=db_name, autocommit=True, connect_timeout=15)

    gmaps = googlemaps.Client(key='AIzaSyDTo1GrHUKKmtBiVw4xBQxD1Uv24R1ypvY')

    request = app.current_request
    input = request.json_body

    phone_id = input["phone_id"] # maybe typecast to int
    start_place_id = input["start_place_id"]
    # start_long = float(input["start_long"])
    start_date = input["start_date"]
    end_place_id = input["end_place_id"]
    # end_long = float(input["end_long"])
    end_date = input["end_date"]

    budget = input["budget"] # maybe typecast to float
    radius = input["radius"] # maybe typecast to int
    keywords = input["keywords"]
    stops = input["places"]

    sql = conn.cursor()
    sql.execute("INSERT IGNORE INTO Users (phone_id) VALUES (%s);", (phone_id))
    
    sql = conn.cursor()
    sql.execute( "INSERT IGNORE INTO Routes (phone_id, start_date, end_date, budget, radius) VALUES (%s, %s, %s, %s, %s);", (phone_id, start_date, end_date, budget, radius)) 
    
   
    sql = conn.cursor()
    sql.execute("SELECT route_id FROM Routes WHERE phone_id = '%s'" %(phone_id))
    route_id = sql.fetchall()[0]


    for keyword in keywords:
        sql = conn.cursor()
        sql.execute("INSERT IGNORE INTO Keywords(route_id, keyword) VALUES (%s, %s);", (route_id, keyword))

    for i in range(len(stops)):
        orig_lat = float(stops[i]['orig_lat'])
        orig_long = float(stops[i]['orig_long'])
        lat = float(stops[i]['latitude'])
        lng = float(stops[i]['longitude'])
        stop_id = float(i)
        # index = stops[i]["index"]
        place_id = stops[i]['placeid']
        # stop_date = stops[i]['stop_date'] ????
        name = stops[i]['name']
        rating = stops[i]['rating']
        sql2 = conn.cursor()
        sql2.execute("INSERT IGNORE INTO Places(place_id, name, latitude, longitude) VALUES (%s, %s, %s, %s);", (place_id, name, lat, lng))
        sql = conn.cursor()
        # sql.execute("INSERT INTO Stops(route_id, place_id, stop_id, stop_date, orig_latitude, orig_longitude) VALUES (?,?,?,?,?,?);", (route_id, place_id, i, stop_date, orig_lat, orig_long))
        # sql.execute("INSERT INTO Stops(route_id, place_id, stop_id, orig_latitude, orig_longitude) VALUES (%s, %s, %s, %s, %s) ON DUPLICATE KEY UPDATE place_id '" + place_id + "' or;", (route_id, place_id, stop_id, orig_lat, orig_long))
        sql.execute("REPLACE INTO Stops(route_id, place_id, stop_id, orig_latitude, orig_longitude) VALUES (%s, %s, %s, %s, %s);", (route_id, place_id, stop_id, orig_lat, orig_long))

    done = {
        'done': 'true'
    }
       
    return done 


# Loads route
@app.route('/route/load/{phone_id}/{route_id}', methods=['GET'])
def load(phone_id, route_id):

    rds_host = 'hiddengemsdb.cp1ydngf7sx0.us-east-1.rds.amazonaws.com'
    name = 'HiddenGems'
    password = 'Stargazing1'
    db_name = 'HiddenGems'
    # gmaps = googlemaps.Client(key='AIzaSyDTo1GrHUKKmtBiVw4xBQxD1Uv24R1ypvY')
    gmaps = googlemaps.Client(key='AIzaSyD_O6TM3vX-EbHpsSwVu-DPsfCxRar7xJo')
    #
    conn = pymysql.connect( host=rds_host, user=name, passwd=password, db=db_name, autocommit=True, connect_timeout=15)
    
    # request = app.current_request
    # input = request.json_body
    
    # phone_id = input['phone_id']
    
    #start and end location, start and end dates, stops, preferences, long/lat, place ID, place name, description


    results = {
        "route_id" : route_id,
        "places": {},
        "keywords": [],
        "budget": -1,
        "start_date":"",
        "end_date":"",
        "radius": 10,
        "index": 0,
        "isDriving": 'false'
    }
    
    #get basics from Route table FOR ONE ROUTE PER PHONEID
    # sql = conn.cursor()
    # sql.execute("SELECT * FROM Routes WHERE phone_id = (%s);", (phone_id))
    # r = sql.fetchall()
    # return r;
    # results["start_date"] = r[2]
    # results["end_date"] = r[3]
    # results["budget"] = r[4]
    # results["radius"] = r[5]
    # results["index"] = r[7]
    # results["route_id"] = r[0]
    
    #get basics from Route table FOR MORE THAN ONE ROUTE PER PHONEID
    sql = conn.cursor()
    sql.execute("SELECT * FROM Routes WHERE phone_id = (%s) AND route_id = (%s);", (phone_id, route_id))
    r = sql.fetchall()[0]

    results["start_date"] = r[2]
    results["end_date"] = r[3]
    results["budget"] = r[4]
    results["radius"] = r[5]
    results["index"] = r[7]
    results["route_id"] = r[0]
    
    # #get keywords for route
    sql= conn.cursor()
    sql.execute("SELECT keyword FROM Keywords WHERE route_id = (%s);" (route_id))
    r = sql.fetchall()
    for keyword in r:
         results["keywords"].append(keyword)

    # #place: 
    # # long, lat, 
    # # name, 
    # # place id, stop id,  
    # # stopDate, 
    # # orig_latitude, orig_longitude

    # #get details and reviews later?

    
    #get all information from stops, place in "places" dictionary in order
    sql= conn.cursor()
    sql.execute("SELECT * FROM Stops WHERE phone_id = (%s) ORDER BY stop_id ASC;", (route_id))
    r = sql.fetchall()

    sql2 = conn.cursor()
    sql2.execute("SELECT * FROM Places;")
    r2 = sql2.fetchall()

    for row1 in r:
        tempDict = {}
        stop_id = row1[2]
        place_id = row1[1]
        for row2 in r2:
            if row2[0] == place_id:
                tempDict = {
                    "place_id" : place_id,
                    "name" : row2[1],
                    "stop_date" : row1[3],
                    "orig_latitude" : row1[4],
                    "orig_longitude" : row1[5],
                    "latitude" : row2[2],
                    "longitude" : row2[3]
                }
            break
        results["places"][stop_id] = tempDict
    return results


@app.route('/nearby', methods=['GET'])
def nearby():
    # gmaps = googlemaps.Client(key='AIzaSyDTo1GrHUKKmtBiVw4xBQxD1Uv24R1ypvY')
    gmaps = googlemaps.Client(key='AIzaSyD_O6TM3vX-EbHpsSwVu-DPsfCxRar7xJo')

    request = app.current_request
    input = request.json_body

    lat = float(input["lat"])
    long = float(input["long"])
    term = input["term"]

    nearby = gmaps.places_nearby(keyword = term, location = [lat, long], radius = 50000)

    results = {
        'places': []
    }

    for i in range(len(nearby['results'])):
        nearest = nearby['results'][i]
        data = {'place_id': nearest['place_id'],
            'name': nearest['name'],
            'latitude': nearest['geometry']['location']['lat'],
            'longitude': nearest['geometry']['location']['lng'],
            'rating': nearest['rating'] if 'rating' in nearest else 0
        }
        results['places'].append(data)

    return results


@app.route('/describe', methods=['POST'])
def describe():
    # gmaps = googlemaps.Client(key='AIzaSyDTo1GrHUKKmtBiVw4xBQxD1Uv24R1ypvY')
    gmaps = googlemaps.Client(key='AIzaSyD_O6TM3vX-EbHpsSwVu-DPsfCxRar7xJo')

    request = app.current_request
    place = request.json_body
    results = {'hello': 'world'}

    place_id = 'ChIJd8BlQ2BZwokRAFUEcm_qrcA'
    desc = gmaps.place(place_id = place_id)

    # results.append(desc)
    return desc['result']['geometry']['location']['lat']


# @app.route('/fuckyou', methods=['POST'])
# def fuckyou():
#     rds_host = 'hiddengemsdb.cp1ydngf7sx0.us-east-1.rds.amazonaws.com'
#     name = 'HiddenGems'
#     password = 'Stargazing1'
#     db_name = 'hiddengemsdb'
#
#
#     # this was causing problems, will fix soon.
#     conn = pymysql.connect(rds_host, user=name, passwd=password, db=db_name, autocommit=True, connect_timeout=15)
#
#     cur = conn.cursor()
#     i = 11
#     l = "hello"
#     j = 12.5
#     k = 13.5
    #     sql = "INSERT INTO hiddengemsdb.Places(place_id, name, latitude, longitude) VALUES(%s, %s, %s, %s);"
    #     cur.execute(sql, (i, "hello", j, k))
#
#     cur.close()
#     conn.close()
#
#     return 0