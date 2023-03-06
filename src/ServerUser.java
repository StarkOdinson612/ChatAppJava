import java.io.IOException;
import java.net.Socket;

public class ServerUser extends User {

    public ServerUser(Socket s) throws IOException, InterruptedException {
        super(s, true);
    }

    @Override
    public String getUsername() {
        return null;
    }
}
