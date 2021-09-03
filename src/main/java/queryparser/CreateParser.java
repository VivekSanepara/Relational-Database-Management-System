package queryparser;

import querytranslation.QueryToken;

public class CreateParser {

    public QueryToken parse(String query) {
        query = query.replaceAll(";", "");
        query = query.replaceAll(",", " ");
        query = query.replaceAll("[^a-zA-Z ]", "");
        String[] sqlWords = query.split(" ");

        String action = sqlWords[0];
        String type = sqlWords[1];
        String name = sqlWords[2];

        // Value is set to hashmap and this hashmap will be used for operations.
        // executor uses this queryToken to do its task.

        QueryToken internalQuery = new QueryToken();
        internalQuery.set("action", action);
        internalQuery.set("type", type);
        internalQuery.set("name", name);

        return internalQuery;
    }
}
