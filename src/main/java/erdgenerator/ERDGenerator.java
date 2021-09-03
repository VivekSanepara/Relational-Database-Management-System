package erdgenerator;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;

import java.io.*;
import java.util.Iterator;

public class ERDGenerator {
	String path = "src/main/java/files/";
	String erdPath = "src/main/java/erdgenerator/";
	String table1 ="";
	String table2 ="";
	String refColumn ="";
	public void generateERD(String username, String database) {
		String[] fileLocationNames;
		File db = new File(path +database);
        fileLocationNames = db.list();
        JSONParser jsonParser = new JSONParser();
        JSONArray tableData = new JSONArray();
        for (String pathname : fileLocationNames) {
        	try (FileReader reader = new FileReader(path +database+"/"+pathname)) {
                JSONObject tableObj = (JSONObject) jsonParser.parse(reader);
                tableObj.put("name",pathname.replaceAll(".json", ""));
                tableData.add(tableObj);
        	 } catch (FileNotFoundException e) {
                 e.printStackTrace();
             } catch (IOException e) {
                 e.printStackTrace();
             } catch (ParseException e) {
                 e.printStackTrace();
             }
        }
        JSONArray erdtables = new JSONArray();
        JSONArray foreignkeys = new JSONArray();
        tableData.forEach(table -> {
        	JSONObject tableName = new JSONObject();
        	JSONObject tableDetails = new JSONObject();
        	JSONArray relationships = new JSONArray();
        	JSONObject indexes = (JSONObject) ((JSONObject) table).get("indexes");
        	String primaryKey = "";
        	if (!(indexes.toString().equals("{}"))) {

        		for(Iterator iterator = indexes.keySet().iterator(); iterator.hasNext();) {
        			String key = (String) iterator.next();
        		    if (((JSONObject) indexes).get(key) instanceof JSONObject) {
        		    	JSONObject index = (JSONObject) ((JSONObject) indexes).get(key);
        		          if (index.get("type").equals("primary")) {
        		        	  primaryKey = key;
        		          }
        		          if (index.get("type").equals("foreign")) {
        		        	  JSONObject foreignKey = new JSONObject();
        		        	  foreignKey.put(((JSONObject) table).get("name"),index);
        		        	  tableDetails.put("foreignKey", index);
        		        	  foreignkeys.add(foreignKey);
        		          }
        		    }
        		}
        	}
        	tableDetails.put("columns",((JSONObject) table).get("columns"));
        	tableDetails.put("relationships",relationships);
        	tableDetails.put("primaryKey", primaryKey);
        	tableName.put(((JSONObject) table).get("name"),tableDetails);
        	erdtables.add(tableName);
        });
        
        foreignkeys.forEach(foreignIndex -> {
        	JSONObject relationship = new JSONObject();
        	table2 = "";
        	table1 ="";
        	refColumn ="";
        	for(Iterator iterator = ((JSONObject) foreignIndex).keySet().iterator(); iterator.hasNext();) {
        		table1 = (String) iterator.next();
        		JSONObject indexDetails = (JSONObject) ((JSONObject) foreignIndex).get(table1);
        		table2 = (String) ((JSONObject) indexDetails).get("refTable");
        		refColumn = (String) ((JSONObject) indexDetails).get("refColumn");
        	}
        	erdtables.forEach(tableObj -> {
        		for(Iterator iterator = ((JSONObject) tableObj).keySet().iterator(); iterator.hasNext();) {
	        		String	key = (String) iterator.next();
	        		JSONObject table = (JSONObject) ((JSONObject) tableObj).get(key);
	        		JSONArray relationships = new JSONArray();
	        		relationships=(JSONArray) ((JSONObject) table).get("relationships");

        			if(key.equalsIgnoreCase(table2)) {
        				JSONObject parentRelation = new JSONObject();
        				JSONObject parentRelationDetails = new JSONObject();
        				parentRelationDetails.put("type","childTable");
        				parentRelationDetails.put("refKey",refColumn);
        				parentRelation.put(table1,parentRelationDetails);
        				relationships.add(parentRelation);
        			}
        			if(key.equalsIgnoreCase(table1)) {
        				JSONObject childRelation = new JSONObject();
        				JSONObject childRelationDetails = new JSONObject();
        				childRelationDetails.put("type","parentTable");
        				childRelationDetails.put("refKey",refColumn);
        				childRelation.put(table2,childRelationDetails);
        				relationships.add(childRelation);
        			}
        			((JSONObject) table).put("relationships",relationships);
        			((JSONObject) tableObj).put(key,table);
        		}
        	});
        });
		Gson gson = new GsonBuilder().setPrettyPrinting().create();
		String prettyERDString = gson.toJson(erdtables);
		try (FileWriter file = new FileWriter(erdPath +database+"_ERD.json")) {
			file.write(prettyERDString);
			file.flush();
			System.out.println("ERD generated successfully!");
		} catch (IOException e) {
			e.printStackTrace();
			System.out.println("Failed to Generate ERD!");
		}
	}

}
