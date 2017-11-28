from chalice import Chalice
import pymysql
import googlemaps
import json
import math
from datetime import datetime

app = Chalice(app_name='HiddenGems')
app.debug = True


@app.route('/')
def index():
    return {'hello': 'world'}


# --------------------------------------------------------------------------------------------------
# The following was taken from this link
# https://gis.stackexchange.com/questions/157693/getting-all-vertex-lat-long-coordinates-every-1-meter-between-two-known-points

# Calculates the distance between two lat, long coordinate pairs
def getPathLength(lat1,lng1,lat2,lng2):
    R = 6371000 # radius of earth in m
    lat1rads = math.radians(lat1)
    lat2rads = math.radians(lat2)
    deltaLat = math.radians((lat2-lat1))
    deltaLng = math.radians((lng2-lng1))
    a = math.sin(deltaLat/2) * math.sin(deltaLat/2) + math.cos(lat1rads) *
    math.cos(lat2rads) * math.sin(deltaLng/2) * math.sin(deltaLng/2)
    c = 2 * math.atan2(math.sqrt(a), math.qrt(1-a))
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

'''
# Return every coordinate pair between two coordinate pairs given the desired interval
def main(interval,azimuth,lat1,lng1,lat2,lng2):
    coords = []
    d = getPathLength(lat1,lng1,lat2,lng2)
    remainder, dist = math.modf((d / interval))
    counter = 1.0
    coords.append([lat1,lng1])
    for distance in xrange(1,int(dist)):
        c = getDestinationLatLong(lat1,lng1,azimuth,counter)
        counter += 1.0
        coords.append(c)
    counter +=1
    coords.append([lat2,lng2])
    return coords
'''

# End of first link's acquired code

# Beginning of second link's acquired code

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
    # from -180° to + 180° which is not what we want for a compass bearing
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

    gmaps = googlemaps.Client(key='AIzaSyDTo1GrHUKKmtBiVw4xBQxD1Uv24R1ypvY')

    request = app.current_request
    input = request.json_body

    phone_id = input["phone_id"]
    start_lat = float(input["start_lat"])
    start_long = float(input["start_long"])
    start_date = input["start_date"]
    end_lat = float(input["end_lat"])
    end_long = float(input["end_long"])
    end_date = input["end_date"]

    budget = float(input["budget"])
    radius = int(input["radius"])
    preferences = input["preferences"]


    # Calculate length of trip
    miles = getPathLength(start_lat,start_long,end_lat,end_long)

    # Calculate time of trip (provided dates are given)
    if (start_date and end_date):
        datetime.strptime(start_date, '%m%d%Y')
        datetime.strptime(end_date, '%m%d%Y')
        days = abs((end_date - start_date).days)

        # Calculate number of stops needed
        if (days == 0):
            stops = int(miles / 150) - 1
            if (stops % 2 == 1):
                stops += 1 # For midpoint calcs, there must be an even number of
        else:
            stops = (days + 1) * 2
            if (stops % 2 == 1):
                stops += 1


    # Calculate number of stops (provided no dates)
    else:
        stops = int(miles / 150) - 1

    # Generate midpoints

    interval = miles / (stops + 1)

    coords = []
    remainder, dist = math.modf((d / interval))
    counter = 1.0
    coords.append([lat1,lng1])
    for distance in xrange(1,int(dist)):
        c = getDestinationLatLong(lat1,lng1,azimuth,counter)
        counter += 1.0
        coords.append(c)
    counter +=1
    coords.append([lat2,lng2])
    return coords

    # Call Google Maps API at each midpoint for attractions and return them in the right format
    results = {
        'places': []
    }

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
    # ------------------------------------------------------------------------------

    '''
    return {
        "route": [
            {"lat": start_lat, "long": start_long},
            {"lat": end_lat, "long": end_long}
        ]
    }

    return {
        "route": [
            {"lat": 41.938133300, "long": -87.667014700},
            {"lat": 43.617336100, "long": -116.931019700},
            {"lat": 34.063456, "long": -118.399293},
        ]
    }
    '''

    '''
    # Note: this can be subbed with places_nearby, etc.
    nearby = gmaps.places(query = 'pizza', location = [start_lat, start_long], radius = 300)
    return nearby
    '''

    # cur.close()
    conn.close()
    return results


# Updates one particular point in the route with
@app.route('/update', methods=['POST'])
def update():
    gmaps = googlemaps.Client(key='AIzaSyDTo1GrHUKKmtBiVw4xBQxD1Uv24R1ypvY')

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
    db_name = 'hiddengemsdb'

    conn = pymysql.connect( host=rds_host, user=name, passwd=password, db=db_name, autocommit=True, connect_timeout=15)

    gmaps = googlemaps.Client(key='AIzaSyDTo1GrHUKKmtBiVw4xBQxD1Uv24R1ypvY')

    request = app.current_request
    input = request.json_body

    phone_id = input["phone_id"] # maybe typecast to int
    start_lat = float(input["start_lat"])
    start_long = float(input["start_long"])
    start_date = input["start_date"]
    end_lat = float(input["end_lat"])
    end_long = float(input["end_long"])
    end_date = input["end_date"]

    budget = input["budget"] # maybe typecast to float
    radius = input["radius"] # maybe typecast to int
    preferences = input["preferences"]
    stops = input["stops"]

    sql = conn.cursor()
    sql.execute( "INSERT INTO Routes (phone_id, start_date, end_date, budget, radius) VALUES (?, ?, ?, ?, ?)", (phone_id, start_date, end_date, budget, radius)) 
    
    sql = conn.cursor()
    sql.execute("SELECT route_id FROM Routes WHERE phone_id = '%s'" %(phone_id))
    route_id = sql.fetchall()
    for i in range(len(stops)):
        lat = float(stops[i]['lat'])
        lng = float(stops[i]['lng'])
        place_id = stops[i]['place_id']
        stop_date = stops[i]['stop_date']
        sql = conn.cursor("INSERT INTO Stops(route_id, place_id, stop_id, stop_date, orig_latitude, orig_longitude) VALUES (?,?,?,?,?,?)", (route_id, place_id, i, stop_date, lat, lng))
    
    return 0


# Loads route
@app.route('/route/load', methods=['GET'])
def load():
    return 0


@app.route('/nearby', methods=['GET'])
def nearby():
    gmaps = googlemaps.Client(key='AIzaSyDTo1GrHUKKmtBiVw4xBQxD1Uv24R1ypvY')

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


@app.route('/describe', methods=['GET'])
def describe():
    gmaps = googlemaps.Client(key='AIzaSyDTo1GrHUKKmtBiVw4xBQxD1Uv24R1ypvY')

    request = app.current_request
    place = request.json_body
    results = {'hello': 'world'}

    desc = gmaps.place(place_id = place['place_id'])
    # results.append(desc)

    return results


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