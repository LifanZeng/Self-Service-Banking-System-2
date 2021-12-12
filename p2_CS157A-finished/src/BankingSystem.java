import java.io.FileInputStream;
import java.sql.*;
import java.util.Properties;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Manage connection to database and perform SQL statements.
 */
public class BankingSystem {
    // Connection properties
    private static String driver;
    private static String url;
    private static String username;
    private static String password;

    // JDBC Objects
    private static Connection con;
    private static Statement stmt;
    private static ResultSet rs;

    /**
     * Initialize database connection given properties file.
     * @param filename name of properties file
     */
    public static void init(String filename) {
        try {
            Properties props = new Properties();						// Create a new Properties object
            FileInputStream input = new FileInputStream(filename);	// Create a new FileInputStream object using our filename parameter
            props.load(input);										// Load the file contents into the Properties object
            driver = props.getProperty("jdbc.driver");				// Load the driver
            url = props.getProperty("jdbc.url");						// Load the url
            username = props.getProperty("jdbc.username");			// Load the username
            password = props.getProperty("jdbc.password");			// Load the password
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
    /**
     * Test database connection.
     */
    public static void testConnection() {
        System.out.println(":: TEST - CONNECTING TO DATABASE");
        try {
            Class.forName(driver);
            con = DriverManager.getConnection(url, username, password);
            con.close();
            System.out.println(":: TEST - SUCCESSFULLY CONNECTED TO DATABASE");
        } catch (Exception e) {
            System.out.println(":: TEST - FAILED CONNECTED TO DATABASE");
            e.printStackTrace();
        }
    }

    /**
     * Create a new customer.
     * @param name customer name
     * @param gender customer gender
     * @param age customer age
     * @param pin customer pin
     */
    public static void newCustomer(String name, String gender, String age, String pin)
    {
        System.out.println(":: CREATE NEW CUSTOMER - RUNNING");
        /* insert your code here */
        Pattern pattern = Pattern.compile("[0-9]*");
        Matcher isNum_pin = pattern.matcher(pin);
        if( !isNum_pin.matches()){
            System.out.println("Fail! The input are not numbers");
            return;
        }
        ////////////////////////////////////////////////////////////
        try{
            Class.forName(driver);
            con = DriverManager.getConnection(url, username, password);                  //Create the connection

            CallableStatement storeProc = con.prepareCall("CALL p2.CUST_CRT(?,?,?,?,?,?,?)");
            storeProc.setString(1, name);
            storeProc.setString(2, gender);
            storeProc.setInt(3, Integer.valueOf(age));
            storeProc.setInt(4, Integer.valueOf(pin));
            storeProc.registerOutParameter(5, Types.INTEGER);
            storeProc.registerOutParameter(6, Types.INTEGER);
            storeProc.registerOutParameter(7, Types.CHAR);
            storeProc.executeUpdate();
            int p_id = storeProc.getInt(5);
            int sql_code = storeProc.getInt(6);
            String err_msg = storeProc.getString(7);
            if (sql_code==0)
                System.out.println("Your Customer ID is: " + p_id);
            else
                System.out.println("Fail to create a new customer. The error message is "+err_msg);
        } catch (Exception e){
            System.out.println("Exception in main()");
            e.printStackTrace();
        }
        ///////////////////////////////////////////////////////////////
        System.out.println(":: CREATE NEW CUSTOMER - SUCCESS");
    }

    /**
     * Open a new account.
     * @param id customer id
     * @param balance initial deposit amount
     * @param type type of account
     */
    public static void openAccount(String id, String balance, String type)
    {
        System.out.println(":: OPEN ACCOUNT - RUNNING");
        /* insert your code here */
        ////////////////////////////////////////////////////////////
        try{
            Class.forName(driver);
            con = DriverManager.getConnection(url, username, password);                  //Create the connection

            CallableStatement storeProc = con.prepareCall("CALL p2.ACCT_OPN(?,?,?,?,?,?)");
            storeProc.setInt(1, Integer.valueOf(id));
            storeProc.setInt(2, Integer.valueOf(balance));
            storeProc.setString(3, type);
            storeProc.registerOutParameter(4, Types.INTEGER);
            storeProc.registerOutParameter(5, Types.INTEGER);
            storeProc.registerOutParameter(6, Types.CHAR);
            storeProc.executeUpdate();
            int accNumber = storeProc.getInt(4);
            int sql_code = storeProc.getInt(5);
            String err_msg = storeProc.getString(6);
            if (sql_code==0)
                System.out.println("Your account ID is: " + accNumber);
            else
                System.out.println("Fail to create a new account. The error message is "+err_msg);
        } catch (Exception e){
            System.out.println("Exception in main()");
            e.printStackTrace();
        }
        ///////////////////////////////////////////////////////////////
        System.out.println(":: OPEN ACCOUNT - SUCCESS");
    }

    /**
     * Close an account.
     * @param accNum account number
     */
    public static void closeAccount(String accNum)
    {
        System.out.println(":: CLOSE ACCOUNT - RUNNING");
        /* insert your code here */
        ////////////////////////////////////////////////////////////
        try{
            Class.forName(driver);
            con = DriverManager.getConnection(url, username, password);                  //Create the connection
//            PreparedStatement stat = con.prepareStatement("update p1.account set balance=0, status='I' where number= ?");
//            stat.setInt(1, Integer.valueOf(accNum));
//            stat.executeUpdate();
            CallableStatement storeProc = con.prepareCall("CALL P2.ACCT_CLS(?,?,?)");
            storeProc.setInt(1, Integer.valueOf(accNum));
            storeProc.registerOutParameter(2, Types.INTEGER);
            storeProc.registerOutParameter(3, Types.CHAR);
            storeProc.executeUpdate();
            int sql_code = storeProc.getInt(2);
            String err_msg = storeProc.getString(3);
            if (sql_code==0)
                System.out.println("Close account successfully.");
            else
                System.out.println("Fail to create a new account. The error message is "+err_msg);

        } catch (Exception e){
            System.out.println("Exception in main()");
            e.printStackTrace();
        }
        ///////////////////////////////////////////////////////////////
        System.out.println(":: CLOSE ACCOUNT - SUCCESS");
    }

    /**
     * Deposit into an account.
     * @param accNum account number
     * @param amount deposit amount
     */
    public static void deposit(String accNum, String amount)
    {
        System.out.println(":: DEPOSIT - RUNNING");
        /* insert your code here */
        Pattern pattern = Pattern.compile("[0-9]*");
        Matcher isNum_amount = pattern.matcher(amount);
        Matcher isNum_accNum = pattern.matcher(accNum);
        if( !(isNum_amount.matches() && isNum_accNum.matches() )){
            System.out.println("Fail! The input are not numbers");
            return;
        }
        ////////////////////////////////////////////////////////////
        try{
            Class.forName(driver);
            con = DriverManager.getConnection(url, username, password);                  //Create the connection
//            PreparedStatement stat = con.prepareStatement("update p1.account set balance=balance + ? where number= ?");
//            stat.setInt(1, Integer.valueOf(amount));
//            stat.setInt(2, Integer.valueOf(accNum));
//            stat.executeUpdate();
            CallableStatement storeProc = con.prepareCall("CALL P2.ACCT_DEP(?,?,?,?)");
            storeProc.setInt(1, Integer.valueOf(accNum));
            storeProc.setInt(2, Integer.valueOf(amount));
            storeProc.registerOutParameter(3, Types.INTEGER);
            storeProc.registerOutParameter(4, Types.CHAR);
            storeProc.executeUpdate();
            int sql_code = storeProc.getInt(3);
            String err_msg = storeProc.getString(4);
            if (sql_code==0)
                System.out.println("Deposit successfully.");
            else
                System.out.println("Fail to deposit. The error message is "+err_msg);
        } catch (Exception e){
            System.out.println("Exception in main()");
            e.printStackTrace();
        }
        ///////////////////////////////////////////////////////////////
        System.out.println(":: OPEN ACCOUNT - SUCCESS");
    }

    /**
     * Withdraw from an account.
     * @param accNum account number
     * @param amount withdraw amount
     */
    public static void withdraw(String accNum, String amount)
    {
        System.out.println(":: WITHDRAW - RUNNING");
        /* insert your code here */
        Pattern pattern = Pattern.compile("[0-9]*");
        Matcher isNum_amount = pattern.matcher(amount);
        Matcher isNum_accNum = pattern.matcher(accNum);
        if( !(isNum_amount.matches() && isNum_accNum.matches() )){
            System.out.println("Fail! The input are not numbers");
            return;
        }
        ////////////////////////////////////////////////////////////
        try{
            Class.forName(driver);
            con = DriverManager.getConnection(url, username, password);                  //Create the connection
//            PreparedStatement stat = con.prepareStatement("update p1.account set balance=balance - ? where number= ? AND balance>=?");
//            stat.setInt(1, Integer.valueOf(amount));
//            stat.setInt(2, Integer.valueOf(accNum));
//            stat.setInt(3, Integer.valueOf(amount));
//            stat.executeUpdate();
            CallableStatement storeProc = con.prepareCall("CALL P2.ACCT_WTH(?,?,?,?)");
            storeProc.setInt(1, Integer.valueOf(accNum));
            storeProc.setInt(2, Integer.valueOf(amount));
            storeProc.registerOutParameter(3, Types.INTEGER);
            storeProc.registerOutParameter(4, Types.CHAR);
            storeProc.executeUpdate();
            int sql_code = storeProc.getInt(3);
            String err_msg = storeProc.getString(4);
            if (sql_code==0)
                System.out.println("Withdraw successfully.");
            else
                System.out.println("Fail to withdraw. The error message is "+err_msg);

        } catch (Exception e){
            //System.out.println("Fail to withdraw! Please check whether the amount is bigger than the balance.");
            e.printStackTrace();
        }
        ///////////////////////////////////////////////////////////////
        System.out.println(":: WITHDRAW - SUCCESS");
    }

    /**
     * Transfer amount from source account to destination account.
     * @param srcAccNum source account number
     * @param destAccNum destination account number
     * @param amount transfer amount
     */
    public static void transfer(String srcAccNum, String destAccNum, String amount)
    {
        System.out.println(":: TRANSFER - RUNNING");
        /* insert your code here */
        ////////////////////////////////////////////////////////////
        try{
            Class.forName(driver);
            con = DriverManager.getConnection(url, username, password);                  //Create the connection
            CallableStatement storeProc = con.prepareCall("CALL P2.ACCT_TRX(?,?,?,?,?)");
            storeProc.setInt(1, Integer.valueOf(srcAccNum));
            storeProc.setInt(2, Integer.valueOf(destAccNum));
            storeProc.setInt(3, Integer.valueOf(amount));
            storeProc.registerOutParameter(4, Types.INTEGER);
            storeProc.registerOutParameter(5, Types.CHAR);
            storeProc.executeUpdate();
            int sql_code = storeProc.getInt(4);
            String err_msg = storeProc.getString(5);
            if (sql_code==0)
                System.out.println("Transfer successfully.");
            else
                System.out.println("Fail to transfer. The error message is "+err_msg);
        } catch (Exception e){
            //System.out.println("Fail to transfer! Please check whether the amount is bigger than the balance.");
            e.printStackTrace();
        }
        System.out.println(":: TRANSFER - SUCCESS");
    }

    /**
     * Display account summary.
     * @param cusID customer ID
     */
    public static void accountSummary(String cusID)
    {
        System.out.println(":: ACCOUNT SUMMARY - RUNNING");
        try{
            Class.forName(driver);
            con = DriverManager.getConnection(url, username, password);                  //Create the connection
            stmt = con.createStatement();
            String query0 = "select number, balance from p2.account where id=" + cusID+" AND status='A'";
            rs = stmt.executeQuery(query0);
            System.out.println("NUMBER" + ",\t" + "BALANCE");
            System.out.println("-----------------------------");
            while(rs.next()) {                                                                      //Loop through result set and retrieve contents of each row
                int number = rs.getInt(1);
                int balance = rs.getInt(2);

                System.out.println(number + ",\t" + balance);        //Print out each row's values to the screen
            }
            rs.close();
            System.out.println("-----------------------------");

            String query = "select Name, p2.customer.ID, Sum(balance) as Total from p2.account, p2.customer where (p2.account.id=p2.customer.id AND p2.customer.id=" + cusID + ") group by name, p2.customer.ID Order by Total";
            rs = stmt.executeQuery(query);
            System.out.println("Name" + ",\t" + "ID" + ",\t" + "Total");
            while(rs.next()) {                                                                      //Loop through result set and retrieve contents of each row

                String Name = rs.getString(1);
                int ID = rs.getInt(2);
                int Total = rs.getInt(3);

                System.out.println(Name + ", " + ID + ",\t" + Total);        //Print out each row's values to the screen
            }
            rs.close();

        } catch (Exception e){
            System.out.println("Exception in main()");
            e.printStackTrace();
        }
        ///////////////////////////////////////////////////////////////
        System.out.println(":: ACCOUNT SUMMARY - SUCCESS");
    }

    /**
     * Display Report A - Customer Information with Total Balance in Decreasing Order.
     */
    public static void reportA()
    {
        System.out.println(":: REPORT A - RUNNING");
        /* insert your code here */
////////////////////////////////////////////////////////////
        try{
            Class.forName(driver);
            con = DriverManager.getConnection(url, username, password);                  //Create the connection
            stmt = con.createStatement();

            String query = "select p2.customer.ID,name,gender,age, Sum(balance) as Total from p2.account, p2.customer " +
                    "where p2.account.id=p2.customer.id group by p2.customer.ID, name, gender, age Order by Total DESC";
            rs = stmt.executeQuery(query);
            System.out.println("ID" + "\t" + "NAME" + "\t" + "GENDER" + "\t" + "AGE" + "\t" + "TOTAL" );
            System.out.println("---------------------------------------------------------------------");
            while(rs.next()) {                                                                      //Loop through result set and retrieve contents of each row
                int id = rs.getInt(1);
                String name = rs.getString(2);
                String gender = rs.getString(3);
                int age = rs.getInt(4);
                int total = rs.getInt(5);

                System.out.println(id + "\t" + name + "\t" + gender + "\t" + age + "\t" + total );        //Print out each row's values to the screen
            }
            rs.close();

        } catch (Exception e){
            System.out.println("Exception in main()");
            e.printStackTrace();
        }
        ///////////////////////////////////////////////////////////////
        System.out.println(":: REPORT A - SUCCESS");
    }

    /**
     * Display Report B - Customer Information with Total Balance in Decreasing Order.
     * @param min minimum age
     * @param max maximum age
     */
    public static void reportB(String min, String max)
    {
        System.out.println(":: REPORT B - RUNNING");
        /* insert your code here */
        ////////////////////////////////////////////////////////////
        try{
            Class.forName(driver);
            con = DriverManager.getConnection(url, username, password);                  //Create the connection
            stmt = con.createStatement();

            String query = "select AVG (Total) from (select p2.customer.ID,name,gender,age, Sum(balance) as Total from p2.account, p2.customer " +
                    "where p2.account.id=p2.customer.id group by p2.customer.ID, name, gender, age) where (age>="+min+" AND age<="+max+")";
            rs = stmt.executeQuery(query);

            System.out.println( "AVERAGE" );
            System.out.println("--------------------------------------------");
            while(rs.next()) {                                                                      //Loop through result set and retrieve contents of each row
            int average = rs.getInt(1);
            System.out.println("\t" + average );        //Print out each row's values to the screen
            }
            rs.close();

        } catch (Exception e){
            System.out.println("Exception in main()");
            e.printStackTrace();
        }
        ///////////////////////////////////////////////////////////////
        System.out.println(":: REPORT B - SUCCESS");
    }



    /**
     * Customer log in
     * @param cusID customer ID
     * @param pin maximum age
     */
    public static int logIn(String cusID, String pin)
    {
////////////////////////////////////////////////////////////
        int valid=0;
        try{
            Class.forName(driver);
            con = DriverManager.getConnection(url, username, password);                  //Create the connection
//            stmt = con.createStatement();
//
//            String query = "select count(*) from p1.customer where id="+cusID+" AND pin = "+pin;
//            rs = stmt.executeQuery(query);
//            while(rs.next()) {                                                                      //Loop through result set and retrieve contents of each row
//                count = rs.getInt(1);
//            }
//            rs.close();

            CallableStatement storeProc = con.prepareCall("CALL P2.CUST_LOGIN(?,?,?,?,?)");
            storeProc.setInt(1, Integer.valueOf(cusID));
            storeProc.setInt(2, Integer.valueOf(pin));
            storeProc.registerOutParameter(3, Types.INTEGER);
            storeProc.registerOutParameter(4, Types.INTEGER);
            storeProc.registerOutParameter(5, Types.CHAR);
            storeProc.executeUpdate();
            valid = storeProc.getInt(3);
            int sql_code = storeProc.getInt(4);
            String err_msg = storeProc.getString(5);
            if(sql_code!=0)
                System.out.println("Fail log in. The error message is "+err_msg);
        } catch (Exception e){
            System.out.println("Exception in main()");
            e.printStackTrace();
        }
        return valid;
        ///////////////////////////////////////////////////////////////
    }

    /**
     * Check whether the Customer owns the account
     * @param cusID customer ID
     * @param accNum account number
     */
    public static int IsOwned(String cusID, String accNum)
    {
////////////////////////////////////////////////////////////
        int is_owned=0;
        try{
//            Class.forName(driver);
//            con = DriverManager.getConnection(url, username, password);                  //Create the connection
//            stmt = con.createStatement();
//            String query = "select count(*) from p1.account where id="+cusID+" AND number = "+accNum;
//            rs = stmt.executeQuery(query);
//            while(rs.next()) {                                                                      //Loop through result set and retrieve contents of each row
//                count = rs.getInt(1);
//            }
//            rs.close();
            CallableStatement storeProc = con.prepareCall("CALL P2.IsOwned(?,?,?,?,?)");
            storeProc.setInt(1, Integer.valueOf(cusID));
            storeProc.setInt(2, Integer.valueOf(accNum));
            storeProc.registerOutParameter(3, Types.INTEGER);
            storeProc.registerOutParameter(4, Types.INTEGER);
            storeProc.registerOutParameter(5, Types.CHAR);
            storeProc.executeUpdate();
            is_owned = storeProc.getInt(3);
            int sql_code = storeProc.getInt(4);
            String err_msg = storeProc.getString(5);
            if(sql_code==0)
                is_owned = 1;
            else{
                is_owned=0;
                System.out.println("Fail. The error message is "+err_msg);
            }

        } catch (Exception e){
            System.out.println("Exception in main()");
            e.printStackTrace();
        }
        return is_owned;
        ///////////////////////////////////////////////////////////////
    }
}
