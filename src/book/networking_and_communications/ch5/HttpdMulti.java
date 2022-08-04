package book.networking_and_communications.ch5;

import java.net.ServerSocket;
import java.net.Socket;

/**
 * Improved version of httpd server: Allows multiple "simultaneous" clients.
 * Improvement can be to use ThreadGroups to make it easier to shut down all
 * client connections at once (when the server is being shut down).
 */
public class HttpdMulti extends Thread {
	public final static int DEFAULT_PORT = 80;

	public boolean fDebugOn = true;

	protected int fPort; // which port we actually end up using
	protected ServerSocket fMainListenSocket = null; // main server socket
	public boolean fContinueListening = true;

	public HttpdMulti(int port) {
		if (port == 0) {
			fPort = DEFAULT_PORT;
		} else {
			fPort = port;
		}

		if (fDebugOn) {
			System.out.println("done instantiating...");
		}
	}

	/**
	 * This method waits for an incoming connection, opens input and output streams
	 * on that connection, then uses HttpTransactionHandler to complete the request.
	 * Once the request has been completed, shuts down the connection and begins
	 * waiting for a new connection.
	 */
	@Override
	public void run() {
		// create a new Server Socket
		try {
			if (fDebugOn) {
				System.out.println("building fMainListenSocket...");
			}
			fMainListenSocket = new ServerSocket(fPort);
		} catch (Exception e) {
			System.err.println("build fMainListenSocket threw: " + e);
			return;
		} finally {
			if (fMainListenSocket == null) {
				System.err.println("Couldn't create a new ServerSocket!");
				return;
			}

			if (fDebugOn) {
				System.out.println("fMainListenSocket initialized on port..." + fPort);
			}
		}

		try {
			while (fContinueListening) {
				if (fDebugOn) {
					System.out.println("server accepting...");
				}

				Socket clientSocket = fMainListenSocket.accept();

				if (clientSocket != null) {
					HttpConnectionMgr mgr = new HttpConnectionMgr(clientSocket);
				}
			}
		} catch (Exception loopEx) {
			System.err.println("main loop ex: " + loopEx);
		}
	}
}
