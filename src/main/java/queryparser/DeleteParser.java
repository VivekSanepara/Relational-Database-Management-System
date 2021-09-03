package queryparser;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import querytranslation.QueryToken;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class DeleteParser {
    static final Logger logger = LoggerFactory.getLogger(DeleteParser.class);

    public QueryToken parse(String query) {
        // Implementation done using Regular Expression Parsing: https://www.baeldung.com/regular-expressions-java#:~:text=Java%20Regex%20Package&text=regex%20package%20consists%20of%20three,then%20return%20a%20Pattern%20object.
        Pattern pattern = Pattern.compile("delete\\s(.*?)from\\s(.*?)where\\s(.*?)?;");
        Matcher matcher = pattern.matcher(query);
        boolean match = matcher.matches();

        if (match == true) {
            logger.info("Parsing Query:" + query);
            String tableName = matcher.group(2);
            String condition = matcher.group(3);
            // Value is set to hashmap and this hashmap will be used for operations.
            // executor uses this queryToken to do its task.

            logger.info("Converting to query token.");
            QueryToken queryToken = new QueryToken();
            queryToken.setTableName(tableName);
            queryToken.setCondition(condition);
            return queryToken;
        } else {
            logger.info("Query Not Parsed");
            System.out.println("Invalid Query, Please Try Again");
            return null;
        }

    }
}
