package login;

import java.util.Scanner;

public class LoginInput {

    private String user = null;
    private Scanner scanner;

    public LoginInput() {
        scanner = new Scanner(System.in);
    }

    public String Login(){
        if(user == null){

            System.out.println("Authentication Stage: ");
            System.out.print("UserName:");
            String userName = scanner.nextLine();
            System.out.print("Password:");
            String password = scanner.nextLine();
            user = verification(userName, password);
        }
        return user;
    }

    public String verification(String userName, String password) {
        Boolean loginSuccess = false;

        final String[] users = {"vivek","mansi","sowmya"};
        final String[] pass = {"xkxgm","ocpuk","uqyo{c"};
        int result;

        char[] passChar = password.toCharArray();
        for(int i=0; i<passChar.length;i++) {
            result=passChar[i]+2;
            passChar[i] = (char)result;
            System.out.println(passChar[i]);
        }
        password = String.valueOf(passChar);
        for(int i = 0;i< users.length;i++) {
            if(users[i].equals(userName) && pass[i].equals(password)) {
                loginSuccess = true;
            }
        }

        if (loginSuccess) {
            System.out.println("Login Successful !!");
            return userName;
        } else {
            System.out.println("Invalid Credentials !!");
        }
        return null;
    }
}
