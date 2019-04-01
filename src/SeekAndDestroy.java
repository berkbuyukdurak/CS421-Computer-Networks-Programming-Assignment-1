import java.io.IOException;
import java.io.OutputStreamWriter;
import java.net.Socket;

public class SeekAndDestroy
{
    public static String CR = "\r";
    public static String LF = "\n";
    public static int controlPort = 6000;
    public static String host = "localhost";

    public static void main(String[] args)
    {
        Socket clientSocket;
        try
        {
            clientSocket = initConnection();
            //System.out.println(clientSocket.getOutputStream());
            sendStringToPort("USER bilkent\\r\\n", clientSocket);
        }
        catch (IOException ioe)
        {
            ioe.printStackTrace();
            System.out.println("Connection Failure");
        }
    }
    /**
     * Init.s connection to the specified server
     * */
    private static Socket initConnection() throws IOException
    {
        Socket clientSocket = new Socket(host, controlPort);
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
    private static void sendStringToPort(String str, Socket clientSocket) throws IOException
    {
        OutputStreamWriter outputStreamWriter =
                new OutputStreamWriter(clientSocket.getOutputStream(), "UTF-8");
        outputStreamWriter.write(str, 0, str.length());
    }
}
