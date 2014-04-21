import java.io.*;
import java.net.*;

public class TCPClient {
    public static void main(String[] args) throws IOException {

        String serverHostname = new String ("127.0.0.1");
        int port = 10007;
        long timestart = System.currentTimeMillis();

        if (args.length > 0) 
            serverHostname = args[0];
        if (args.length > 1) 
            port = Integer.parseInt(args[1]);
        System.out.println ("\nAttemping to connect to host " +
                serverHostname + " on port: " + port + " at " +
                (System.currentTimeMillis()-timestart) + " milliseconds");
        Socket echoSocket = null;
        PrintWriter out = null;
        BufferedReader in = null;

        try {
            echoSocket = new Socket(serverHostname, port);
            out = new PrintWriter(echoSocket.getOutputStream(), true);
            in = new BufferedReader(new InputStreamReader(
                    echoSocket.getInputStream()));
        } catch (UnknownHostException e) {
            System.err.println("Don't know about host: " + serverHostname);
            System.exit(1);
        } catch (IOException e) {
            System.err.println("Couldn't get I/O for "
                    + "the connection to: " + serverHostname);
            System.exit(1);
        }

        BufferedReader stdIn = new BufferedReader(
                new InputStreamReader(System.in));
        String userInput;

        System.out.println ("\nConnection successful! Key-Value Store is ready! Available operations:");
        System.out.println ("1: PUT(Key, Value)");        
        System.out.println ("2: GET(Key)");
        System.out.println ("3: DELETE(Key)");
        System.out.println ("4: Exit");
        
        System.out.print ("\nCommand: ");
        while ((userInput = stdIn.readLine()) != null) {
            out.println(userInput);
            if (userInput.toLowerCase().equals("exit")) {
                System.out.println ("Connection with server " + echoSocket.getInetAddress() +
                        ":" + echoSocket.getPort() + " terminates at " +
                        (System.currentTimeMillis()-timestart) + " milliseconds"); 
                System.exit(1);
            }
                
//            System.out.println("in.readLine = " + in.readLine());
            System.out.println("Server> " + in.readLine());
            
            System.out.print("\nCommand: ");
        }

        out.close();
        in.close();
        stdIn.close();
        echoSocket.close();
    }
}

