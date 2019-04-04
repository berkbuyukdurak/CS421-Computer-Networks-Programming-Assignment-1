import java.io.*;
import java.net.ServerSocket;
import java.net.Socket;
import java.nio.ByteBuffer;
import java.util.ArrayList;
import java.util.Stack;


public class SeekAndDestroy
{
    final static int dataPort = 53462;
    final static String CR = "\r";
    final static String LF = "\n";
    final static int POSITIVE_RESULT = 1;
    final static int NEGATIVE_RESULT = 1;

    Stack<Integer> sizeStack = new Stack<>();
    Stack<String> directoryStack = new Stack<String>();
    String command;
    Socket clientSocket;
    ServerSocket serverSocket;
    OutputStreamWriter outToServer;
    TreeNode treeRoot = new TreeNode("treeRoot", "d",null);
    TreeNode currentTreeNode = treeRoot;

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
    public int sendStringToPort(String str)
    {
        try {
            outToServer = new OutputStreamWriter(clientSocket.getOutputStream(), "US-ASCII");
            outToServer.write(str, 0, str.length());
            outToServer.flush();

            InputStreamReader inputStreamReader =
                    new InputStreamReader(clientSocket.getInputStream());
            BufferedReader bufferedReader =
                    new BufferedReader(inputStreamReader);

            String tmp = bufferedReader.readLine();
            System.out.println(tmp.split(" ")[0]);
            int responseCode = Integer.parseInt(tmp.split(" ")[0]);
            if (responseCode == 400)
                System.out.println("CODE 400 Received");
            return responseCode;
        } catch (IOException e)
        {
            e.printStackTrace();
            return NEGATIVE_RESULT;
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
             s.close();
             return result;
         }
        catch (IOException e)
        {
            e.printStackTrace();
            return null;
        }
    }
    public void writeImage(){
        try {
            Socket s = serverSocket.accept();
            InputStream is = s.getInputStream();
            InputStreamReader isr = new InputStreamReader(is);
            BufferedReader br = new BufferedReader(isr);
            byte[] size = new byte[2];
            is.read(size);

            ByteBuffer bb = ByteBuffer.wrap(size);
            short sho = bb.getShort();

            byte[] image = new byte[sho];
            is.read(image);
            File imageFile = new File("received.jpg");
            OutputStream os = new FileOutputStream(imageFile);
            os.write(image);
            os.close();

            s.close();

        }
        catch (IOException e)
        {
            e.printStackTrace();
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
        command = "RETR" + " " + filename + CR + LF;
        sendStringToPort(command);
        writeImage();
    }
    public void delete(String filename)
    {
        command = "DELE" + " " + filename + CR + LF;
        sendStringToPort(command);
    }
    public void quit()
    {
        command = "QUIT" + CR + LF;
        sendStringToPort(command);
    }

    public int findTarget(){
        Stack<TreeNode> dfsStack = new Stack<>();
        dfsStack.push(treeRoot);
        ArrayList<String> directories;// = nlst();
        String[] checkingIfEmpty;

        /**
         * Depth first search on a tree
         * Look at the top of the stack
         * if not root(first entrance) and node is not processed
         * then go in the directory
         *
         * If directory contains nothing go up
         * if it contains files but no directories
         * goes down, fails to add anything to add to stack
         * in the next loop it should go up since it is processed
         *
         * If it passes these checks then parses response and adds them as
         * childs. Pushes them to the stack for further search if they are
         * type directory
        */
        while (dfsStack.isEmpty() == false)
        {
            currentTreeNode = dfsStack.peek();
            if (currentTreeNode != treeRoot && currentTreeNode.processed == false)
                cwd(currentTreeNode.data);

            directories = nlst();
            checkingIfEmpty = directories.get(0).split(":");

            if (checkingIfEmpty.length == 1 )
            {
                dfsStack.pop();
                cdup();
                currentTreeNode.processed = true;
                continue;
            }
            if (currentTreeNode.processed)
            {
                dfsStack.pop();
                cdup();
                continue;
            }

            directories.set(0, directories.get(0).substring(2));
            currentTreeNode.processed = true;
            for (String s : directories) {
                TreeNode newChild = new TreeNode(s.split(":")[0], s.split(":")[1], null);
                currentTreeNode.addChild(newChild);
                if (newChild.dataType.equals("d")) {
                    dfsStack.push(newChild);
                }
                if (s.split(":")[0].equals("target.jpg")) {
                    // Found
                    retrieve("target.jpg");
                    return POSITIVE_RESULT;
                }
            }
        }
        return NEGATIVE_RESULT;
    }
    //////////////////////////////////////////////////
    ///////////////             //////////////////////
    /////////////////////////////////////////////////
    public static void main(String[] args)
    {
        int controlPort = Integer.parseInt(args[1]);
        String host = args[0];
        SeekAndDestroy sad = new SeekAndDestroy(controlPort, dataPort, host);
        try
        {
            sad.sendUserName("bilkent");
            sad.sendPass("cs421");
            sad.serverSocket = new ServerSocket(dataPort);
            sad.sendPort(dataPort);
            int result = sad.findTarget();
            if (result == POSITIVE_RESULT)
                System.out.println("FOUND");
            else
                System.out.println("NOT FOUND");
            sad.delete("target.jpg");
            sad.quit();

        }
        catch (Exception ioe)
        {
            ioe.printStackTrace();
            System.out.println("Connection Failure");
        }
    }
    class TreeNode
    {
        String data;
        String dataType;
        boolean processed;
        ArrayList<TreeNode> children;
        TreeNode parent;
        TreeNode(String data, String dataType, TreeNode parent)
        {
            this.data = data;
            this.parent = parent;
            this.dataType = dataType;
            processed = false;
            children = new ArrayList<TreeNode>();
        }
        public void addChild(String name, String dataType)
        {
            children.add(new TreeNode(name, dataType, this));
        }
        public void addChild(TreeNode treeNode)
        {
            treeNode.parent = this;
            this.children.add(treeNode);
        }
    }
}