import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;

public class SeekAndDestroy
{
    public static String CR = "\r";
    public static String LF = "\n";
    public static int controlPort = 6000;
    public static String host = "localhost";

    public static void main(String[] args)
    {
        try {
            Socket clientSocket = initConnection();
            System.out.println(clientSocket.getOutputStream());
        } catch (IOException ioe)
        {
            ioe.printStackTrace();
            System.out.println("Fail");
        }
    }
    /**
     * Init.s connection to the specified server
     * */
    private static Socket initConnection() throws IOException
    {
        Socket clientSocket = new Socket(host, controlPort);
        System.out.println("Here");
        return clientSocket;
    }

    private void sendUserName(String username)
    {

    }
    private void sendPass(String password)
    {

    }
    private void sendPort(int port)
    {

    }
    private void nlst()
    {

    }
    private void cwd(String child)
    {

    }
    private void cdup()
    {

    }
    private void sendRetry(String filename)
    {

    }
    private void sendDelete(String filename)
    {

    }
    private void quit()
    {

    }
}
