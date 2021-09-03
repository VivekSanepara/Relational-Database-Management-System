package queryexecutor;

import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import querytranslation.QueryToken;

import java.io.FileReader;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;

public class UseExecutor {
    static final Logger logger = LoggerFactory.getLogger(UseExecutor.class);

    String path = "src/main/java/files/databases.json";

    private String username = null;
    private String database = null;

    public String getDatabase() {
        return database;
    }


    public boolean execute(QueryToken queryToken, String username) {
        this.username = username;
        System.out.println(username);
        String db1 = (String) queryToken.get("database");
        boolean dbPresent = false;
       // Processing whether database belongs to authorised use or not.
        logger.info("identifying database is authorised to user or not.");
        JSONParser parser = new JSONParser();
        // Implementation done by referring to https://howtodoinjava.com/java/library/json-simple-read-write-json-examples/
        try (FileReader reader = new FileReader(path)) {
            Object fileObj = parser.parse(reader);
            JSONArray dblist = (JSONArray) fileObj;
            for (Object db : dblist) {
                JSONObject row = (JSONObject) db;
            // executor uses this HashMap to do its task.
                if (row.get("name").equals(db1)) {
                    if (!row.get("username").equals(username)) {
                        System.out.println("Not authorised to use this database.");
                        return false;
                    }
                    dbPresent = true;
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }

        if (!dbPresent) {
            System.out.println("Database doesn't exist.");
            return false;
        }

        this.database = db1;
        Path path = Paths.get("src/main/java/files/" + this.database);
        if (Files.exists(path)) {
            return true;
        } else {
            System.out.println("Database not found.");
            return false;
        }
    }
}
