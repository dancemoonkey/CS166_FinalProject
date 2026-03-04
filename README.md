hello there this is our project
# CS166_FinalProject
[CS166_Project_Phase_1.pdf](https://github.com/user-attachments/files/25506161/CS166_Project_Phase_1.pdf)
Constraints, Assumptions, and Explanations
Explanation of Colors:
The given constraints are written in normal black text. These are taken directly from the
project descriptions.
If we make extra assumptions, we write them in blue.
We provide explanations for problematic (impossible to represent/enforce) constraints in
red.
Customer
The customer has a first name, last name, phone number and address.
-
We also assume customers have a customerID attribute which is unique
Customers can own many different cars
Customers may bring any of their cars for service
-
see the assumption and explanation for “initiating many service requests” below
Customers bring their car for service at a certain date indicating to the mechanic if there
is any problem with the car
-
We assume two attributes, opendate (date the car was brought in, we use “open”
to match the “close service request” later) and custComments (customer’s
comments about the car)
-
We also assume a customer can initiate many service requests, and a service
request is initiated by exactly one customer.
Cars
Cars have a unique VIN, a year, a make and a model.
A car has exactly one owner.
A car may have many outstanding service requests.
-
Assuming a service request must be associated with exactly one car
For each outstanding service request, the car has an odometer reading and the date the
car was brought in by the customer.
The service request will be closed when the car is fixed by a mechanic
-
We use a “closes” relationship between a mechanic and a service request to
represent this
-
Assuming a mechanic can close many requests and a request must be closed by
exactly one mechanic
Mechanic
Mechanics have a first name, a last name, an employee id, and some years of
experience.
-
Assuming mechanics have a unique employee id
Mechanics work on a single car at a time.
-
This is not possible to represent or enforce in both the ER diagram and the
relational schema. ER diagrams can only represent which entities are related, not
specific rules such as the given requirement. Similarly, the relational schema
cannot represent this condition.
When a mechanic fixes a car, he/she closes the service request indicating when it was
closed, any comments they have and the final bill.
-
We use the ServiceRequest entity (see details in its section) and create a
“closes” relationship with the mechanic to represent this.
Exactly one mechanic can create a service request.
-
Note: a ServiceRequest is created by a Mechanic (creates) and requested by a
Customer (initiates), which is when the Customer brings in the car to get help
from the mechanic. These are distinct roles and do not conflict.
-
Therefore, we assume a service request must be created by exactly one
mechanic, and a mechanic can create many service requests.
Both open and closed service requests need to be available at any time.
-
We can add a status attribute in the ServiceRequest entity to mark the service
request as open or closed. However, note that this only serves as a partial
representation of the constraint in the ER diagram. Additionally, this is not
possible to enforce in the relational schema.
ServiceRequest
Represents a service request. Has attributes custComments, openDate, closeDate,
odometerReading, status, mechComments, and finalBill. All of this is taken from the
given information.
-
We make an additional assumption that there is also an attribute requestID,
which is its primary key.
