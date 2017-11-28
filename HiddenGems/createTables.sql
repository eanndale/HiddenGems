CREATE TABLE Places (
place_id VARCHAR(100),
name VARCHAR(100) NOT NULL,
latitude FLOAT NOT NULL,
longitude FLOAT NOT NULL,
PRIMARY KEY(place_id)
);

CREATE TABLE Routes (
route_id VARCHAR(100) NOT NULL AUTO_INCREMENT,
phone_id VARCHAR(100) NOT NULL,
name VARCHAR(50),
start_date VARCHAR(10),
end_date VARCHAR(10),
budget FLOAT(10),
radius INT,
PRIMARY KEY(route_id)
);

CREATE TABLE Preferences (
route_id VARCHAR(100) NOT NULL,
preference VARCHAR(20) NOT NULL,
PRIMARY KEY(route_id, preference),
FOREIGN KEY (route_id) REFERENCES Routes(route_id) ON DELETE CASCADE
);

CREATE TABLE Stops (
route_id VARCHAR(100) NOT NULL,
place_id VARCHAR(100) NOT NULL,
stop_id NOT NULL,
PRIMARY KEY(route_id, stop_id),
FOREIGN KEY (route_id) REFERENCES Routes(route_id) ON DELETE CASCADE
);

CREATE TABLE Details (
place_id VARCHAR(100),
description VARCHAR(300),
rating INT,
PRIMARY KEY(place_id),
FOREIGN KEY (place_id) REFERENCES Places(place_id) ON DELETE CASCADE
);

CREATE TABLE Reviews (
phone_id VARCHAR(100) NOT NULL,
place_id VARCHAR(100) NOT NULL,
body VARCHAR(280) NOT NULL,
PRIMARY KEY(phone_id, place_id),
FOREIGN KEY (place_id) REFERENCES Places(place_id) ON DELETE CASCADE
);