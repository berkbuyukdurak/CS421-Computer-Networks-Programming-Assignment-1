import org.omg.PortableInterceptor.SYSTEM_EXCEPTION;

import javax.xml.crypto.Data;
import java.io.*;
import java.net.ServerSocket;
import java.net.Socket;
import java.nio.charset.Charset;
import java.util.ArrayList;
import java.util.Stack;


public class SeekAndDestroy
{
    final static int controlPort = 60000;
    final static int dataPort = 53462;
    final static String host = "localhost";
    final static String CR = "\r";
    final static String LF = "\n";

    Stack<Integer> sizeStack = new Stack<>();
    Stack<String> directoryStack = new Stack<String>();
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

            InputStreamReader inputStreamReader =
                    new InputStreamReader(clientSocket.getInputStream());
            BufferedReader bufferedReader =
                    new BufferedReader(inputStreamReader);

            System.out.println(bufferedReader.readLine());
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
                 if (line == null || line.equals("0000000000000000") || line.equals(""))
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
        //Checking if the directory is empty, going up if it is
        String[] checkingIfEmpty = directories.get(0).split(":");
        if(checkingIfEmpty.length == 1){
            System.out.println("CD up because of no elements");
            cdup();
            while(!sizeStack.isEmpty()){
                int s = sizeStack.pop();
                if(s > 1){
                    sizeStack.push(s-1);
                    break;
                }
                else
                    cdup();

            }

            return 1;
        }
        //Checking if the target is in the current directory, returning found flag if
        for(int i = 0; i < directories.size(); i++){
            if(directories.get(i).split(":")[0].equals("target.jpg")){
                System.out.print("FOUND");
                return 5;
            }
        }
        //Setting the first elements name correctly, I don't know why but 2 char garbage value is set
        //if this process is not done
        directories.set(0, directories.get(0).substring(2));
        //Checking if only the f type files are left in the directory
        boolean fFlag = true;
        for(int i = 0; i < directories.size(); i++){
            String[] directoryContent = directories.get(i).split(":");

            if(directoryContent[1].equals("d")){
                fFlag = false;
            }
        }
        if(fFlag){
            System.out.println("Only f left, CD up");
            cdup();
            while(!sizeStack.isEmpty()){
                int s = sizeStack.pop();
                if(s > 1){
                    sizeStack.push(s-1);
                    break;
                }
                else
                    cdup();

            }
            return 1;
        }
        //
        int size = 0;
        for(int i = 0; i < directories.size(); i++){
            String[] directoryContent = directories.get(i).split(":");
            System.out.print(directoryContent[0]+"/");
            if(directoryContent[1].equals("d")){
                directoryStack.push(directoryContent[0]);
                size++;
            }
        }
        sizeStack.push(size);
        System.out.println();
        return 1;
    }
    public void loopSearch(){
        while(!directoryStack.isEmpty()){
            System.out.println("CD into: "+directoryStack.peek());
            System.out.print(sizeStack);
            cwd(directoryStack.pop());
            int i = findTarget();
            if (i == 5)
                break;
        }
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
            sad.loopSearch();
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