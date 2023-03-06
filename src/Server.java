import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.InetAddress;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.*;
import java.io.PrintWriter;

public class Server {
    private static Set<User> userList;

    public static void main(String[] args) throws IOException, InterruptedException {
        ServerSocket sSocket = new ServerSocket(50000);
        userList = new HashSet<>();
        System.out.printf("Hosting chat server at %s:%d.\n", InetAddress.getLocalHost().getHostAddress(),50000);
        Scanner sc = new Scanner(System.in);
        Socket connectedSocket = sSocket.accept();

        User connectedUser = new User(connectedSocket);

        System.out.printf("Connected to %s at %s!\n", connectedUser.getUsername(), connectedUser.getIP());

        System.out.print("Enter Username: ");
        String thisUsername = sc.nextLine();
        connectedUser.writeMessage(thisUsername);
    }

    public static void addUserToList(User u)
    {
        userList.add(u);
    }

    public static Set<User> getUserList()
    {
        return userList;
    }
}

class ServerListen implements Runnable
{
    private ServerSocket sSocket;

    public ServerListen(ServerSocket sS)
    {
        sSocket = sS;
    }

    @Override
    public void run() {
        while (!sSocket.isClosed())
        {
            try {
                Socket connectingSocket = sSocket.accept();
                User connectingUser = new User(connectingSocket);

                System.out.printf("%s connected from %s\n");

                Server.addUserToList(connectingUser);

                Thread thisUserMesageListen = new Thread(new ServerClientRead(connectingUser));
            } catch (IOException | InterruptedException e) {
                e.printStackTrace();
            }
        }
    }
}

class ServerClientRead implements Runnable
{
    private User user;
    private String otherName;

    public ServerClientRead(User u)
    {
        user = u;
        otherName = user.getUsername();
    }

    @Override
    public void run() {
        while (user.socketOpen())
        {
            try {
                String message = user.readMessage();
                System.out.printf("%s: %s\n", otherName, message);
                Thread messageSendThread = new Thread(new ServerWrite(message));

            } catch (IOException e) {
                throw new RuntimeException(e);
            }
        }
    }
}

class ServerWrite implements Runnable
{

    private String message;
    private Set<User> userList;

    public ServerWrite(String message)
    {
        this.message = message;
        this.userList = Server.getUserList();
    }

    @Override
    public void run() {
        for (User u : userList)
        {
            if (u.socketOpen())
            {
                try {
                    u.writeMessage(message);
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
    }
}