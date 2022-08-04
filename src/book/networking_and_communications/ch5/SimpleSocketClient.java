package book.networking_and_communications.ch5;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.net.Socket;

/*
 * Example 5-1 Implementation of a Simple Socket Client. 
 * A class that implements a simple client to a remote service using a Socket.
 */
public class SimpleSocketClient extends Thread {
	public static final int PORTNUM_ECHO = 7; // RFC 862
	public static final int PORTNUM_DAYTIME = 13;// RFC 867
	public static final int PORTNUM_CHARGEN = 19;// RFC 864

	public boolean fDebugOn = true;

	protected DataInputStream fRemoteInputStream;
	protected DataOutputStream fRemoteOutputStream;

	protected Socket fRemoteHostSocket;
	protected String fHostName;
	protected int fPortNum;

	public static void main(String[] args) {
		new SimpleSocketClient().start();
	}

	/**
	 * Default Constructor
	 */
	public SimpleSocketClient() {
//		fHostName = "netcom.com"; // doesn't work
		fHostName = "time-a-g.nist.gov"; // https://tf.nist.gov/tf-cgi/servers.cgi
		fPortNum = PORTNUM_DAYTIME;
	}

	/**
	 * Give a host name and a port to connect to
	 * 
	 * @param hostName The name of the host to connect to i.e. "www.rawthought.com"
	 * @param portNum  The port to connect to i.e. 80 for HTTP, 79 for Finger, etc.
	 */
	public SimpleSocketClient(String hostName, int portNum) {
		// tuck away our instance variables
		fHostName = hostName;
		fPortNum = portNum;
	}

	/**
	 * We use this method as an "autopilot." Given the initial setup info, proceeds
	 * to execute a single session in a separate thread. It opens a connection,
	 * handles the session, then closes the connection when done
	 */
	public void run() {
		if (fDebugOn) {
			System.out.println("Running SimpleSocketClient...");
		}
		if (MOpenConnection()) {
			// if we opened the connection successfully
			MHandleSession(); // handle the session...
			MCloseConnection(); // close the connection
		}
	}

	/**
	 * Actually connect to the remote host. When this fails, it's typically because
	 * we can't get a connection, either because our local host is having Inet
	 * access problems, or because the remote host isn't there (or was incorrectly
	 * specified), or because the remote host doesn't provide a service on the given
	 * port.
	 * 
	 * @return True if connection was opened successfully.
	 */
	public boolean MOpenConnection() {
		boolean success = true; // start off assuming we'll be successful

		if (fDebugOn) {
			System.out.println("Opening Connection...");
		}

		try {
			// create and open a socket to the given host and port
			fRemoteHostSocket = new Socket(fHostName, fPortNum);

			try {
				// get the output stream that we can use to send data
				fRemoteOutputStream = new DataOutputStream(fRemoteHostSocket.getOutputStream());

				// get the input stream that we can use to receive data
				fRemoteInputStream = new DataInputStream(fRemoteHostSocket.getInputStream());
			} catch (IOException streamEx) {
				success = false;
				if (fDebugOn) {
					System.err.println("building streams failed: " + streamEx);
				}
				// need to shut down the socket we've already opened!
				MCloseConnection();
			}
		} catch (IOException sockEx) {
			success = false;
			if (fDebugOn) {
				System.err.println("open socket failed: " + sockEx);
			}
		}

		return success;
	}

	/**
	 * Handle a single session with the remote host. This default method reads input
	 * until it's done, then gives up
	 */
	public void MHandleSession() {

		String curLine = "";

		if (fDebugOn) {
			System.out.println("Handling Session...");
		}

		do {
			// dump the current line to stdout
			if (fDebugOn) {
				System.out.println(curLine);
			}

			try {
				// get the next line
				curLine = fRemoteInputStream.readLine();
			} catch (IOException readEx) {
				// we expect this throw when we reach EOF on the stream
				curLine = null;
			}
		} while (curLine != null);

	}

	/**
	 * Close the open connection
	 */
	public void MCloseConnection() {
		if (fDebugOn) {
			System.out.println("Closing connection...");
		}

		try {
			// only attempt to close if we've got a valid socket
			if (fRemoteHostSocket != null) {
				fRemoteHostSocket.close();
			}

			fRemoteOutputStream = null;
			fRemoteInputStream = null;
		} catch (IOException closeEx) {
			if (fDebugOn) {
				System.err.println("socket close threw: " + closeEx);
			}
		}

	}
}
