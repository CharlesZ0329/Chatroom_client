import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.io.PrintWriter;
import java.net.InetAddress;
import java.net.Socket;
import java.net.SocketException;

/**This class hold all attributes and behaviors a client instance should have.
 * 
 */
public class ClientInstance {
	public BufferedReader kb;
	// the server socket
	public static Socket clientSocket;

	public ClientInstance() {
		// TODO Auto-generated constructor stub
		runClient();
	}

	public void runClient() {

		// session-handling thread
		Thread t1;
		Thread t2;

		try {
			// keyboard for user typing
			kb = new BufferedReader(new InputStreamReader(System.in));
			System.out.println("System: Type the host's IP address");
			String addr = kb.readLine();
			InetAddress address = InetAddress.getByName(addr);
			System.out.println("System: Type the port number you wanna connect: ");
			int port = Integer.parseInt(kb.readLine());

			// set up server socket
			clientSocket = new Socket(address, port);

			// start session-handler in new thread
			t1 = new Thread(new inputHandler(clientSocket));
			t2 = new Thread(new outputHandler(clientSocket));
			t1.start();
			t2.start();
		} catch (SocketException se) {
			/*
			 * will be thrown when accept() is called after closing the server
			 * socket, in method shutDown(). If shutDownCalled, then simply
			 * exit; otherwise, something else has happened:
			 */
			System.err.println(se.getMessage());
		} catch (IOException ioe) {
			System.err.println("I/O error:");
			System.err.println(ioe.getMessage());
			System.exit(1);
		}
	}

	/**
	 * class for input thread handler.
	 *
	 */
	class inputHandler implements Runnable {
		// the connection to the remote client
		private Socket client;

		inputHandler(Socket s) {
			client = s;
		}

		public void run() {
			// for I/O
			BufferedReader in = null;
			try {
				// set up I/O
				in = new BufferedReader(new InputStreamReader(client.getInputStream()));

				// for client input
				String line;
				while ((line = in.readLine()) != null) {

					System.out.println("\n" + line);

				}
			} catch (IOException e) {
				// fatal error for this session
				System.err.println(e.getMessage());
			} finally {
				try {
					in.close();
				} catch (IOException e) {
				}
			}
		}
	}
	/**
	 * class for output thread handler
	 *
	 */
	class outputHandler implements Runnable {
		private Socket client;

		outputHandler(Socket s) {
			client = s;
		}

		public void run() {
			PrintWriter out = null;// for I/O
			try {
				// set up I/O
				out = new PrintWriter(new OutputStreamWriter(client.getOutputStream()));
				kb = new BufferedReader(new InputStreamReader(System.in));
				// for client input
				boolean done = false;
				String line;
				while (!done) {
					line = kb.readLine();
					out.println(line);
					if (line == null) {
						// quit
						done = true;
					}
					out.flush();

				}
			} catch (IOException e) {
				// fatal error for this session
				System.err.println(e.getMessage());
			} finally { // close connections
				out.close();
			}
		}
	}

}
