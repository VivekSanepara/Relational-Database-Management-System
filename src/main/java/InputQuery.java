import querytranslation.QueryEngine;

import java.io.IOException;
import java.util.Scanner;

public class InputQuery {
    private String username = null;
    private Scanner sc;
    private QueryEngine queryEngine;

    public InputQuery() {
        sc = new Scanner(System.in);
        queryEngine = new QueryEngine();
    }

    public void QueryInput(String username) {
        while (true) {
            System.out.println("Enter a SQL query to proceed or press 0 to terminate!");
            String query = sc.nextLine();
            if (query.equals("0")) {
                break;
            }
            try {
                queryEngine.run(query, username);
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        sc.close();
    }
}
