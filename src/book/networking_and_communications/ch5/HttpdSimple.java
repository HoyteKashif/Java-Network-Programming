package book.networking_and_communications.ch5;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.net.ServerSocket;
import java.net.Socket;

import book.networking_and_communications.shared.HttpTransactionHandler;

/**
 * Example 5-3 A Simple Web Server Implementation. A class that implements a
 * simple, synchronous HTTP server.
 */
public class HttpdSimple {
	public final static int DEFAULT_PORT = 80;

	public boolean fDebugOn = true;

	protected int fPort;

	private ServerSocket fMainListenSocket = null;
	private boolean fContinueListening = true;
	Socket fClientSocket = null;
	DataInputStream fClientInputStream;
	DataOutputStream fClientOutputStream;

	HttpTransactionHandler fTransactionHandler;

	/**
	 * @param port
	 */
	public HttpdSimple(int port) {
		if (port == 0) {
			fPort = DEFAULT_PORT;
		} else {
			fPort = port;
		}
	}

	/**
	 * This method waits for an incoming connection, opens input and output streams
	 * on that connection, then uses HttpTransactionHandler to complete the request.
	 * Once the request has been completed, shuts down the connection and begins
	 * waiting for a new connection.
	 */
	public void doIt() {
		// first, bump our priority a little bit...
		try {
			Thread.currentThread().setPriority(Thread.MIN_PRIORITY + 1);
		} catch (IllegalArgumentException badPriorityEx) {
			System.err.println("setPriority ex: " + badPriorityEx);
		}

		// create a new ServerSocket
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

		// Listen for traffic
		try {
			while (fContinueListening) {
				if (fDebugOn) {
					System.out.println("server accepting...");
				}

				fClientSocket = fMainListenSocket.accept(); // this blocks!

				if (fClientSocket != null) {
					// okay, we now have a client!
					if (fDebugOn) {
						System.out.println("building iostreams...");
					}
					fClientInputStream = new DataInputStream(fClientSocket.getInputStream());
					fClientOutputStream = new DataOutputStream(fClientSocket.getOutputStream());

					if ((fClientOutputStream != null) && (fClientInputStream != null)) {
						// now, handle the transaction
						fTransactionHandler = new HttpTransactionHandler(fClientInputStream, fClientOutputStream);

						try {
							fTransactionHandler.handleTransaction();
						} catch (Exception handleTransEx) {
							System.err.println("handleTransaction ex: " + handleTransEx);
						}

						// we no longer need the transaction handler
						fTransactionHandler = null;

						// we no longer need the client input stream!
						fClientInputStream = null;

						// we no longer need the client output stream!
						fClientOutputStream = null;
					} else {
						if (fClientOutputStream == null) {
							System.err.println("fClientOutputStream null!");
						}
						if (fClientInputStream == null) {
							System.err.println("fClientInputStream null!");
						}
					}

					if (fDebugOn) {
						System.out.println("closing fClientSocket...");
					}

					try {
						fClientSocket.close();
					} catch (Exception clientSocketCloseEx) {
						System.err.println("fClientSocket.close() threw: " + clientSocketCloseEx);
					}

					fClientSocket = null;
					if (fDebugOn) {
						System.out.println("done with cleanup...");
					}
				}
			}
		} catch (Exception loopEx) {
			System.err.println("main loop ex: " + loopEx);
		}
	}
}
