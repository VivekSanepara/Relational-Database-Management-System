package queryexecutor;

import distributed.RemoteDBConnection;
import distributed.RemoteDBReader;
import distributed.RemoteDBWriter;
import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import querytranslation.QueryToken;

import java.io.*;
import java.util.List;
import java.util.Set;

public class DeleteExecutor {

    String inFilePath = "src/main/java/files/";
    private String username = null;
    private String database = null;
    static final Logger logger = LoggerFactory.getLogger(DeleteExecutor.class);
    public boolean execute(QueryToken query, String username, String database) throws IOException {
        this.username = username;
        this.database = database;
        int conditionFlag = 0;

        String table = query.getTableName().trim();
        BufferedReader br = new BufferedReader(new FileReader("src/main/java/gdd/GDD.csv"));
        String line = "";
        while ((line = br.readLine()) != null)   //returns a Boolean value
        {
            String[] resp = line.split(",");

            if (resp[1].equals("Local") && resp[0].equals(table))
            {
                logger.info("Query Execution in Local");
            }
            if(resp[1].equals("Remote") && resp[0].equals(table))
            {
                RemoteDBConnection remoteDBConnection = new RemoteDBConnection();
                remoteDBConnection.createConnection();
                RemoteDBWriter remoteDBWriter = new RemoteDBWriter();
                remoteDBWriter.deleteTable(table);
            }
        }

        String x = query.getCondition().replaceAll("[^a-zA-Z1-9]", " ");
        String[] conditions = x.split(" ");

        String path = inFilePath + database + "/" + table + ".json";
        // Implementation done by referring to https://www.tutorialspoint.com/how-can-we-read-a-json-file-in-java
        JSONParser parser = new JSONParser();
        try (FileReader reader = new FileReader(path)) {

            // Retrieve Object from file and Convert it to JSONobject.
            Object fileObj = parser.parse(reader);
            JSONObject jsonFileObject = (JSONObject) fileObj;

            String[] columnArray;
            JSONObject tableColumns = (JSONObject) jsonFileObject.get("columns");
            Set<String> keys = tableColumns.keySet();
            columnArray = new String[keys.size()];
            int index = 0;
            for (String str : keys) {
                columnArray[index++] = str.toLowerCase();
            }

            // Check if column names in the query is valid
            for (String column : columnArray) {
                if (column.equals(conditions[0])) {
                    conditionFlag = 1;
                }
            }

        // Implementation done by referring to https://howtodoinjava.com/java/library/json-simple-read-write-json-examples/
            if (conditionFlag == 1) {
                JSONArray data = (JSONArray) jsonFileObject.get("data");
                for (int i = 0; i < data.size(); i++) {
                    JSONObject row = (JSONObject) data.get(i);
                    if (row.get(conditions[0]).toString().equals(conditions[1].trim())) {
                        data.remove(i);
                        try (Writer out = new FileWriter(path)) {
                            out.write(jsonFileObject.toJSONString());
                        } catch (IOException e) {
                            e.printStackTrace();
                        }
                        System.out.println("Delete operation done.");
                        return true;
                    }
                }

            } else {
                System.out.println("Invalid column name !!");
                return false;
            }
        } catch (FileNotFoundException e) {
            System.out.println("Table not found !!");
        } catch (ParseException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
        System.out.println("cannot perform, please try again.");
        return false;
    }

}
