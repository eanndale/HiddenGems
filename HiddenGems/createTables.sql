CREATE TABLE Places (
place_id VARCHAR(100),
name VARCHAR(100) NOT NULL,
latitude FLOAT NOT NULL,
longitude FLOAT NOT NULL,
PRIMARY KEY(place_id)
);

CREATE TABLE Users (
phone_id VARCHAR(100) NOT NULL,
PRIMARY KEY(phone_id)
);

CREATE TABLE Routes (
route_id INT AUTO_INCREMENT,
phone_id VARCHAR(100) NOT NULL,
start_date DATETIME NOT NULL,
end_date DATETIME NOT NULL,
budget FLOAT(10) NOT NULL,
radius INT NOT NULL,
isDriving BOOL DEFAULT 0,
index INT,
PRIMARY KEY(route_id),
FOREIGN KEY (phone_id) REFERENCES Users(phone_id) ON DELETE CASCADE
);

CREATE TABLE Keywords (
route_id INT NOT NULL,
keyword VARCHAR(20) NOT NULL,
PRIMARY KEY(route_id, keyword),
FOREIGN KEY (route_id) REFERENCES Routes(route_id) ON DELETE CASCADE
);

CREATE TABLE Stops (
route_id VARCHAR(100) NOT NULL,
place_id VARCHAR(100) NOT NULL,
stop_id NOT NULL,
stop_date DATETIME NOT NULL,
orig_latitude FLOAT NOT NULL,
orig_longitude FLOAT NOT NULL,
PRIMARY KEY(route_id, stop_id),
FOREIGN KEY (route_id) REFERENCES Routes(route_id) ON DELETE CASCADE,
FOREIGN KEY (place_id) REFERENCES Places(place_id) ON DELETE CASCADE
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
