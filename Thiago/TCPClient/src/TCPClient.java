import java.io.BufferedReader;
import java.io.DataOutputStream;
import java.io.InputStreamReader;
import java.net.*;

class TCPClient
{
	public static void main(String args[]) throws Exception
	{
		long timestart = System.currentTimeMillis();
		Socket clientSocket = null;
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
			InetAddress IPAddress = InetAddress.getByName(serverIP);
			clientSocket = new Socket(IPAddress, port);
			DataOutputStream outToServer = new DataOutputStream(clientSocket.getOutputStream());
			BufferedReader inFromServer = new BufferedReader(new InputStreamReader(clientSocket.getInputStream()));
			
			String data = operation + " " + key + " " + value;
			outToServer.writeBytes(data + "\n");
		
			if (operation.toLowerCase().matches("get"))
			{
				clientSocket.setSoTimeout(1000); // Set a 1 second timeout for the Value packet to arrive
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
			}
			break; // end the client sequence
		}
		clientSocket.close();
		System.out.println("Client Close at " + (System.currentTimeMillis()-timestart) + " milliseconds");
	}
}
