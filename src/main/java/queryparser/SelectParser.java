package queryparser;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import querytranslation.QueryToken;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class SelectParser {
    static final Logger logger = LoggerFactory.getLogger(SelectParser.class);

    public QueryToken parse(String query) {
     // Implementation done using Regex For SELECT query: https://stackoverflow.com/questions/1506699/regular-expression-to-match-select-from-where-sql-querie
        // Implementation done using Regular Expression Parsing: https://www.baeldung.com/regular-expressions-java#:~:text=Java%20Regex%20Package&text=regex%20package%20consists%20of%20three,then%20return%20a%20Pattern%20object.

        Pattern pattern = Pattern.compile("select\\s+(.*?)\\s*from\\s+(.*?)\\s*(where\\s(.*?)\\s*)?;", Pattern.DOTALL);
        Matcher queryMatch = pattern.matcher(query);
        queryMatch.find();
        boolean match = queryMatch.matches();
        if (match == true) {
            logger.info("Parsing Query:" + query);
            String[] columns = queryMatch.group(1).split(",");
            String table = queryMatch.group(2);
            String conditions = queryMatch.group(4);

            logger.info("Query Translation Stage");
            QueryToken queryToken = new QueryToken();
            queryToken.set("columns", columns);
            queryToken.set("table", table);
            queryToken.set("conditions", conditions);
            // Value is set to hashmap and this hashmap will be used for operations.
            // executor uses this queryToken to do its task.

            return queryToken;
        } else {
            logger.info("Query Not Parsed");
            System.out.println("Invalid Query, Please Try Again");
            return null;
        }

    }
}
