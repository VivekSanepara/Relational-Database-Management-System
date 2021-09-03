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

public class InsertExecutor {
    static final Logger logger = LoggerFactory.getLogger(InsertExecutor.class);
    String inFilePath = "src/main/java/files/";
    private String username = null;
    private String database = null;

    public boolean execute(QueryToken queryToken, String username, String database) throws IOException {
        this.username = username;
        this.database = database;
        // executor uses this HashMap to do its task.
        // value is retrived in table, columns and values.
        String table = (String) queryToken.get("table");
        logger.info("Identifying columns and values");
        String[] columns = (String[]) queryToken.get("columns");
        String[] values = (String[]) queryToken.get("values");
        BufferedReader br = new BufferedReader(new FileReader("src/main/java/gdd/GDD.csv"));
        String line = "";
        int remote = 0;
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
                remote = 1;
            }
        }

        logger.info("Equating Number of column and its value.");
        if (columns.length != values.length) {
            System.out.println("Invalid columns and values pair.");
            return false;
        }

        logger.info("File selection.");
        String path = inFilePath + database + "/" + table + ".json";
        //Implementation done by referring to Simple Read Operation of file: https://howtodoinjava.com/java/library/json-simple-read-write-json-examples/
        JSONParser parser = new JSONParser();
        JSONObject fileObj = null;
        try (FileReader reader = new FileReader(path)) {
            fileObj = (JSONObject) parser.parse(reader);
        } catch (ParseException e) {
            logger.info("parse exception");
        } catch (IOException e) {
            logger.info("system crashed");
        }

        //In Update, all data is stored in array. json object of row is added to stored array. then,
        //old array is removed and new stored array is added.

        JSONObject jsonFileObject = fileObj;
        JSONArray data = (JSONArray) jsonFileObject.get("data");
        JSONObject row = new JSONObject();
        for (int i = 0; i < columns.length; i++) {
            row.put(columns[i], values[i]);
        }
        data.add(row);
        jsonFileObject.remove("data");
        jsonFileObject.put("data", data);

        try (Writer out = new FileWriter(path)) {
            out.write(jsonFileObject.toJSONString());
            logger.info("writing to file.");
        } catch (IOException e) {
            e.printStackTrace();
            logger.info("System crashed.");
        }
        if(remote == 1) {
            RemoteDBConnection remoteDBConnection = new RemoteDBConnection();
            remoteDBConnection.createConnection();
            RemoteDBWriter remoteDBWriter = new RemoteDBWriter();
            remoteDBWriter.writeData(table,data);
        }
        logger.info("query executed");
        return true;
    }
}
