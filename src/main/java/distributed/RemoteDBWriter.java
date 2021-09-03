package distributed;


import com.jcraft.jsch.ChannelSftp;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardOpenOption;
import java.util.List;
import java.util.StringJoiner;

public class RemoteDBWriter {

    private static final Logger logger = LoggerFactory.getLogger(RemoteDBWriter.class);

    public RemoteDBWriter() {
    }

    public static void writeFile(String fileName, String fileContent) {
        try {
            String filePath = "/home/vivek_sanepara2100/group3/csci-5408/DDBMS/" + fileName;
            String tempFilePath = "DDBMS/temp.tmp";
            Path tempFile = Paths.get(tempFilePath);
            Files.createFile(tempFile);
            Files.write(tempFile, fileContent.getBytes(), StandardOpenOption.TRUNCATE_EXISTING);

            ChannelSftp sftpChannel = RemoteDBConnection.createConnection();
            sftpChannel.put(tempFilePath, filePath);

            Files.deleteIfExists(tempFile);
        } catch (Exception exception) {

        }
    }


    public static void writeData(String tableName, List<String> columnData) {
        StringJoiner tableDataJoiner = new StringJoiner("\n");
        for (String data : columnData) {
            tableDataJoiner.add(data);
        }

        String tableData = tableDataJoiner.toString();

        String dataFileName = tableName + ".json";

        List<String> existingData = RemoteDBReader.readFile(dataFileName);
        existingData.add(tableData);

        writeFile(dataFileName, String.join("\n", existingData));
    }

    public static void deleteTable(String tableName) {
        try {
            String dataFile = "/home/vivek_sanepara2100/group3/csci-5408/DDBMS/" + tableName;

            ChannelSftp sftpChannel = RemoteDBConnection.createConnection();
            sftpChannel.rm(dataFile);

        } catch (Exception exception) {

        }
    }
}
