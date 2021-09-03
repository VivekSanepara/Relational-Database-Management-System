package queryexecutor;


import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.File;
import java.nio.file.Path;
import java.nio.file.Paths;

public class DropDatabaseExecutor {

    static final Logger logger = LoggerFactory.getLogger(DropDatabaseExecutor.class);

    private String username = null;
    private String database = null;

    public boolean execute(String username, String database) {
        this.username = username;
        this.database = database;
        Path path = Paths.get("src/main/java/files/" + database);
        File dbPath = new File(path.toString());
        dbPath.delete();
        logger.info("Database dropped!");
        return true;
    }

}
