import java.io.*;
import java.net.Socket;

public class User {
    private Socket socket;
    private String username;
    private ObjectOutputStream pw;
    private BufferedReader br;

    public User(Socket s) throws IOException, InterruptedException {
        socket = s;
        this.br = new BufferedReader(new InputStreamReader(socket.getInputStream()));
        pw = new ObjectOutputStream(socket.getOutputStream());
//        username = n;

        System.out.println("Awaiting connection...");

        String user = this.readMessage();

        username = user;
    }

    public User(Socket s, ObjectOutputStream p) throws IOException, InterruptedException {
        socket = s;
        br = new BufferedReader(new InputStreamReader(socket.getInputStream()));
        pw = p;

        System.out.println("Awaiting connection...");

        String user = this.readMessage();

        username = user;
    }

    protected User(Socket s, boolean protectedConstructor) throws IOException {
        socket = s;
        this.br = new BufferedReader(new InputStreamReader(socket.getInputStream()));
        pw = new ObjectOutputStream(socket.getOutputStream());

        this.username = "SERVER";
    }

    public String getUsername()  { return username; }
    public String getIP() { return socket.getLocalAddress().toString().substring(1) + ":" + socket.getLocalPort(); }

    public int writeMessage(String message) throws IOException {
        if (socket.isClosed()) { return -1; }
        pw.writeChars(message + "\n");
        return 0;
    }

    public String readMessage() throws IOException {
        return br.readLine();
    }

    public boolean socketOpen() { return !socket.isClosed(); }

    public void closeSocket() throws IOException { socket.close(); pw.close(); br.close(); }
}
