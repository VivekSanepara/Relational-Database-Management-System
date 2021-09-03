package queryexecutor;

import distributed.RemoteDBConnection;
import distributed.RemoteDBReader;
import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import querytranslation.QueryToken;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.util.List;
import java.util.Set;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

public class SelectExecutor {
    Lock lock = new ReentrantLock();
    static final Logger logger = LoggerFactory.getLogger(SelectExecutor.class);
    String path = "src/main/java/files/";

    private String username = null;
    private String database = null;

    public boolean execute(QueryToken queryToken, String username, String database) throws IOException {
        lock.lock();
        this.username = username;
        this.database = database;

        // executor uses this HashMap to do its task.
        String table = (String) queryToken.get("table");
        String[] columns = (String[]) queryToken.get("columns");
        String conditions = (String) queryToken.get("conditions");
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
                if (data!=null) {
                    System.out.println(data);
                }
            }
        }


        String path = this.path +database+"/"+table+".json";

        // Implementation done by referring to File Reading and writing: https://howtodoinjava.com/java/library/json-simple-read-write-json-examples/
        JSONParser parser = new JSONParser();
        JSONObject obj = null;
        try(FileReader reader = new FileReader(path)){
            obj = (JSONObject) parser.parse(reader);
        } catch (ParseException e) {
            logger.info("parse error");
        } catch (IOException e) {
            logger.info("system crashed");
        }
        JSONObject jsonObject = obj;
        JSONArray rows = (JSONArray) jsonObject.get("data");

        logger.info("Identifying requested columns");
        if(columns.length ==1 && columns[0].equals("*")) {
            JSONObject tblColumns = (JSONObject) jsonObject.get("columns");
            Set<String> keys = tblColumns.keySet();
            columns = new String[keys.size()];
            int index = 0;
            for (String str : keys){
                columns[index++] = str;
            }
        }

        logger.info("Identifying requested conditions");
        if(conditions.length() > 0){
            rows = filterRows(rows,conditions);
        }

        for(int i=0;i<rows.size();i++){
            JSONObject row = (JSONObject) rows.get(i);
            for (String column: columns) {
                column = column.replace(" ","");
                System.out.print(column + " - " + row.get(column) + " | ");
            }
            System.out.println();
        }
        System.out.println();

        logger.info("query executed");
        return true;
    }

    private JSONArray filterRows(JSONArray rows, String conditions){
        JSONArray filteredRows = new JSONArray();
        for(int i=0;i<rows.size();i++){
            JSONObject row = (JSONObject) rows.get(i);
            String[] andConditions = conditions.split(" and ");
            int conditionsSatisfied =0;

            for(int j=0; j<andConditions.length; j++){
                String[] orConditions = andConditions[j].split(" or ");
                boolean satisfied = false;
                for(int k=0; k<orConditions.length; k++){
                    String condition = orConditions[k];
                    if(condition.contains("=")){
                        String[] conditionParts = condition.split("=");
                        String column = conditionParts[0];
                        String value = conditionParts[1].replace("'","").replace("\"","");
                        if(row.get(column).equals(value)){
                            satisfied = true;
                        }
                    }
                }
                if(satisfied){
                    conditionsSatisfied++;
                }
            }
            if(conditionsSatisfied == andConditions.length){
                filteredRows.add(row);
            }
        }
        return filteredRows;
    }
}
