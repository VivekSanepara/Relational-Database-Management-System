package distributed;

import com.jcraft.jsch.ChannelSftp;
import com.jcraft.jsch.SftpException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.InputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.util.ArrayList;
import java.util.List;

public class RemoteDBReader {

    private static final Logger logger = LoggerFactory.getLogger(RemoteDBReader.class);

    public RemoteDBReader() {
    }

    public static List<String> readFile(String fileName) {
        List<String> fileLines = new ArrayList<>();

        try {
            String filePath = "/home/vivek_sanepara2100/group3/csci-5408/DDBMS/" + fileName;

            ChannelSftp sftpChannel = RemoteDBConnection.createConnection();
            InputStream stream = sftpChannel.get(filePath);

            Path tempFile = Paths.get("DDBMS.tmp");
            Files.copy(stream, tempFile, StandardCopyOption.REPLACE_EXISTING);
            fileLines = Files.readAllLines(tempFile);

            Files.deleteIfExists(tempFile);
        } catch (SftpException exception) {

        } catch (Exception exception) {

        }
        return fileLines;
    }


    public static List<String> readData(String tableName) {
        String filePath = "/home/vivek_sanepara2100/group3/csci-5408/DDBMS/" + tableName;
        List<String> fileLines = new ArrayList<>();
        try {
            ChannelSftp sftpChannel = RemoteDBConnection.createConnection();
            InputStream stream = sftpChannel.get(filePath);

            Path tempFile = Paths.get("DDBMS.tmp");
            Files.copy(stream, tempFile, StandardCopyOption.REPLACE_EXISTING);
            fileLines = Files.readAllLines(tempFile);

            Files.deleteIfExists(tempFile);
        } catch (Exception exception) {

        }
        return fileLines;
    }

}
