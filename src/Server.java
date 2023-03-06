import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.InetAddress;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.Scanner;
import java.io.PrintWriter;

public class Server {
    public static void main(String[] args) throws IOException, InterruptedException {
        ServerSocket sSocket = new ServerSocket(50000);
        System.out.printf("Hosting chat server at %s:%d.\n", InetAddress.getLocalHost().getHostAddress(),50000);
        Scanner sc = new Scanner(System.in);
        Socket connectedSocket = sSocket.accept();

        User connectedUser = new User(connectedSocket);

        System.out.printf("Connected to %s at %s!\n", connectedUser.getUsername(), connectedUser.getIP());

        System.out.print("Enter Username: ");
        String thisUsername = sc.nextLine();
        connectedUser.writeMessage(thisUsername);

        Thread serverListenThread = new Thread(new ServerListen(connectedUser));
        serverListenThread.start();

        Thread serverWriteThread = new Thread(new ServerWrite(connectedUser, sc));
        serverWriteThread.start();
    }
}

class ServerListen implements Runnable
{
    private User user;
    private String otherName;

    public ServerListen(User u)
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
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
        }
    }
}

class ServerWrite implements Runnable
{
    private User user;
    private Scanner scanner;


    public ServerWrite(User user, Scanner sc)
    {
        this.user = user;

        scanner = sc;
    }

    @Override
    public void run() {
        while (user.socketOpen())
        {
            try {
                String input = scanner.nextLine();
                if (input.equalsIgnoreCase("cancelcoconut")) { user.closeSocket(); }

                user.writeMessage(input);
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
        }
        System.out.println(user.getUsername() + " disconnected...");
    }
}