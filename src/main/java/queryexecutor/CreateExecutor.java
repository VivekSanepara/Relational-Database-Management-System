package queryexecutor;

import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import querytranslation.QueryToken;

import java.io.*;
import java.util.Arrays;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

public class CreateExecutor {
    Lock lock = new ReentrantLock();
    static final Logger logger = LoggerFactory.getLogger(CreateExecutor.class);

    String path = "src/main/java/files/";
    String databasePath = "src/main/java/files/databases.json";
    static CreateExecutor instance = null;

    private boolean databaseExists = false;
    private String username = null;
    private String database = null;

    public boolean execute(QueryToken queryToken, String query, String username, String database) {
        lock.lock();
        this.username = username;
        this.database = database;
        logger.info("Checking if database exists!");
        if(queryToken.get("type").equals("database")){
            lock.unlock();
            return createDB(queryToken);
        }else{
            lock.unlock();
            return createTable(queryToken,query, username, database);
        }
    }

    private boolean createDB(QueryToken queryToken) {
        // executor uses this HashMap to do its task.
        String name = (String) queryToken.get("name");
        String path = this.path + name;
        File file = new File(path);
        System.out.println(path);
        boolean bool = file.mkdir();
        if(bool){
            System.out.println("DB created successfully");
            logger.info("DB "+name+" created successfully!");
            parseDBFile(name);
        }else{
            System.out.println("Sorry couldn’t create DB");
        }
        return true;
    }

    private boolean createTable(QueryToken internalQuery, String query, String username, String database) {
    	query = query.replaceAll(";", "");
        query = query.replaceAll(",", " ");
        query = query.replaceAll("[^a-zA-Z ]", "");
        String[] sqlWords = query.split(" ");
        int primaryIndex = sqlWords.length-1;
        int foreignIndex = 0;

        JSONObject colObj = new JSONObject();
        JSONObject meta = new JSONObject();
        JSONObject indexes = new JSONObject();
        JSONArray  data = new JSONArray();

        
        if(query.toLowerCase().contains("primary key")) {
        	String[] primaryKeys = Arrays.copyOfRange(sqlWords, primaryIndex, primaryIndex+3);
        	JSONObject primaryJson = new JSONObject();
        	primaryJson.put("type","primary");
        	indexes.put(primaryKeys[2],primaryJson);
        }

        sqlWords = Arrays.copyOfRange(sqlWords, 0, primaryIndex);
        System.out.println(sqlWords);
        
        for(int i = 3; i< sqlWords.length; i+=2) {
        	colObj.put(sqlWords[i], sqlWords[i+1]);
        }
        logger.info("Adding indexes to table!");
        logger.info("Adding columns to table!");
        String tableName = (String) internalQuery.get("name");
        JSONObject tableObj = new JSONObject();
        
        tableObj.put("columns",colObj);
        tableObj.put("meta",meta);
        tableObj.put("indexes",indexes);
        tableObj.put("data",data);

		try (FileWriter file = new FileWriter(path + database +"/"+tableName+".json")) {
		    file.write(tableObj.toJSONString());
		    file.flush();
		    logger.info("Table "+tableName+" created successfully!");
		} catch (IOException e) {
		    e.printStackTrace();
		}
        return true;
    }

    private void parseDBFile(String name) {
        databaseExists = false;
        JSONParser parser = new JSONParser();
        try (FileReader reader = new FileReader(databasePath)) {
            //Read JSON file
            Object obj = parser.parse(reader);

            JSONArray dblist = (JSONArray) obj;
            dblist.forEach(db -> {
                System.out.println(db);
                if(((JSONObject) db).get("name") == name) {
                    if (((JSONObject) db).get("username") == username) {
                        databaseExists = true;
                    }
                }
            });
            if(!databaseExists) {
                JSONObject dbObj = new JSONObject();
                dbObj.put("name", name);
                dbObj.put("username", username);
                dblist.add(dbObj);
                try (FileWriter file = new FileWriter(databasePath)) {
                    file.write(dblist.toJSONString());
                    file.flush();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        } catch (FileNotFoundException e) {
            e.printStackTrace();

        } catch (IOException e) {
            e.printStackTrace();

        } catch (ParseException e) {
            e.printStackTrace();

        }
    }
}
