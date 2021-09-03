
package distributed;

import com.jcraft.jsch.ChannelSftp;
import com.jcraft.jsch.JSch;
import com.jcraft.jsch.JSchException;
import com.jcraft.jsch.Session;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.FileInputStream;
import java.io.IOException;
import java.util.Properties;
import java.util.Scanner;

public class RemoteDBConnection {

    String user, host;
    int port;
    private static ChannelSftp sftp = null;
    static final Logger logger = LoggerFactory.getLogger(RemoteDBConnection.class);
    public RemoteDBConnection() {

    }
    public RemoteDBConnection(String user, String host, int port) {
        this.user = user;
        this.host = host;
        this.port = port;
    }


    private static void load() {

        try {
            String user, host;
            int port;
            logger.info("remote connection");
            Properties properties = new Properties();
            properties.load(new FileInputStream("db.properties"));
            host = properties.getProperty("host");
            port=Integer.parseInt(properties.getProperty("port"));
            user = properties.getProperty("remoteUsername");
        } catch (IOException e) {

        }
    }

    public static ChannelSftp createConnection(){
        try {
            load();
            JSch jsch=new JSch();
            Scanner scanner = new Scanner(System.in);
            Session session= (Session) jsch.getSession("user", "host");
            ChannelSftp sftp = (ChannelSftp) ((Session) session).openChannel("sftp");
            ((Session) session).connect();
            String command="scp -f ";
            DataOutputStream dataOutputStream=new DataOutputStream(sftp.getOutputStream());
            DataInputStream dataInputStream = new DataInputStream(sftp.getInputStream());

        }
         catch (JSchException e) {

        } catch (IOException e) {

        }
        return sftp;
    }
}

