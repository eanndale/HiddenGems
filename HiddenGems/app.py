from chalice import Chalice
import pymysql
import googlemaps
import json

app = Chalice(app_name='HiddenGems')
app.debug = True

@app.route('/')
def index():
    return {'hello': 'world'}

# AIzaSyDWFJbIJ5zpvow8AT41ejH-g17qT8YBQYw -> old Google Maps API key

@app.route('/route', methods=['POST'])
def route():
    rds_host = "hiddengemsdb.cp1ydngf7sx0.us-east-1.rds.amazonaws.com"
    name = "hiddengemsdb"
    password = "Stargazing1"
    db_name = "hiddengems"

    # this was causing problems, will fix soon.
    # conn = pymysql.connect(rds_host, user=name, passwd=password, db=db_name, connect_timeout=5)

    gmaps = googlemaps.Client(key='AIzaSyDTo1GrHUKKmtBiVw4xBQxD1Uv24R1ypvY')

    request = app.current_request
    points = request.json_body

    # start_address = points["start_address"]
    # end_address = points["end_address"]

    start_lat = float(points["start_lat"])
    start_long = float(points["start_long"])
    end_lat = float(points["end_lat"])
    end_long = float(points["end_long"])

    # note this actually works
    # directions = gmaps.directions(origin=[start_lat, start_long], destination=[end_lat, end_long], mode="driving")
    # return directions

    # Really bad implementation of midpoints (not accounting for optimal routing)
    # ------------------------------------------------------------------------------
    directions = gmaps.directions(origin=[start_lat, start_long], destination=[end_lat, end_long], mode="driving")
    direct = json.dumps(directions)
    directer = json.loads(direct[1:-1])
    conv_fac = 0.621371
    meters = int(directer['legs'][0]['distance']['value'])
    miles = float(meters) / 1000.0 * conv_fac
    stops = int(miles / 150) - 1

    '''
    R = 6373.0 # radius of Earth in km

    lat1 = radians(start_lat)
    
    long1 = radians(start_long)
    lat2 = radians(end_lat)
    long2 = radians(end_long)

    dlong = long2 - long1
    dlat = lat2 - lat1

    a = sin(dlat / 2)**2 + cos(lat1) * cos(lat2) * sin(dlong / 2)**2
    c = 2 * arctan2(sqrt(a), sqrt(1 - a))

    distance = R * c

    stops = distance / 150 - 1
    '''

    results = {
        'places': []
    }

    if (stops < 1):
        mid_lat = (start_lat + end_lat) / 2.0
        mid_long = (start_long + end_long) / 2.0
        nearby = gmaps.places_nearby(keyword = 'attraction', location = [mid_lat, mid_long], radius = 50000)
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
        inc_lat = abs(end_lat - start_lat) / float(stops + 1)
        inc_long = abs(end_long - start_long) / float(stops + 1)
        for i in range(0, stops):
            mid_lat += inc_lat
            mid_long += inc_long
            nearby = gmaps.places_nearby(keyword = 'attraction', location = [mid_lat, mid_long], radius = 50000)
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
    return results

    # use google maps to find route between points
    # choose intermediate points on route every hundred? miles
    # make searches around those places for cool stuff
    # add those locations to the database provided they are unique
    # return the list of locs

    # deletion can be done from frontend (just delete point marked)

    # getting a different point just involves searching again.

@app.route('/update', methods=['POST'])
def update():
    gmaps = googlemaps.Client(key='AIzaSyDTo1GrHUKKmtBiVw4xBQxD1Uv24R1ypvY')

    request = app.current_request
    place = request.json_body
    data = {}

    nearby = gmaps.places_nearby(keyword = 'attraction', location = [place['orig_lat'], place['orig_long']], radius = 50000)
    if (len(nearby['results']) > 1):
        nearest = nearby['results'][place['index'] + 1]
        data = {'place_id': nearest['place_id'],
                'name': nearest['name'],
                'latitude': nearest['geometry']['location']['lat'],
                'longitude': nearest['geometry']['location']['lng'],
                'rating': nearest['rating'] if 'rating' in nearest else 0,
                'orig_lat': place['orig_lat'],
                'orig_long': place['orig_long'],
                'index': place['index'] + 1
                }
    return data


@app.route('/describe', methods=['GET'])
def describe():
    gmaps = googlemaps.Client(key='AIzaSyDTo1GrHUKKmtBiVw4xBQxD1Uv24R1ypvY')

    request = app.current_request
    place = request.json_body
    results = {}

    desc = gmaps.place(place_id = place['place_id'])

    return results