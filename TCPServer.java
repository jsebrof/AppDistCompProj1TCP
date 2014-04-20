import java.net.*; 
import java.util.HashMap;
import java.io.*; 

public class TCPServer 
{ 
    public static void main(String[] args) throws IOException 
    { 
        ServerSocket serverSocket = null; 
        long timestart = System.currentTimeMillis();
        HashMap<String, String> store = new HashMap<String, String>(); // Map for storing key/value pairs
        int port = 10007; // Default port to use
        if (args.length != 0)
            port = Integer.parseInt(args[0]); // If port is specified

        try { 
            serverSocket = new ServerSocket(port); 
        } 
        catch (IOException e) 
        { 
            System.err.println("Could not listen on port: " + port + "."); 
            System.exit(1); 
        } 

        Socket clientSocket = null; 
        System.out.println("Server Start at " + (System.currentTimeMillis() - timestart) 
                + " milliseconds on port " + port + ".\nWaiting for connection.....");

        try { 
            clientSocket = serverSocket.accept(); 
            System.out.println(clientSocket);
        } 
        catch (IOException e) 
        { 
            System.err.println("Accept failed."); 
            System.exit(1); 
        } 

        System.out.println ("Connection successful");
        
        // Setup bytes for sending and receiving packets
        byte[] receiveOperationData = new byte[1024];
        byte[] receiveKeyData = new byte[1024];
        byte[] receiveValueData = new byte[1024];
        byte[] sendData = new byte[1024];
        
        
        
        
        PrintWriter out = new PrintWriter(clientSocket.getOutputStream(), 
                true); 
        BufferedReader in = new BufferedReader( 
                new InputStreamReader( clientSocket.getInputStream())); 

        String inputLine; 

        while ((inputLine = in.readLine()) != null) 
        { 
            System.out.println ("Server: " + inputLine); 
            out.println(inputLine); 

            if (inputLine.equals("Bye.")) 
                break; 
        } 

        out.close(); 
        in.close(); 
        clientSocket.close(); 
        serverSocket.close(); 
    } 
} 
