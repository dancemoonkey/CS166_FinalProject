--query1--
--List date, comment, and bill for all closed requests with bill lower than 100
SELECT closeDate, mechComments, finalBill
FROM ServiceRequest
WHERE finalBill < 100 AND status = 'closed';

--query2--
--List first and last name of customers having more than 20 different cars
SELECT C.firstName, C.lastName
FROM Customer C, Car CS
WHERE C.customerID = CS.ownerID
GROUP BY C.customerID, C.firstName, C.lastName
HAVING COUNT(CS.VIN) > 20;

--query3--
--List Make, Model, and Year of all cars build before 1995 having less than 50000 miles
SELECT DISTINCT CS.Make, CS.Model, CS.Year
FROM Car CS, ServiceRequest S
WHERE S.associatedCarVIN = CS.VIN 
AND CS.Year < 1995
AND S.odometerReading < 50000;

--query4--
--List the make, model and number of service requests for the first k cars with the highest number of service orders.
--The k value should be positive and larger than 0. The user should provide this value. Focus on the open service requests.
SELECT CS.Make, CS.Model, COUNT(S.requestID) AS num_requests
FROM Car CS, ServiceRequest S
WHERE CS.VIN = S.associatedCarVIN
AND S.status = 'open'
GROUP BY CS.VIN, CS.Make, CS.Model
ORDER BY num_requests DESC
limit ?;

--query5--
--List the first name, last name and total bill of customers in descending order of their total bill for all cars brought to the mechanic.
SELECT C.firstName, C.lastName, SUM(S.finalBill) AS total_bill
FROM Customer C, ServiceRequest S
WHERE C.customerID = S.customerID
AND S.status = 'closed'
GROUP BY C.customerID, C.firstName, C.lastName
ORDER BY total_bill DESC;