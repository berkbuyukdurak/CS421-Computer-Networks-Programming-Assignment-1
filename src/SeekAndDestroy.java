import javax.xml.crypto.Data;
import java.io.*;
import java.net.ServerSocket;
import java.net.Socket;
import java.nio.charset.Charset;
import java.util.ArrayList;
import java.util.PriorityQueue;
import java.util.Queue;


public class SeekAndDestroy
{
    final static int controlPort = 60000;
    final static int dataPort = 53462;
    final static String host = "localhost";
    final static String CR = "\r";
    final static String LF = "\n";

    Queue<String> directoryQueue = new PriorityQueue<String>();
    String command;
    Socket clientSocket;
    ServerSocket serverSocket;
    OutputStreamWriter outToServer;

    // Default Constructor
    public SeekAndDestroy(int controlPort, int dataPort, String host)
    {

        try {
            clientSocket = new Socket(host, controlPort);
        }catch (IOException e)
        {
            e.printStackTrace();
        }
    }
    // TODO: IF command requests data, then should listen to that one after calling this
    public void sendStringToPort(String str)
    {
        try {
            outToServer = new OutputStreamWriter(clientSocket.getOutputStream(), "US-ASCII");
            outToServer.write(str, 0, str.length());
            outToServer.flush();
        } catch (IOException e)
        {
            e.printStackTrace();
        }
    }
    public ArrayList<String> readPort(){
         try {
             ArrayList<String> result = new ArrayList<>();
             Socket s = serverSocket.accept();
             InputStream is = s.getInputStream();
             InputStreamReader isr = new InputStreamReader(is, "US-ASCII");
             BufferedReader buffer = new BufferedReader(isr);
             while(true) {
                 String line = buffer.readLine();
                 if (line == null || line.equals(""))
                         break;
                 result.add(line);
             }
             return result;
         }
        catch (IOException e)
        {
            e.printStackTrace();
            return null;
        }
    }
    //////////////////////////////////////////////////
    ///////////////             //////////////////////
    //////////////////////////////////////////////////
    public void sendUserName(String username)
    {
        command = "USER" + " " + username + CR + LF;
        sendStringToPort(command);
    }
    public void sendPass(String password)
    {
        command = "PASS" + " " + password + CR + LF;
        sendStringToPort(command);
    }
    public void sendPort(int port)
    {
        command = "PORT" + " " + port + CR + LF;
        sendStringToPort(command);
    }
    public ArrayList<String> nlst()
    {
        ArrayList<String> toReturn = new ArrayList<>();
        command = "NLST" + CR + LF;
        sendStringToPort(command);
        ArrayList<String> output = readPort();
        return output;

    }
    public void cwd(String child)
    {
        command = "CWD" + " " + child + CR + LF;
        sendStringToPort(command);
    }
    public void cdup()
    {
        command = "CDUP" + " " + CR + LF;
        sendStringToPort(command);
    }
    public void retrieve(String filename)
    {
        command = "RETR" + filename + CR + LF;
        sendStringToPort(command);
    }
    public void delete(String filename)
    {
        command = "DELE" + filename + CR + LF;
        sendStringToPort(command);
    }
    public void quit()
    {
        command = "QUIT" + CR + LF;
        sendStringToPort(command);
    }

    public int findTarget(){
        ArrayList<String> directories = nlst();
        System.out.println(directories.size());
        if(directories.size() > 0){
            directories.set(0, directories.get(0).substring(2));
        }
        for(int i = 0; i < directories.size(); i++){

            String[] directoryContent = directories.get(i).split(":");
            System.out.println(directoryContent[0] +"~"+ directoryContent[1]);

        }
        return 1;
    }

    //////////////////////////////////////////////////
    ///////////////             //////////////////////
    /////////////////////////////////////////////////

    public static void main(String[] args)
    {
        SeekAndDestroy sad = new SeekAndDestroy(controlPort, dataPort, host);
        try
        {
            sad.sendUserName("bilkent");
            sad.sendPass("cs421");
            sad.serverSocket = new ServerSocket(dataPort);
            sad.sendPort(dataPort);
            //sad.nlst();
            sad.findTarget();
            //sad.cdup();
            sad.quit();

        }
        catch (Exception ioe)
        {
            ioe.printStackTrace();
            System.out.println("Connection Failure");
        }
    }
}