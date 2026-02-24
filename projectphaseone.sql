--drop tables as a safety measure
DROP TABLE IF EXISTS ServiceRequest;
DROP TABLE IF EXISTS Car;
DROP TABLE IF EXISTS Mechanic;
DROP TABLE IF EXISTS Customer;

--create customer table, nothing too special here. just translate from the ER diagram
CREATE TABLE Customer (
    customerID INTEGER,
    firstName TEXT NOT NULL,
    lastName TEXT NOT NULL,
    Phone TEXT,
    Address TEXT,
    PRIMARY KEY (customerID)
);

--create car table
--since each car must be owned by one customer and a customer can own many cars, we have a one-to-many relationship
--between customer and car
--so we add customer as a foreign key in Car, and this also means we don't need a separate "owns" table
CREATE TABLE Car (
    VIN TEXT,
    Make TEXT NOT NULL,
    Model TEXT NOT NULL,
    Year INTEGER NOT NULL,
    ownerID INTEGER NOT NULL,
    PRIMARY KEY (VIN),
    FOREIGN KEY (ownerID) REFERENCES Customer(customerID)
);

--create mechanic table, nothing too special on this one
CREATE TABLE Mechanic (
    employeeID INTEGER,
    firstName TEXT NOT NULL,
    lastName TEXT NOT NULL,
    yearsofExp INTEGER,
    PRIMARY KEY (employeeID)
);

--create servicerequest table. this one is the most complicated
CREATE TABLE ServiceRequest (
    --first, the easy part: the attributes
    requestID INTEGER,
    openDate DATE,
    closeDate DATE,
    odometerReading INTEGER,
    custComments TEXT,
    mechComments TEXT,
    finalBill DECIMAL,
    status TEXT,

    --customerID represents the "initiates" relationship
    --a customer can initiate many service requests, but a service request must be initiated by one customer
    --one-to-many relationship, so we add customer as a foreign key to servicerequest  
    customerID INTEGER NOT NULL,
    --same logic here, a car can have many service requests and a request must be associated with one car, so a one-to-many relationship
    --this would be the "has" relationship between car and service request on the ER diagram
    associatedCarVIN TEXT NOT NULL,
    --"exactly one mechanic can create a service request" means a request must be created by one mechanic
    --and a mechanic can create many service requests. one-to-many relationship
    --represents the "creates" relationship between mechanic and service request
    mechanicWhoCreated INTEGER NOT NULL,
    --a mechanic can close many requests and a request must be closed by exactly one mechanic
    --one-to-many relationship, represents the "closes" relationship between mechanic and service request
    --note: we don't do "NOT NULL" here. when a request is created, we do need to know who created it, but a mechanic
    --doesn't close the request until later. it wouldn't make sense to set the mechanic who closes the request
    --as soon as the request is created
    mechanicWhoClosed INTEGER,

    --and from here we just set the primary key and add the foreign keys
    PRIMARY KEY (requestID),
    FOREIGN KEY (customerID) REFERENCES Customer(customerID),
    FOREIGN KEY (associatedCarVIN) REFERENCES Car(VIN),
    FOREIGN KEY (mechanicWhoCreated) REFERENCES Mechanic(employeeID),
    FOREIGN KEY (mechanicWhoClosed) REFERENCES Mechanic(employeeID)
);