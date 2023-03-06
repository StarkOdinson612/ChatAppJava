import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.Socket;

public class User {
    private Socket socket;
    private String username;
    private PrintWriter pw;
    private BufferedReader br;

    public User(Socket s) throws IOException, InterruptedException {
        socket = s;
        this.br = new BufferedReader(new InputStreamReader(socket.getInputStream()));
        pw = new PrintWriter(new PrintWriter(socket.getOutputStream()), true);
//        username = n;

        System.out.println("Awaiting connection...");

        String user = this.readMessage();

        username = user;
    }

    public User(Socket s, PrintWriter p) throws IOException, InterruptedException {
        socket = s;
        br = new BufferedReader(new InputStreamReader(socket.getInputStream()));
        pw = p;

        System.out.println("Awaiting connection...");

        String user = this.readMessage();

        username = user;
    }

    public String getUsername()  { return username; }
    public String getIP() { return socket.getLocalAddress().toString().substring(1) + ":" + socket.getLocalPort(); }

    public int writeMessage(String message)
    {
        if (socket.isClosed()) { return -1; }
        pw.println(message);
        return 0;
    }

    public String readMessage() throws IOException {
        return br.readLine();
    }

    public boolean socketOpen() { return !socket.isClosed(); }

    public void closeSocket() throws IOException { socket.close(); pw.close(); br.close(); }
}
