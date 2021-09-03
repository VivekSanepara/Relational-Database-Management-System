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

public class UpdateExecutor {
    static final Logger logger = LoggerFactory.getLogger(UpdateExecutor.class);
    String path = "src/main/java/files/";
    private String username = null;
    private String database = null;
    int remote = 0;
    List<String> data = null;

    public boolean execute(QueryToken queryToken, String username, String database) throws IOException {
        this.username = username;
        this.database = database;
        int columnFlag = 0;
        int conditionFlag = 0;

        logger.info("Identifying requested columns");
        String table = queryToken.getTableName().trim();
        // executor uses query token which contains hashmap to do its task.

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
                RemoteDBReader remoteDBReader = new RemoteDBReader();
                List<String> data = remoteDBReader.readData(table);
            }
        }
        String y = queryToken.getOption().replaceAll("[^a-zA-Z]", " ");
        String[] columnValue = y.split(" ");

        String x = queryToken.getCondition().replaceAll("[^a-zA-Z1-9]", " ");
        String[] conditions = x.split(" ");

        String filePath = path + database + "/" + table + ".json";

        // Implementation done by referring to https://howtodoinjava.com/java/library/json-simple-read-write-json-examples/
        JSONParser parser = new JSONParser();
        try (FileReader reader = new FileReader(filePath)) {

            Object fileObj = parser.parse(reader);
            JSONObject jsonObject = (JSONObject) fileObj;

            String[] columnArray;
            JSONObject tableColumns = (JSONObject) jsonObject.get("columns");
            Set<String> keys = tableColumns.keySet();
            columnArray = new String[keys.size()];
            int index = 0;
            for (String str : keys) {
                columnArray[index++] = str.toLowerCase();
            }

            // Check if column names in the queryToken is valid
            logger.info("Identifying column and condition.");
            for (String column : columnArray) {
                if (column.equals(columnValue[0])) {
                    columnFlag = 1;
                }
                if (column.equals(conditions[0])) {
                    conditionFlag = 1;
                }
            }
            logger.info("Updating value.");
            if (columnFlag == 1 && conditionFlag == 1) {
                JSONArray data = (JSONArray) jsonObject.get("data");
                for (int i = 0; i < data.size(); i++) {
                    JSONObject fileRow = (JSONObject) data.get(i);


                    if (fileRow.get(conditions[0]).toString().equals(conditions[1].trim())) {
                        fileRow.remove(columnValue[0]);
                        fileRow.put(columnValue[0], columnValue[2]);

                        // Implementation done by referring to https://www.tutorialspoint.com/how-to-write-create-a-json-file-using-java#:~:text=write(jsonObject.,into%20a%20file%20named%20output.
                        try (Writer out = new FileWriter(filePath)) {
                            out.write(jsonObject.toJSONString());
                        } catch (IOException e) {
                            e.printStackTrace();
                        }
                        System.out.println("Update done successfully !!");
                        return true;
                    }
                }

            }
            else if (remote == 1) {
                    RemoteDBConnection remoteDBConnection = new RemoteDBConnection();
                    remoteDBConnection.createConnection();
                    RemoteDBWriter remoteDBWriter = new RemoteDBWriter();
                    remoteDBWriter.writeData(table,data);
            }
            else {
                System.out.println("No columns found.");
                return false;
            }
        } catch (FileNotFoundException e) {
            System.out.println("Table not found !!!");
            logger.info("system crashed");
        } catch (ParseException e) {
            e.printStackTrace();
            logger.info("system crashed");
        } catch (IOException e) {
            e.printStackTrace();
            logger.info("system crashed");
        }
        System.out.println("No such condition found.");
        return false;
    }

}
