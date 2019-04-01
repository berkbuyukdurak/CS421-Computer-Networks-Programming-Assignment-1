import java.io.IOException;
import java.io.OutputStreamWriter;
import java.net.Socket;

public class SeekAndDestroy
{
    public static String CR = "\r";
    public static String LF = "\n";
    public static int controlPort = 6000;
    public static String host = "localhost";
    String command;
    Socket clientSocket;

    public static void main(String[] args)
    {
        SeekAndDestroy sad = new SeekAndDestroy();
        try
        {
            //System.out.println(clientSocket.getOutputStream());
            //sendStringToPort("USER bilkent\\r\\n", clientSocket);
            sad.sendUserName("bilkent");
            sad.sendPass("cs421");
            sad.sendPort(53462);
            sad.nlst();

        }
        catch (Exception ioe)
        {
            ioe.printStackTrace();
            System.out.println("Connection Failure");
        }
    }
    public SeekAndDestroy()
    {
        try {
            clientSocket = initConnection();
        }catch (IOException e)
        {
            e.printStackTrace();
        }
    }
    /**
     * Init.s connection to the specified server
     * */
    private static Socket initConnection() throws IOException
    {
        return new Socket(host, controlPort);
    }

    private void sendUserName(String username)
    {
        command = "USER" + " " + username + CR + LF;
        sendStringToPort(command);
    }
    private void sendPass(String password)
    {
        command = "PASS" + " " + password + CR + LF;
        sendStringToPort(command);
    }
    private void sendPort(int port)
    {
        command = "PORT" + " " + port + CR + LF;
        sendStringToPort(command);
    }
    private void nlst()
    {
        command = "NLST" + CR + LF;
        sendStringToPort(command);
    }
    private void cwd(String child)
    {
        command = "CWD" + child + CR + LF;
        sendStringToPort(command);
    }
    private void cdup()
    {
        command = "CDUP" + CR + LF;
        sendStringToPort(command);
    }
    private void retry(String filename)
    {
        command = "RETR" + filename + CR + LF;
        sendStringToPort(command);
    }
    private void delete(String filename)
    {
        command = "DELE" + filename + CR + LF;
        sendStringToPort(command);
    }
    private void quit(Socket clientSocket)
    {
        command = "QUIT" + CR + LF;
        sendStringToPort(command);
    }
    private void sendStringToPort(String str)
    {
        try {


            OutputStreamWriter outputStreamWriter =
                    new OutputStreamWriter(clientSocket.getOutputStream(), "UTF-8");
            outputStreamWriter.write(str, 0, str.length());
            outputStreamWriter.flush();
            outputStreamWriter.close();
        } catch (IOException e)
        {
            e.printStackTrace();
        }
    }
}
