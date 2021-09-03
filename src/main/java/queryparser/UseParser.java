package queryparser;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import querytranslation.QueryToken;

public class UseParser {
    final static Logger logger = LoggerFactory.getLogger(UseParser.class);

    public QueryToken parse(String query) {
        query = query.replaceAll(";", "");
        String[] sqlWords = query.split(" ");

        logger.info("Parsing Query:"+query);
        String database = sqlWords[1];

        logger.info("Query Translation stage");
        QueryToken queryToken = new QueryToken();
        // Value is set to hashmap and this hashmap will be used for operations.
        // executor uses this queryToken to do its task.

        queryToken.set("database",database);

        return queryToken;
    }
}
