package querytranslation;

import queryexecutor.*;
import queryparser.*;

import java.io.IOException;

public class QueryEngine {
    UseParser useParser = new UseParser();
    private String database = null;

    public void run(String query, String username) throws IOException {
        QueryToken queryToken = null;
        String action = query.replaceAll(" .*", "");
        action = action.toLowerCase();
        boolean success = false;
        switch (action) {
            case "use":
                queryToken = useParser.parse(query);
                UseExecutor useExecutor = new UseExecutor();
                useExecutor.execute(queryToken, username);
                this.database = useExecutor.getDatabase();
                break;
            case "insert":
                if (isDbSelected()) {
                    InsertParser insertParser = new InsertParser();
                    queryToken = insertParser.parse(query);
                    if (queryToken != null) {
                        InsertExecutor insertExecutor = new InsertExecutor();
                        insertExecutor.execute(queryToken, username, database);
                    }
                }
                break;
            case "select":
                if (isDbSelected()) {
                    SelectParser selectParser = new SelectParser();
                    queryToken = selectParser.parse(query);
                    if (queryToken != null) {
                        SelectExecutor selectExecutor = new SelectExecutor();
                        selectExecutor.execute(queryToken, username, database);
                    }
                }
                break;
            case "update":
                if (isDbSelected()) {
                    UpdateParser updateParser = new UpdateParser();
                    queryToken = updateParser.parse(query);
                    if (queryToken != null) {
                        UpdateExecutor updateExecutor = new UpdateExecutor();
                        updateExecutor.execute(queryToken, username, database);
                    }
                }
                break;
            case "delete":
                if (isDbSelected()) {
                    DeleteParser deleteParser = new DeleteParser();
                    queryToken = deleteParser.parse(query);
                    if (queryToken != null) {
                        DeleteExecutor deleteExecutor = new DeleteExecutor();
                        deleteExecutor.execute(queryToken, username, database);
                    }
                }
                break;
            case "create":
                CreateParser createParser = new CreateParser();
                CreateExecutor createExecutor = new CreateExecutor();
                queryToken = createParser.parse(query);
                if (((String) queryToken.get("type")).equalsIgnoreCase("database")) {
                    createExecutor.execute(queryToken, query, username, database);
                } else {
                    if (isDbSelected()) {
                        createExecutor.execute(queryToken, query, username, database);
                    }
                }
                break;
            case "drop":
                DropDatabaseExecutor dropDatabaseExecutor = new DropDatabaseExecutor();
                dropDatabaseExecutor.execute(username,database);
                break;
            default:
                System.out.println("invalid query!");
        }
    }

    private boolean isDbSelected() {
        if (database == null) {
            System.out.println("No Database is selected.");
            return false;
        } else {
            return true;
        }
    }
}
