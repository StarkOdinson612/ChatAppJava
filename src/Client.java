import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.Socket;
import java.net.UnknownHostException;
import java.util.Scanner;

public class Client {
    public static void main(String[] args) throws IOException, InterruptedException {
        Scanner s = new Scanner(System.in);
        Socket otherSocket = null;

        while (otherSocket == null)
        {
            try
            {
                otherSocket = Client.createSocket(s);
            }
            catch (Exception e)
            {
            }

        }

        System.out.printf("Connected to server!\n");
        System.out.print("Enter Username: ");
        String thisUsername = s.nextLine();
        PrintWriter pw = new PrintWriter(otherSocket.getOutputStream(), true);
        pw.println(thisUsername);

        User otheruser = new User(otherSocket, pw);

        System.out.printf("Connected to host (%s) at %s!\n", otheruser.getUsername(), otheruser.getIP());

        Thread clientListenThread = new Thread(new ClientListen(otheruser));
        clientListenThread.start();

        Thread clientWriteThread = new Thread(new ClientWrite(otheruser, s));
        clientWriteThread.start();
    }

    public static Socket createSocket(Scanner s) throws Exception {
        System.out.print("Enter Host IP: ");
        String hostIP = s.nextLine();

        System.out.print("Enter Host Port: ");
        String hostPort = s.nextLine();
        System.out.println();
        Socket otherSocket;

        try {
            otherSocket = new Socket("127.0.0.1", 50000);
        }
        catch (Exception e)
        {
            return null;
        }

        return otherSocket;
    }
}

class ClientListen implements Runnable
{
//    private User user;
    private String otherUsername;
    private User otherUser;

    public ClientListen(User u)
    {
        otherUser = u;
        otherUsername = otherUser.getUsername();

    }

    @Override
    public void run() {
        while (otherUser.socketOpen())
        {
            try {
                String message = otherUser.readMessage();
                System.out.printf("%s: %s\n", otherUsername, message);
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
        }
    }
}

class ClientWrite implements Runnable
{
    private User user;
    private Scanner scanner;

    public ClientWrite(User u, Scanner sc)
    {
        user = u;
        scanner = sc;
    }

    @Override
    public void run() {
        while (user.socketOpen())
        {
            try {
                String input = scanner.nextLine();
                if (input.equalsIgnoreCase("cancelcoconut"))
                {
                    user.closeSocket();
                    break;
                }

                user.writeMessage(input);
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
        }

        System.out.println(user.getUsername() + " disconnected...");
    }
}
