import datadictionarygenerator.DataDictionaryGenerator;
import erdgenerator.ERDGenerator;
import login.LoginInput;
import sqldump.SQLDump;
import java.util.Scanner;

public class Main {
    private static Scanner sc;

    public static void main(String[] args) {
        System.out.println ("###########################");
        System.out.println ("##   CSCI-5408 Project   ##");
        System.out.println ("###########################");

        InputQuery InputQuery = new InputQuery();
        LoginInput loginInput = new LoginInput();
        sc = new Scanner (System.in);

        String username = loginInput.Login();

        if (username != null) {
            selectMenu(InputQuery, username);
        }
    }

    private static void selectMenu(InputQuery InputQuery, String username) {
        System.out.println ("Choose from one of the operations");
        System.out.println ("1. Enter Query");
        System.out.println ("2. SQL Dump");
        System.out.println ("3. Generate ERD");
        System.out.println ("4. Generate data dictionary");
        String userInput = sc.nextLine ();
        switch (userInput) {
            case "1":
                InputQuery.QueryInput(username);
                break;
            case "2":
                SQLDump dump = new SQLDump ();
                System.out.println ("Enter database name");
                String databaseName = sc.nextLine ();
                dump.generateSQLDump (username, databaseName);
                break;
            case "3":
                ERDGenerator erdObj = new ERDGenerator ();
                System.out.println ("Enter a database name");
                String database = sc.nextLine ();
                erdObj.generateERD (username, database);
                break;
            case "4":
                DataDictionaryGenerator dataDictionaryGenerator = new DataDictionaryGenerator ();
                System.out.println ("Enter Database: ");
                String db = sc.nextLine ();
                dataDictionaryGenerator.generate (username, db);
                break;
            default:
                System.out.println ("Invalid input!");

        }
    }
}
