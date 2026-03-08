/*
 * Template JAVA User Interface
 * =============================
 *
 * Database Management Systems
 * Department of Computer Science &amp; Engineering
 * University of California - Riverside
 *
 * Target DBMS: 'Postgres'
 *
 */


import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.ResultSetMetaData;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;

/**
 * This class defines a simple embedded SQL utility class that is designed to
 * work with PostgreSQL JDBC drivers.
 *
 */
public class EmbeddedSQL{

   // reference to physical database connection.
   private Connection _connection = null;

   // handling the keyboard inputs through a BufferedReader
   // This variable can be global for convenience.
   static BufferedReader in = new BufferedReader(
                                new InputStreamReader(System.in));

   /**
    * Creates a new instance of EmbeddedSQL
    *
    * @param hostname the MySQL or PostgreSQL server hostname
    * @param database the name of the database
    * @param username the user name used to login to the database
    * @param password the user login password
    * @throws java.sql.SQLException when failed to make a connection.
    */
   public EmbeddedSQL (String dbname, String dbport, String user, String passwd) throws SQLException {

      System.out.print("Connecting to database...");
      try{
         // constructs the connection URL
         String url = "jdbc:postgresql://localhost:" + dbport + "/" + dbname;
         System.out.println ("Connection URL: " + url + "\n");

         // obtain a physical connection
         this._connection = DriverManager.getConnection(url, user, passwd);
         System.out.println("Done");
      }catch (Exception e){
         System.err.println("Error - Unable to Connect to Database: " + e.getMessage() );
         System.out.println("Make sure you started postgres on this machine");
         System.exit(-1);
      }//end catch
   }//end EmbeddedSQL

   /**
    * Method to execute an update SQL statement.  Update SQL instructions
    * includes CREATE, INSERT, UPDATE, DELETE, and DROP.
    *
    * @param sql the input SQL string
    * @throws java.sql.SQLException when update failed
    */
   public void executeUpdate (String sql) throws SQLException {
      // creates a statement object
      Statement stmt = this._connection.createStatement ();

      // issues the update instruction
      stmt.executeUpdate (sql);

      // close the instruction
      stmt.close ();
   }//end executeUpdate

   /**
    * Method to execute an input query SQL instruction (i.e. SELECT).  This
    * method issues the query to the DBMS and outputs the results to
    * standard out.
    *
    * @param query the input query string
    * @return the number of rows returned
    * @throws java.sql.SQLException when failed to execute the query
    */
   public int executeQuery (String query) throws SQLException {
      // creates a statement object
      Statement stmt = this._connection.createStatement ();

      // issues the query instruction
      ResultSet rs = stmt.executeQuery (query);

      /*
       ** obtains the metadata object for the returned result set.  The metadata
       ** contains row and column info.
       */
      ResultSetMetaData rsmd = rs.getMetaData ();
      int numCol = rsmd.getColumnCount ();
      int rowCount = 0;

      // iterates through the result set and output them to standard out.
      boolean outputHeader = true;
      while (rs.next()){
	 if(outputHeader){
	    for(int i = 1; i <= numCol; i++){
		System.out.print(rsmd.getColumnName(i) + "\t");
	    }
	    System.out.println();
	    outputHeader = false;
	 }
         for (int i=1; i<=numCol; ++i)
            System.out.print (rs.getString (i) + "\t");
         System.out.println ();
         ++rowCount;
      }//end while
      stmt.close ();
      return rowCount;
   }//end executeQuery

   /**
    * Method to close the physical connection if it is open.
    */
   public void cleanup(){
      try{
         if (this._connection != null){
            this._connection.close ();
         }//end if
      }catch (SQLException e){
         // ignored.
      }//end try
   }//end cleanup

   /**
    * The main execution method
    *
    * @param args the command line arguments this inclues the <mysql|pgsql> <login file>
    */
   public static void main (String[] args) {
      if (args.length != 3) {
         System.err.println (
            "Usage: " +
            "java [-classpath <classpath>] " +
            EmbeddedSQL.class.getName () +
            " <dbname> <port> <user>");
         return;
      }//end if
      
      Greeting();
      EmbeddedSQL esql = null;
      try{
         // use postgres JDBC driver.
         Class.forName ("org.postgresql.Driver").newInstance ();
         // instantiate the EmbeddedSQL object and creates a physical
         // connection.
         String dbname = args[0];
         String dbport = args[1];
         String user = args[2];
         esql = new EmbeddedSQL (dbname, dbport, user, "");

         boolean keepon = true;
         while(keepon) {
            // These are sample SQL statements
            System.out.println("MAIN MENU");
            System.out.println("---------");
            System.out.println("0. Add a new customer");
            System.out.println("1. Add a mechanic");
            System.out.println("2. Add a car");
            System.out.println("3. Initiate a service request");
            System.out.println("9. < EXIT");

            switch (readChoice()){
               case 0: AddCustomer(esql); break;
               case 1: AddMechanic(esql); break;
               case 2: AddCar(esql); break;
               case 3: initiateServiceRequest(esql); break;
               case 9: keepon = false; break;
               default : System.out.println("Unrecognized choice!"); break;
            }//end switch
         }//end while
      }catch(Exception e) {
         System.err.println (e.getMessage ());
      }finally{
         // make sure to cleanup the created table and close the connection.
         try{
            if(esql != null) {
               System.out.print("Disconnecting from database...");
               esql.cleanup ();
               System.out.println("Done\n\nBye !");
            }//end if
         }catch (Exception e) {
            // ignored.
         }//end try
      }//end try
   }//end main
   
   public static void Greeting(){
      System.out.println(
         "\n\n*******************************************************\n" +
         "              User Interface      	               \n" +
         "*******************************************************\n");
   }//end Greeting

   /*
    * Reads the users choice given from the keyboard
    * @int
    **/
   public static int readChoice() {
      int input;
      // returns only if a correct value is given.
      do {
         System.out.print("Please make your choice: ");
         try { // read the integer, parse it and break.
            input = Integer.parseInt(in.readLine());
            break;
         }catch (Exception e) {
            System.out.println("Your input is invalid!");
            continue;
         }//end try
      }while (true);
      return input;
   }//end readChoice

   //addcustomer function, which adds a new customer into the database.
   public static void AddCustomer(EmbeddedSQL esql){
      try{
         //variables to store user input
         int customerID;
         String firstName;
         String lastName;
         String Phone;
         String Address;

         //from here, do some error handling that checks if the input is correct.
         while (true) { 
            System.out.print("\tEnter customer ID: ");
            String customerInput = in.readLine().trim();
            if(customerInput.isEmpty()) {
               System.out.println("\tError: customer ID cannot be empty!");
               continue;
            }
            try {
               customerID = Integer.parseInt(customerInput);
               if(customerID <= 0) {
               System.out.println("\tError: customer ID cannot be zero/negative!");
               continue;
            }

            //check if the customerID already exists for extra safety
            //run a query to count how many customers have the same customerID as the one the user just gave
            String checkQuery = "SELECT COUNT(*) FROM Customer WHERE customerID = " + customerID;
            ResultSet rs = esql._connection.createStatement().executeQuery(checkQuery);
            rs.next();
            int count = rs.getInt(1);
            rs.close();
            if(count > 0){
                System.out.println("\tError: customer ID already exists! Please enter a different ID.");
                continue;
            }
            //if we got here, it means the customerID is valid
            break;

         }catch (NumberFormatException e) {
               System.out.print("\tError: customer ID must only be numbers!");
         }
         
      }

      while (true) { 
         System.out.print("\tEnter customer first name: ");
         firstName = in.readLine().trim();
         if(firstName.isEmpty()) {
            System.out.println("\tError: customer first name cannot be empty!");
            continue;
         }
         if(!firstName.matches("[a-zA-Z'-]+")){
            System.out.println("\tError: customer first name must only be letters!");
            continue;
         }
         break;
      }

      while (true) { 
         System.out.print("\tEnter customer last name: ");
         lastName = in.readLine().trim();
         if(lastName.isEmpty()) {
            System.out.println("\tError: customer last name cannot be empty!");
            continue;
         }
         if(!lastName.matches("[a-zA-Z'-]+")){
            System.out.println("\tError: customer last name must only be letters!");
            continue;
         }
         break;
      }
      while (true) { 
         System.out.print("\tEnter customer's phone number: ");
         Phone = in.readLine().trim();
         if(Phone.isEmpty()) {
            System.out.println("\tError: customer phone number cannot be empty!");
            continue;
         }
         //enforces a 10 digit number nothing > / <
         if(!Phone.matches("\\d{10}")){
            System.out.println("\tError: invalid Phone Number!");
            continue;
         }
         break;
      }

      while (true) { 
         System.out.print("\tEnter customer's address: ");
         Address = in.readLine().trim();
         if(Address.isEmpty()) {
            System.out.println("\tError: customer address cannot be empty!");
            continue;
         }
         //ensures its has number and letter in address like 12 lake drive
         if (!Address.matches(".*\\d+.*") || !Address.matches(".*[a-zA-Z]+.*")) {
            System.out.println("\tError: Address must contain both numbers and letters.");
            continue;
        }
         break;
      }

         String query = "INSERT INTO Customer (customerID, firstName, lastName, Phone, Address) VALUES (";
         query += "'" + customerID + "', ";
         query += "'" + firstName + "', ";
         query += "'" + lastName + "', ";
         query += "'" + Phone + "', ";
         query += "'" + Address + "')";

      esql.executeUpdate(query);

      }catch(Exception e){
         System.err.println (e.getMessage());
      }
   }//end AddCustomer

   //addMechanic function, which adds a new mechanic into the database
   public static void addMechanic(EmbeddedSQL esql)
   {
      try {
         //variables to store user input
         int employeeID;
         String firstName;
         String lastName;
         int yearsofExp;

         while (true) { 
            System.out.print("\tEnter employee ID: ");
            //integer.parseINT will strictly only allow integers. excluding - for negative numbers
            //so we need to catch that cases since we cant have '-837211' as a valid employeeID
            String employeeInput = in.readLine().trim();
            if(employeeInput.isEmpty()) {
               System.out.println("\tError: employee ID cannot be empty!");
               continue;
            }
            try {
               employeeID = Integer.parseInt(employeeInput);
               if(employeeID <= 0) {
                  System.out.println("\tError: employee ID cannot be zero/negative!");
                  continue;
               }

                //extra safety check to see if employeeID already exists
                String checkQuery = "SELECT COUNT(*) FROM Mechanic WHERE employeeID = " + employeeID;
                ResultSet rs = esql._connection.createStatement().executeQuery(checkQuery);
                rs.next();
                int count = rs.getInt(1);
                rs.close();
                if(count > 0){
                    System.out.println("\tError: employee ID already exists! Please enter a different ID.");
                    continue;
                }

            break;

         }catch (NumberFormatException e) {
               System.out.println("\tError: employee ID must only be numbers!");
         }
         
      }

         while (true) { 
            System.out.print("\tEnter mechanic first name: ");
            firstName = in.readLine().trim();
            if(firstName.isEmpty()) {
               System.out.println("\tError: employee first name cannot be empty!");
               continue;
            }
            if(!firstName.matches("[a-zA-Z'-]+")){
               System.out.println("\tError: employee first name must only be letters!");
               continue;
            }
            break;
         }

         while (true) { 
            System.out.print("\tEnter mechanic last name: ");
            lastName = in.readLine().trim();
            if(lastName.isEmpty()) {
               System.out.println("\tError: employee last name cannot be empty!");
               continue;
            }
            if(!lastName.matches("[a-zA-Z'-]+")){
               System.out.println("\tError: employee last name must only be letters!");
               continue;
            }
            break;
         }
         
         while (true) { 
            System.out.print("\tEnter employee's years of Experience: ");
            String employeeYOE = in.readLine().trim();
            if(employeeYOE.isEmpty()) {
               System.out.println("\tError: employee YOE cannot be empty!");
               continue;
            }
            try {
               yearsofExp = Integer.parseInt(employeeYOE);
               if(yearsofExp < 0) {
               System.out.println("\tError: employee's YOE cannot be negative!");
               continue;
            }
            break;

         }catch (NumberFormatException e) {
               System.out.println("\tError: employee's YOE must only be numbers!");
         }
      }

         
         String query = "INSERT INTO Mechanic(employeeID, firstName, lastName, yearsOfExp) VALUES (";
         query += "'" + employeeID + "', ";
         query += "'" + firstName + "', ";
         query += "'" + lastName + "', ";
         query += "'" + yearsofExp + "')";
         

         
         esql.executeUpdate(query);
      
   }
      catch(Exception e){
         System.err.println (e.getMessage());
      }
   }//end addMechanic
   
    //addCar function
    //This function should allow for adding a new car into the database. 
    //You should provide an interface that takes as input the information of a new car
    //and checks if the provided information are valid based on the constraints of the database schema.
    public static void AddCar(EmbeddedSQL esql){
        try{
            //set up variables for user input
            String vin;
            String make;
            String model;
            int year;
            int ownerID;

            //get VIN input and validate it
            while(true){
                System.out.print("\tEnter VIN: ");
                //this handles if the user just presses "enter" without actually typing anything
                vin = in.readLine().trim();
                if(vin.isEmpty()){
                    System.out.println("VIN cannot be empty.");
                    continue;
                }

                //VIN is unique since it's a primary key of Car.
                //so we also need to do a uniqueness check for the VIN
                //we run a query to count how many cars have the same VIN
                String vinCheck = "SELECT COUNT(*) FROM Car WHERE VIN = '" + vin + "'";
                ResultSet vinQueryResult = esql._connection.createStatement().executeQuery(vinCheck);
                vinQueryResult.next();
                //if the count is greater than 0, it means a car with the same VIN already exists
                int count = vinQueryResult.getInt(1);
                vinQueryResult.close();
                if(count > 0){
                    System.out.println("A car with this VIN already exists.");
                    continue;
                }
                //if we got here, the VIN is valid and unique! great! break out of the loop
                break;
            }

            //next up, the Make. read input and validate
            while(true){
                System.out.print("\tEnter Make: ");
                //handles if the user just presses "enter" without typing anything
                make = in.readLine().trim();
                if(make.isEmpty()){
                    System.out.println("Make cannot be empty.");
                    continue;
                }
                break;
            }

            //next, the Model. read input and validate
            while(true){
                System.out.print("\tEnter Model: ");
                //again, this handles if user just presses "enter" without typing anything
                model = in.readLine().trim();
                if(model.isEmpty()){
                    System.out.println("Model cannot be empty.");
                    continue;
                }
                break;
            }

            //moving on to Year. read input and validate
            while(true){
                System.out.print("\tEnter Year: ");
                String input = in.readLine().trim();
                //make sure the year is a number. if so, turn the string into an integer.
                if(input.matches("\\d+")){
                    year = Integer.parseInt(input);
                    break;
                } 
                else {
                    System.out.println("Year must be a number.");
                }
            }

            //finally, the ownerID. read input and validate
            while(true){
                System.out.print("\tEnter Owner ID: ");
                String input = in.readLine().trim();
                //make sure the ownerID is a number. if so, turn the string into an integer.
                if(input.matches("\\d+")){
                    ownerID = Integer.parseInt(input);

                    //we also need to make sure the owner actually exists
                    //so we run a query to count how many customers have the same customerID as the ownerID the user gave
                    //ownerID is a foreign key that references customerID in the Customer table
                    //so it has to match an existing customerID
                    String ownerCheck = "SELECT COUNT(*) FROM Customer WHERE customerID = " + ownerID;
                    ResultSet queryResult = esql._connection.createStatement().executeQuery(ownerCheck);
                    queryResult.next();
                    //if the count is 0, it means there is no customer with that customerID
                    //which means the ownerID is invalid
                    int ownerCount = queryResult.getInt(1);
                    queryResult.close();
                    if(ownerCount == 0){
                        System.out.println("Customer does not exist. Add the customer first.");
                        continue;
                    }

                    //if we get here, this means that the ownerID is valid. break out of the loop
                    break;
                } 
                else {
                    System.out.println("Owner ID must be a number.");
                }
            }

            //now that all the inputs are valid, we make the SQL query
            //and insert the new car into the database
            String query = "INSERT INTO Car (VIN, Make, Model, Year, ownerID) VALUES (" + "'" + vin + "', '" + make + "', '" + model + "', " + year + ", " + ownerID + ")";
            esql.executeUpdate(query);
            System.out.println("Car added successfully!");

        }
        catch(Exception e){
            System.err.println("FAILED to add car: " + e.getMessage());
        }
    }

    //initiateServiceRequest function 
    public static void initiateServiceRequest(EmbeddedSQL esql){
      try {
         String lastName;
         while (true) { 
            System.out.print("\tEnter customer's last name: ");
            lastName = in.readLine().trim();
            if(lastName.isEmpty()) {
               System.out.println("\tError: customer last name cannot be empty!");
               continue;
            }
            if(!lastName.matches("[a-zA-Z'-]+")){
               System.out.println("\tError: customer last name must only be letters!");
               continue;
            }
            break;
         }
         //searching db for existing customers.
         String searchQuery = "SELECT customerID, firstName, lastName FROM Customer WHERE lastName = '" + lastName + "'";
         ResultSet searchResult = esql._connection.createStatement().executeQuery(searchQuery);

         //thought about using hashmap but its only better for looking up by ID. 
         //List is the better option here to store the results for now

         List<Integer> customerIDS = new ArrayList<>();
         List<String> customersNames = new ArrayList<>();

         while(searchResult.next())
         {
            customerIDS.add(searchResult.getInt("customerID"));
            customersNames.add(searchResult.getString("firstName") + " " + searchResult.getString("lastName"));
         }
         searchResult.close();

         int selectedCustomer;




         if(customerIDS.isEmpty())
         {
            System.out.println("\tCustomer with last name" + lastName + " not Found.");
            //client application should provide the option of adding a new customer
            int choice = 0;
            System.out.println("Would you like to add a new customer?. 1) Yes 2) No");
            while (true) {
               String choiceInput = in.readLine().trim();
               if (choiceInput.matches("\\d+")) {
                   choice = Integer.parseInt(choiceInput);
                   if (choice == 1 || choice == 2) {
                       break;
                   }
               }
               System.out.println("Invalid choice. Please enter 1 or 2.");
           }
           
           if (choice == 1) {
               AddCustomer(esql);
               ResultSet newResult = esql._connection.createStatement().executeQuery("SELECT MAX(customerID) FROM Customer");
               if (newResult.next()) {
                   selectedCustomer = newResult.getInt(1);
               }
               newResult.close();
           } else {
               System.out.println("Service request cancelled. Goodbye.");
               return;
           }
       }
       else if(customerIDS.size() == 1)
       {
         selectedCustomer = customerIDS.get(0);
         System.out.println("\tMenu:\n " + customersNames.get(0) + " (ID: " + selectedCustomer + ")");

        } else {
            // many customers found with the same last name
            System.out.println("Multiple customers found with last name '" + lastName + "':");
            for (int i = 0; i < customerIDS.size(); i++) {
                System.out.println("\t" + (i + 1) + ". " + customersNames.get(i) + " (ID: " + customerIDS.get(i) + ")");
            }

            // keep asking until the user gives a valid selection
            while (true) {
                System.out.print("\tSelect the customer: ");
                String inputChoice = in.readLine().trim();
                if (inputChoice.matches("\\d+")) {
                    int selection = Integer.parseInt(inputChoice) - 1;
                    if (selection >= 0 && selection < customerIDS.size()) {
                        selectedCustomer = customerIDS.get(selection);
                        break;
                    }
                }
                System.out.println("Invalid selection. Try again.");
            }
        }

         //listing all cars associated with that client
         String carQuery = "SELECT VIN, Make, Model, Year FROM Car WHERE ownerID = " + selectedCustomer;
         ResultSet carResult = esql._connection.createStatement().executeQuery(carQuery);

         List<String> CarVINS = new ArrayList<>();
         List<String> CarDescs = new ArrayList<>();

         while(carResult.next())
         {
            CarVINS.add(carResult.getString("VIN"));
            CarDescs.add(carResult.getString("VIN") + " " +carResult.getString("Make") + " " + carResult.getString("Model") + " " + carResult.getString("Year"));
         }

         carResult.close();

         //option to initiate serreq for one of car thru a vin selection since its primary key
         String SelectedVIN;

         if(CarVINS.isEmpty())
         {
            System.out.println("\tNo cars found for this customer. Please add a car first.");
            AddCar(esql);
            ResultSet newCarResult = esql._connection.createStatement().executeQuery("SELECT VIN FROM Car WHERE OwnerID = " + selectedCustomer + " ORDER BY VIN DESC LIMIT 1");
            if (newCarResult.next()) {
                SelectedVIN = newCarResult.getString(1);
            }
            newCarResult.close();
         }
         else {
            System.out.println("Customers cars:");
            for (int i = 0; i < CarVINS.size(); i++) {
                System.out.println("\t" + (i + 1) + ". " + CarDescs.get(i));
            }
            System.out.println("\t" + (CarVINS.size() + 1) + ". Add a new car");

            while (true) {
                System.out.print("\tSelect the car: ");
                String carChoice = in.readLine().trim();
                if (carChoice.matches("\\d+")) {
                    int carSelection = Integer.parseInt(carChoice) - 1;
                    if(carSelection == CarVINS.size()) {
                        AddCar(esql);
                        ResultSet newCarResult = esql._connection.createStatement().executeQuery("SELECT VIN FROM Car WHERE OwnerID = " + selectedCustomer + " ORDER BY VIN DESC LIMIT 1");
                        if (newCarResult.next()) {
                            SelectedVIN = newCarResult.getString(1);
                        }
                        newCarResult.close();
                        break;
                    }
                    else if (carSelection >= 0 && carSelection < CarVINS.size()) {
                        SelectedVIN = CarVINS.get(carSelection);
                        break;
                    }
                }
                System.out.println("Invalid selection. Try again.");
         }

         //at this point we have the selected customer and the selected car, we can now create a service request for that car
         int requestID = 0;
         //to get the next logical requestID, we get the max requestID in the database and add 1 to it
         //and if there's no request in the database, we start with 1 since requestID is a primary key and cannot be 0.
         ResultSet maxIDResult = esql._connection.createStatement().executeQuery("SELECT COALESCE(MAX(requestID), 0) FROM ServiceRequest");
         if (maxIDResult.next()) {
            requestID = maxIDResult.getInt(1) + 1;
         }
         maxIDResult.close();
         String openDate;
         //String closeDate;
         int odometerReading;
         String custComments;
         //String mechComments;
         //float finalBill;
         //String status;
         int mechanicWhoCreated;

         while(true){
            System.out.print("\tEnter open date (YYYY-MM-DD): ");
            openDate = in.readLine().trim();
            if(openDate.isEmpty()) {
               System.out.print("\tError: open date cannot be empty!");
               continue;
            }
            //format check 
            if(!openDate.matches("\\d{4}-\\d{2}-\\d{2}")) {
               System.out.print("\tError: open date must be in the format YYYY-MM-DD!");
               continue;
            }
            break;
         } 
           
         while (true) { 
            System.out.print("\tEnter odometer reading: ");
            String odometerInput = in.readLine().trim();
            if(odometerInput.isEmpty()) {
               System.out.print("\tError: odometer reading cannot be empty!");
               continue;
            }
            try {
               odometerReading = Integer.parseInt(odometerInput);
               if(odometerReading < 0) {
               System.out.print("\tError: odometer reading cannot be negative!");
               continue;
            }
         }catch (NumberFormatException e) {
            System.out.print("\tError: odometer reading must only be numbers!");
         }
         break;
      }

     
         System.out.print("\tEnter customer comments: "); 
         custComments = in.readLine().trim();

      
         while(true){
            System.out.print("\tEnter the employee ID of the mechanic creating this service request: ");
            String mechIDInput = in.readLine().trim();
            if(mechIDInput.isEmpty()) {
               System.out.print("\tError: mechanic employee ID cannot be empty!");
               continue;
            }
            if(mechIDInput.matches("\\d+")){
               mechanicWhoCreated = Integer.parseInt(mechIDInput);

               //we also need to make sure the mechanic actually exists
               ResultSet MechResult = esql._connection.createStatement().executeQuery("SELECT COUNT(*) FROM Mechanic WHERE employeeID = " + mechanicWhoCreated);
               MechResult.next();

               int mechCount = MechResult.getInt(1);
               MechResult.close();
               if(mechCount == 0){
                   System.out.println("Mechanic does not exist. Add the mechanic first.");
                   continue;
               }
               break;
            }
            else {
               System.out.println("\tError: mechanic employee ID must only be numbers!");
            }
         }
             
         


         String serviceQuery = "INSERT INTO ServiceRequest (requestID, openDate, closeDate, odometerReading, custComments, mechComments, finalBill, status, customerID, associatedCarVIN, mechanicWhoCreated, mechanicWhoClosed) VALUES (" +
         requestID + ", '" +
         openDate + "', NULL, " +
         odometerReading + ", '" +
         custComments + "', NULL, NULL, 'open', " +
         selectedCustomer + ", '" +
         SelectedVIN + "', " +
         mechanicWhoCreated + ", NULL)";


         
         esql.executeUpdate(serviceQuery);
        System.out.println("Service request created successfully with ID: " + requestID);
      
         }
      } catch (Exception e) {
         System.err.println (e.getMessage());
      }

        
    }

   public static void QueryExample(EmbeddedSQL esql){
      try{
         String query = "SELECT * FROM Catalog WHERE cost < ";
         System.out.print("\tEnter cost: $");
         String input = in.readLine();
         query += input;

         int rowCount = esql.executeQuery(query);
         System.out.println ("total row(s): " + rowCount);
      }catch(Exception e){
         System.err.println (e.getMessage());
      }
   }//end QueryExample
   
   public static void Query1(EmbeddedSQL esql){
      try{
         String query = "SELECT S.sid, S.sname, COUNT(C.pid) AS totalParts " +
               "FROM Suppliers S, Catalog C " +
               "WHERE S.sid = C.sid " +
               "GROUP BY S.sid, S.sname " +
               "ORDER BY S.sid;";
         int rowCount = esql.executeQuery(query);
         System.out.println ("total row(s): " + rowCount);
      }catch(Exception e){
         System.err.println (e.getMessage());
      }
   }//end Query1

   public static void Query2(EmbeddedSQL esql){
      try{
         String query = "SELECT S.sid, S.sname, COUNT(C.pid) AS totalParts " +
            "FROM suppliers S, catalog C " +
            "WHERE S.sid = C.sid " +
            "GROUP BY S.sid, S.sname " +
            "HAVING COUNT(C.pid) >= 3 " +
            "ORDER BY S.sid;";

         int rowCount = esql.executeQuery(query);
         System.out.println ("total row(s): " + rowCount);
      }catch(Exception e){
         System.err.println (e.getMessage());
      }
   }//end Query2

   public static void Query3(EmbeddedSQL esql){
      try{
         String query = "SELECT S.sname, COUNT(C.pid) AS totalParts " +
            "FROM Suppliers S, Catalog C, Parts P " +
            "WHERE S.sid = C.sid " +
            "AND C.pid = P.pid " +
            "AND NOT EXISTS ( " +
               "SELECT * " +
               "FROM Catalog C2, Parts P2 " +
               "WHERE C2.sid = S.sid " +
               "AND C2.pid = P2.pid " +
               "AND P2.color <> 'Green' )" +
               "GROUP BY S.sid, S.sname; ";
         
         int rowCount = esql.executeQuery(query);
         System.out.println ("total row(s): " + rowCount);
      }catch(Exception e){
         System.err.println (e.getMessage());
      }
   }//end Query3

   public static void Query4(EmbeddedSQL esql){
      try{
         String query = "SELECT S.sname, MAX(C.cost) AS priceOfMostExpensivePart " +
         "FROM Suppliers S, Catalog C " +
         "WHERE S.sid = C.sid " +
         "AND EXISTS ( " +
         "   SELECT * " +
         "   FROM Catalog C1, Parts P1 " +
         "   WHERE C1.sid = S.sid " +
         "   AND C1.pid = P1.pid " +
         "   AND P1.color = 'Green' " +
         ") " +

         "AND EXISTS ( " +
         "   SELECT * " +
         "   FROM Catalog C2, Parts P2 " +
         "   WHERE C2.sid = S.sid " +
         "   AND C2.pid = P2.pid " +
         "   AND P2.color = 'Red' " +
         ") " +

         "GROUP BY S.sid, S.sname " +
         "ORDER BY S.sname;";


         int rowCount = esql.executeQuery(query);
         System.out.println ("total row(s): " + rowCount);
      }catch(Exception e){
         System.err.println (e.getMessage());
      }
   }//end Query4

   public static void Query5(EmbeddedSQL esql){
      
      try{
         System.out.print("\tEnter cost: $");
         String input = in.readLine();

         String query = "SELECT DISTINCT p.pname " +
         "FROM suppliers S, catalog C, parts P " +
         "WHERE S.sid = C.sid " +
         "AND C.pid = P.pid " + 
         "AND C.cost < " + input + " " + 
        "ORDER BY p.pname ";

         
         //query += input;

         int rowCount = esql.executeQuery(query);
         System.out.println ("total row(s): " + rowCount);
      }catch(Exception e){
         System.err.println (e.getMessage());
      }
   }//end Query5

   public static void Query6(EmbeddedSQL esql){
      try{
          System.out.print("\tEnter part name: $");
         String input = in.readLine();

         String query = "SELECT DISTINCT S.address " +
         "FROM suppliers S, catalog C, parts P " +
         "WHERE P.pid = C.pid " +
         "AND S.sid = C.sid "  + 
         "AND P.pname = '" + input + "' " +
        "ORDER BY S.address; ";

        // query += input;

         int rowCount = esql.executeQuery(query);
         System.out.println ("total row(s): " + rowCount);
      }catch(Exception e){
         System.err.println (e.getMessage());
      }
   }//end Query6

}//end EmbeddedSQL