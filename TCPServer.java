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
            //            System.out.println(clientSocket);
        } 
        catch (IOException e) 
        { 
            System.err.println("Accept failed at " + (System.currentTimeMillis() - timestart) 
                    + " milliseconds on port " + port + "."); 
            System.exit(1); 
        } 



        InetAddress IPAddress = clientSocket.getInetAddress();
        int clientPort = clientSocket.getLocalPort();

        PrintWriter out = new PrintWriter(clientSocket.getOutputStream(), true); 
        BufferedReader in = new BufferedReader( 
                new InputStreamReader( clientSocket.getInputStream())); 

        String inputLine; 
        
//        while ((inputLine = in.readLine()) != null) 
        while (true) 
        { 
            inputLine = in.readLine().trim();

            //            inputLine = inputLine.trim();
            String returnMessage = "Invalid request received from " + IPAddress + ":" +
                    clientPort + " at " + (System.currentTimeMillis()-timestart) +
                    " milliseconds! Please try again!";

            if (inputLine.endsWith(")")) {
                inputLine = inputLine.substring(0, inputLine.length() - 1); 
                String[] inputSegs = inputLine.split("[(]");
                String operation = inputSegs[0];
                String key = inputSegs[1];
                if (operation.equalsIgnoreCase("get")) 
                {

                    if (store.containsKey(key)) 
                    {
                        returnMessage = "Value \"" + store.get(key) + "\" for Key \"" + key +
                                "\" returned to " + IPAddress + ":" + clientPort + " at " +
                                (System.currentTimeMillis()-timestart) + " milliseconds";
                    }
                    else
                    {
                        returnMessage = "No value found in store for Key \"" + key + "\" at " +
                                (System.currentTimeMillis()-timestart) + " milliseconds";
                    }
                } 
                else if (operation.equalsIgnoreCase("delete")) 
                {

                    if (store.containsKey(key))
                    {
                        returnMessage = "Key \"" + key + "\" Value \"" + store.get(key) +
                                "\" deleted at " + (System.currentTimeMillis()-timestart) + " milliseconds";
                        store.remove(key); // delete key/value from the Map
                    }
                    else
                    {
                        returnMessage = "No value found in store for Key \"" + key + "\" at " +
                                (System.currentTimeMillis()-timestart) + " milliseconds";
                    }
                }
                else if (operation.equalsIgnoreCase("put") && key.contains(",")) 
                {
                    String[] keySegs = key.split(",");
                    key = keySegs[0].trim();
                    String value = keySegs[1].trim();
                    store.put(key, value); // place key/value into the Map

                    returnMessage = "Key \"" + key + "\" Value \"" + store.get(key) + "\" stored at " + (System.currentTimeMillis()-timestart) + " milliseconds";
                }

                System.out.println ("Server: " + returnMessage); 
                out.println(returnMessage); 
            }
            else if (inputLine.equals("Exit")) 
            {
                break;
            } else 
            {
                System.out.println ("Server: " + returnMessage); 
                out.println(returnMessage); 
            }

        } 

        out.close(); 
        in.close(); 
        clientSocket.close(); 
        serverSocket.close(); 
    } 
} 
