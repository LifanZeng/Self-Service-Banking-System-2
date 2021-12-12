import java.io.FileInputStream;
import java.sql.DriverManager;
import java.util.ArrayList;
import java.util.Properties;
import java.util.Scanner;

public class P2 {
    public static void main(String args[]){
            System.out.println(":: PROGRAM START");
            if (args.length < 1) {
                System.out.println("Need database properties filename");
            } else {
                BankingSystem.init(args[0]);
                BankingSystem.testConnection();
                System.out.println();
                ArrayList<String> methodParams = new ArrayList<String>();
                String selection, selection_c, selection_0;
                do{
                    System.out.println("Main Menu--Welcome to the Self Services banking System!");
                    System.out.println("1. New Customer\n2. Customer Login\n3. Exit");
                    Scanner in = new Scanner(System.in);
                    selection = in.next();
                    switch (selection){
                        case "1":
                            System.out.println("To create a new customer");
                            System.out.println("Please input your information follow the order: Name->Gender->Age->Pin:");
                            System.out.println("Name(should be letters or space), Gender(M or F), Age(numbers), Pin(numbers)");
                            Scanner input = new Scanner(System.in);
                            String name, gender, age, pin;
                            name = input.nextLine();
                            gender = input.nextLine();
                            age = input.nextLine();
                            pin = input.nextLine();
                            methodParams.add(name);
                            methodParams.add(gender);
                            methodParams.add(age);
                            methodParams.add(pin);
                            executeMethod("#newCustomer", methodParams);
                            methodParams.clear();
                            break;
                        case "2":
                            System.out.println("Please input your Customer ID and PIN:");
                            Scanner input_ln = new Scanner(System.in);
                            String id_ln, pin_ln;
                            int isLogin=0;
                            id_ln=input_ln.nextLine();
                            pin_ln=input_ln.nextLine();
                            if(id_ln.equals("0") && pin_ln.equals("0")){    //Screen #4
                                do{
                                System.out.println("Administrator Main Menu");
                                System.out.println("1. Account Summary for a Customer\n" +
                                        "2. Report A::Customer Information with Total Balance in Decreasing Order\n" +
                                        "3. Report B::Find the Average Total Balance Between Age Groups\n"+
                                        "4. Exit");
                                Scanner input_0 = new Scanner(System.in);
                                selection_0 = input_0.next();
                                switch(selection_0){
                                    case "1":
                                        System.out.println("Account summary");
                                        System.out.println("Please input Customer ID:");
                                        Scanner input_A = new Scanner(System.in);
                                        String cusID;
                                        cusID = input_A.nextLine();
                                        methodParams.add(cusID);
                                        executeMethod("#accountSummary", methodParams);
                                        methodParams.clear();
                                        break;
                                    case "2":
                                        System.out.println("Report A::Customer Information with Total Balance in Decreasing Order");
                                        executeMethod("#reportA", methodParams);
                                        methodParams.clear();
                                        break;
                                    case "3":
                                        System.out.println("Report B::Find the Average Total Balance Between Age Groups");
                                        System.out.println("Please input the Min age, then Max age. They should be numbers:");//String min, String max
                                        Scanner input_3 = new Scanner(System.in);
                                        String min, max;
                                        min = input_3.nextLine();
                                        max = input_3.nextLine();
                                        methodParams.add(min);
                                        methodParams.add(max);
                                        executeMethod("#reportB", methodParams);
                                        methodParams.clear();
                                        break;
                                    default:
                                        break;
                                }
                                }while (!selection_0.equals("4"));       // exit do-while for Administrator Main Menu
                            }
                            methodParams.add(id_ln);
                            methodParams.add(pin_ln);
                            isLogin=BankingSystem.logIn(methodParams.get(0), methodParams.get(1));
                            methodParams.clear();
                            if(isLogin==0){
                                System.out.println("Your customer ID and PIN do not exist or do not match. Try again.");
                                break;
                            }
                        do{
                            System.out.println("Customer Main Menu");
                            System.out.println("1. Open Account\n2. Close Account\n3. Deposit\n4. Withdraw\n5. Transfer\n6. Account Summary\n7. Exit");
                            Scanner input_c = new Scanner(System.in);
                            selection_c=input_c.next();
                            switch (selection_c){
                                case "1":
                                    System.out.println("To open a new account");
                                    System.out.println("Please input the information follow the order: Customer ID->Initial amount->Type:");
                                    System.out.println("Customer ID(Numbers. If no, to open a new customer), Initial amount(Numbers. >=0), Type(C for Checking; S for Saving)");
                                    Scanner input_o = new Scanner(System.in);
                                    String id, amount, type;
                                    id = input_o.nextLine();
                                    amount = input_o.nextLine();
                                    type = input_o.nextLine();
                                    methodParams.add(id);
                                    methodParams.add(amount);
                                    methodParams.add(type);
                                    executeMethod("#openAccount", methodParams);
                                    methodParams.clear();
                                    break;
                                case "2":
                                    System.out.println("To close an account");
                                    System.out.println("Please input your Account Number:");
                                    Scanner input_cc = new Scanner(System.in);
                                    String accNum;
                                    accNum = input_cc.nextLine();
                                   // methodParams.add(id_ln);
                                    methodParams.add(id_ln);
                                    methodParams.add(accNum);
                                    int isOwned=BankingSystem.IsOwned(methodParams.get(0), methodParams.get(1));
                                    methodParams.clear();
                                    if(isOwned==0){
                                        System.out.println("This account is not under your Customer ID. You cannot close this account");
                                        break;
                                    }
                                    methodParams.add(accNum);
                                    executeMethod("#closeAccount", methodParams);
                                    methodParams.clear();
                                    break;
                                case "3":
                                    System.out.println("To deposit money to account");
                                    System.out.println("Please input your information follow the order: Account number->Deposit Amount:");
                                    System.out.println("Account number(your account number), Amount(numbers, >=0)");
                                    Scanner input_d = new Scanner(System.in);
                                    String accNum_d, amount_d;
                                    accNum_d = input_d.nextLine();
                                    amount_d = input_d.nextLine();
                                    methodParams.add(accNum_d);
                                    methodParams.add(amount_d);
                                    executeMethod("#deposit", methodParams);
                                    methodParams.clear();
                                    break;
                                case "4":
                                    System.out.println("To withdraw money from account");
                                    System.out.println("Please input your information follow the order: Account number->Withdraw Amount:");
                                    System.out.println("Account number(your account number), Amount(numbers, >=0)");
                                    Scanner input_w = new Scanner(System.in);
                                    String accNum_w, amount_w;
                                    accNum_w = input_w.nextLine();
                                    amount_w = input_w.nextLine();
                                    //-------------------------------------
                                    methodParams.add(id_ln);
                                    methodParams.add(accNum_w);
                                    int isOwned_w=BankingSystem.IsOwned(methodParams.get(0), methodParams.get(1));
                                    methodParams.clear();
                                    if(isOwned_w==0){
                                        System.out.println("This account is not under your Customer ID. You cannot withdraw from this account");
                                        break;
                                    }
                                    //-------------------------------------
                                    methodParams.add(accNum_w);
                                    methodParams.add(amount_w);
                                    executeMethod("#withdraw", methodParams);
                                    methodParams.clear();
                                    break;
                                case "5":
                                    System.out.println("To transfer money from source account to destination account");
                                    System.out.println("Please input your information follow the order: Source account->Destination account->Transfer amount:");
                                    Scanner input_t = new Scanner(System.in);
                                    String srcAccNum_t, destAccNum_t, amount_t;
                                    srcAccNum_t = input_t.nextLine();
                                    destAccNum_t = input_t.nextLine();
                                    amount_t = input_t.nextLine();
                                    //---------------------------------------------------
                                    methodParams.add(id_ln);
                                    methodParams.add(srcAccNum_t);
                                    int isOwned_t=BankingSystem.IsOwned(methodParams.get(0), methodParams.get(1));
                                    methodParams.clear();
                                    if(isOwned_t==0){
                                        System.out.println("This source account is not under your Customer ID. You cannot transfer from this account");
                                        break;
                                    }
                                    //---------------------------------------------------
                                    methodParams.add(srcAccNum_t);
                                    methodParams.add(destAccNum_t);
                                    methodParams.add(amount_t);
                                    executeMethod("#transfer", methodParams);
                                    methodParams.clear();
                                    break;
                                case "6":
                                    System.out.println("Account summary");
                                    System.out.println("Please input your Customer ID:");
                                    Scanner input_A = new Scanner(System.in);
                                    String cusID;
                                    cusID = input_A.nextLine();
                                    methodParams.add(cusID);
                                    executeMethod("#accountSummary", methodParams);
                                    methodParams.clear();
                                    break;
                                default:
                                    break;
                            }
                        }while (!selection_c.equals("7"));  // exit do-while for Customer Main Menu
                        break;                              // Welcome menu, case "2"-break
                        default:
                            break;                          // Welcome menu, default-break
                    }
                }while (!selection.equals("3"));            // exit for Welcome menu
            }

            System.out.println(":: PROGRAM END");
    }



//    /**
//     * Run batch input using properties file.
//     * @param filename properties filename
//     */
//    public static void run(String filename) {
//        String methodName = "";
//        ArrayList<String> methodParams = new ArrayList<String>();
//        try {
//            // Extract batch input from property file.
//            Properties props = new Properties();
//            FileInputStream input = new FileInputStream(filename);
//            props.load(input);
//            String value = props.getProperty("p1.batch.input");
//            // Parse input for method names and parameters.//   not mine
//            String[] tokens = value.split(",");
//            for (int i = 0; i < tokens.length; i++) {
//                if (tokens[i].charAt(0) == '#' && methodName == "") {
//                    methodName = tokens[i];
//                }
//                else if (tokens[i].charAt(0) == '#' && methodName != "") {
//                    for(String s: methodParams){
//                        System.out.println("?^"+s);
//                    }
//                    executeMethod(methodName, methodParams);                //* Execute when meet next '#'.
//                    methodName = tokens[i];
//                    methodParams.clear();
//                }
//                else {
//                    methodParams.add(tokens[i]);
//                }
//            }
//            if (methodName != "") {
//                executeMethod(methodName, methodParams);
//            }
//        } catch (Exception e) {
//            e.printStackTrace();
//        }
//    }

    /**
     * Execute method with name and parameters.
     * @param methodName name of method to execute
     * @param methodParams list of parameters to pass to method
     */
    private static void executeMethod(String methodName, ArrayList<String> methodParams) {
        switch (methodName) {
            case "#newCustomer":
                BankingSystem.newCustomer(methodParams.get(0), methodParams.get(1), methodParams.get(2), methodParams.get(3));
                break;
            case "#openAccount":
                BankingSystem.openAccount(methodParams.get(0), methodParams.get(1), methodParams.get(2));
                break;
            case "#closeAccount":
                BankingSystem.closeAccount(methodParams.get(0));
                break;
            case "#deposit":
                BankingSystem.deposit(methodParams.get(0), methodParams.get(1));
                break;
            case "#withdraw":
                BankingSystem.withdraw(methodParams.get(0), methodParams.get(1));
                break;
            case "#transfer":
                BankingSystem.transfer(methodParams.get(0), methodParams.get(1), methodParams.get(2));
                break;
            case "#accountSummary":
                BankingSystem.accountSummary(methodParams.get(0));
                break;
            case "#reportA":
                BankingSystem.reportA();
                break;
            case "#reportB":
                BankingSystem.reportB(methodParams.get(0), methodParams.get(1));
                break;
            default:
                System.out.println("Could not find method to execute");
                break;
        }
        System.out.println();
    }
}
