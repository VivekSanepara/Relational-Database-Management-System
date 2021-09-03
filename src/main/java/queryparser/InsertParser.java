package queryparser;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import querytranslation.QueryToken;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class InsertParser {
    static final Logger logger = LoggerFactory.getLogger(InsertParser.class);

    public QueryToken parse(String query) {
        // Implementation done using Regular Expression Parsing: https://www.baeldung.com/regular-expressions-java#:~:text=Java%20Regex%20Package&text=regex%20package%20consists%20of%20three,then%20return%20a%20Pattern%20object.
        // Implementation done using Regex for INSERT QUERY: https://www.regexlib.com/UserPatterns.aspx?authorId=a6de5349-754f-4803-8596-47fc1541c1cc&AspxAutoDetectCookieSupport=1
        Pattern pattern = Pattern.compile("insert into\\s(.*?)\\s(.*?)\\svalues\\s(.*?);", Pattern.DOTALL);
        Matcher matcher = pattern.matcher(query);
        matcher.find();
        boolean match = matcher.matches();

        if (match == true) {
            logger.info("Parsing Query:" + query);
            String table = matcher.group(1);

            String[] values = matcher.group(3)
                    .replaceAll("[\\[\\](){}]", "")
                    .replace(" ", "")
                    .replace("'", "")
                    .replace("\"", "")
                    .split(",");

            String[] columns = matcher.group(2)
                    .replaceAll("[\\[\\](){}]", "")
                    .replace(" ", "")
                    .replace("'", "")
                    .replace("\"", "")
                    .split(",");
            // Value is set to hashmap and this hashmap will be used for operations.
            // executor uses this queryToken to do its task.

            logger.info("Query Translation Stage");
            QueryToken queryToken = new QueryToken();
            queryToken.set("table", table);
            queryToken.set("columns", columns);
            queryToken.set("values", values);
            return queryToken;
        } else {
            logger.info("Query Not Parsed");
            System.out.println("Invalid Query, Please Try Again");
            return null;
        }
    }
}
