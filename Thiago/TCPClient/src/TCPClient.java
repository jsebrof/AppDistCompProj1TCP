import java.io.BufferedReader;
import java.io.DataOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.*;

class TCPClient
{
	static long timestart;
	static Socket clientSocket;
	static int HOPS = 3;
	static int timeout_ack = 500;
	static int timeout_answer = 1000;
	public static void main(String args[]) throws Exception
	{
		int i;
		timestart = System.currentTimeMillis();
		while (true)
		{
			System.out.println("Client Start at " + (System.currentTimeMillis()-timestart) + " milliseconds");
			
			String serverIP = "";
			int port = 0;
			String operation = "";
			String key = "";

			if (args.length >= 4) // If there are the minimum # of command line arguments
			{
				serverIP = args[0];
				port = Integer.parseInt(args[1]);
				operation = args[2];
				key = args[3];
			}
			else
			{
				System.out.println("Insufficient command line arguments at " + (System.currentTimeMillis()-timestart) + " milliseconds");
				break;
			}

			String value = "";
			if (args.length > 4) // A fifth command line argument in case of a put operation
			{
				value = args[4];
			}
			
			InetAddress IPAddress = InetAddress.getByName(serverIP); //gets the IP from DNS
			
			try {
				clientSocket = new Socket(IPAddress, port);
			} catch (IOException e) {
				System.err.println("Could not connect to " + IPAddress + ":" + port + ".");
				clientSocket.close();
				System.exit(1);
			}
			
			DataOutputStream outToServer = new DataOutputStream(clientSocket.getOutputStream());
			BufferedReader inFromServer = new BufferedReader(new InputStreamReader(clientSocket.getInputStream()));
			
			String data = operation + " " + key + " " + value;
			outToServer.writeBytes(data + "\n");
			
		
			if (operation.toLowerCase().matches("get"))
			{
				clientSocket.setSoTimeout(timeout_answer); // Set a 1 second timeout for the Value packet to arrive
				try
				{
					data = inFromServer.readLine(); // receive value packet from server
				}
				catch (SocketTimeoutException e)
				{
					System.out.println("Timeout on server at " + (System.currentTimeMillis()-timestart) + " milliseconds");
					break; // kill the sequence, as there is no returned packet to work with
				}
				System.out.println("Received from server: \"" + data.trim() + "\" at " + (System.currentTimeMillis()-timestart) + " milliseconds");
				sendAck(outToServer); //sends acknowledgment to server
			}else{
				//wait for an ACK
				i = 1;
				while(!waitAck(inFromServer)){
					if(i == HOPS){
						System.out.println("Failed! Timeout on receiving acknoledgment! Closing....");
						break;
					}
					outToServer.writeBytes(data + "\n");
					i++;
				}
			}
			break; // end the client sequence
		}
		clientSocket.close();
		System.out.println("Client Close at " + (System.currentTimeMillis()-timestart) + " milliseconds");
	}
	
	public static boolean waitAck(BufferedReader in) throws IOException{
		String ack = "";
		boolean ret = false;
		clientSocket.setSoTimeout(timeout_ack);
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
			System.out.println("Timeout on acknowledgement at "
					+ (System.currentTimeMillis() - timestart)
					+ " milliseconds.");
		}

		return ret;
	}
	
	public static void sendAck(DataOutputStream out) throws IOException{
		out.writeBytes("ack" + "\n");
		System.out.println("Acknowledgement sent at "
				+ (System.currentTimeMillis() - timestart)
				+ " milliseconds.");
	}
}


