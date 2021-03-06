import java.io.BufferedReader;
import java.io.DataOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.*;
import java.util.HashMap;
import java.util.InputMismatchException;
import java.util.Scanner;

class TCPServer
{
	static long timestart;
	static ServerSocket serverSocket = null;
	static Socket connectionSocket = null;
	static final int HOPS = 3;
	static final int timeout_ack = 500;
	static final int timeout_answer = 1000;
	public static void main(String args[]) throws Exception
	{
		int i;
		String data;
		timestart = System.currentTimeMillis();
		if (args.length < 1) // If there isn't at least one command line argument
		{
			System.out.println("Insufficient command line arguments at " + (System.currentTimeMillis()-timestart) + " milliseconds");
			System.exit(0);
		}
		HashMap<String, String> store = new HashMap<String, String>(); // Map for storing key/value pairs
		int port = Integer.parseInt(args[0]); // Port # to listen for messages at
		System.out.println("Server Start at " + (System.currentTimeMillis()-timestart) + " milliseconds");

		try {
			serverSocket = new ServerSocket(port);
		} catch (IOException e) {
			System.err.println("Could not listen on port: " + port + ".");
			serverSocket.close();
			System.exit(1);
		}
        
		while(true)
		{
			while(true)
			{
				System.out.println("\nWaiting to receive connection request at " + (System.currentTimeMillis()-timestart) + " milliseconds.");
				try {
					connectionSocket = serverSocket.accept();
				} catch (IOException e) {
					System.err.println("Accept failed at " + (System.currentTimeMillis()-timestart) + " milliseconds");
					break;
				}
	            BufferedReader inFromClient = new BufferedReader(new InputStreamReader(connectionSocket.getInputStream()));
	            DataOutputStream outToClient = new DataOutputStream(connectionSocket.getOutputStream());
	            
				// Receive operation and key packets
				serverSocket.setSoTimeout(0); // Don't timeout on waiting for the first packet
				data = inFromClient.readLine();
				Scanner dataScanner = new Scanner(data);
				// get operation from operation packet
				String operation = dataScanner.next();
				operation = operation.trim().toLowerCase();
				if(!(operation.matches("put") || operation.matches("get") || operation.matches("delete"))){
					System.out.println("Incorrect command line arguments at " + (System.currentTimeMillis()-timestart) + " milliseconds");
					break;
				}
				// get key from key packet
				String key = dataScanner.next();
				key = key.trim();
				
				String value = "";
				// get value from value packet
				if (operation.matches("put")) // put operation requires a third "value" packet
				{
					if(!dataScanner.hasNext()){
						System.out.println("Insufficient arguments at" + (System.currentTimeMillis()-timestart) + " milliseconds");
						break;
					}
					value = dataScanner.next();
					value = value.trim();
					
				}
				
				// if no value packet was received, value string will be empty and not obstruct the code to follow
				
				// after verify the packet received, sends ack to the client
				sendAck(outToClient); //sends ACK to the client
				
				// get IP address and port of client
				InetAddress IPAddress = connectionSocket.getInetAddress();
				int clientPort = connectionSocket.getPort();
				
				System.out.println("Request from: " + IPAddress + ":" + clientPort + " for " + operation + " " + key + " " + value + " at " + (System.currentTimeMillis()-timestart) + " milliseconds");
				switch (operation) // depending on the requested operation
				{
				case "put":
					store.put(key, value); // place key/value into the Map
					System.out.println("Key \"" + key + "\" Value \"" + store.get(key) + "\" stored at " + (System.currentTimeMillis()-timestart) + " milliseconds");
					break;
				case "get":
					String returnMessage;
					if (store.containsKey(key))
					{
						returnMessage = store.get(key);
						System.out.println("Value \"" + store.get(key) + "\" for Key \"" + key + "\" returned to " + IPAddress + ":" + clientPort + " at " + (System.currentTimeMillis()-timestart) + " milliseconds");
					}
					else
					{
						returnMessage = "Requested value not found in store for given key";
						System.out.println("No value found in store for Key \"" + key + "\" at " + (System.currentTimeMillis()-timestart) + " milliseconds");
					}
					outToClient.writeBytes(returnMessage + "\n"); // return requested value to the client
					//wait for an ACK
					i = 1;
					while(!waitAck(inFromClient)){
						if(i == HOPS){
							System.out.println("Failed! Timeout on receiving acknoledgment!....");
							break;
						}
						outToClient.writeBytes(returnMessage + "\n");
						i++;
					}
					break;
				case "delete":
					if (store.containsKey(key))
					{
						System.out.println("Key \"" + key + "\" Value \"" + store.get(key) + "\" deleted at " + (System.currentTimeMillis()-timestart) + " milliseconds");
						store.remove(key); // delete key/value from the Map
					}
					else
					{
						System.out.println("No value found in store for Key \"" + key + "\" at " + (System.currentTimeMillis()-timestart) + " milliseconds");
					}
					break;
				default:
					System.out.println("Unknown command from client: \"" + operation + " " + key + " " + value + "\" at " + (System.currentTimeMillis()-timestart) + " milliseconds");
					break;
				}
			}
			connectionSocket.close();
			System.out.println("Client disconnected at " + (System.currentTimeMillis()-timestart) + " milliseconds");
		}
	}

	private static boolean waitAck(BufferedReader in) throws IOException {
		String ack = "";
		boolean ret = false;
		connectionSocket.setSoTimeout(timeout_ack);
		try {
			ack = in.readLine(); // receive value packet from server
			ack = ack.trim();
			if (ack.matches("ack")) {
				System.out.println("Acknowledgement received at "
						+ (System.currentTimeMillis() - timestart)
						+ " milliseconds.");
				ret = true;
			}
		} catch (SocketTimeoutException e) {
			System.out.println("Timeout waiting for acknowledgement at "
					+ (System.currentTimeMillis() - timestart)
					+ " milliseconds.");
		}
		return ret;
	}
	
	private static void sendAck(DataOutputStream out) throws IOException{
		out.writeBytes("ack" + "\n");
		System.out.println("Acknowledgement sent at "
				+ (System.currentTimeMillis() - timestart)
				+ " milliseconds.");
	}
}
