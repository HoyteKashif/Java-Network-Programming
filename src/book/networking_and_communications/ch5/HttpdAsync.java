package book.networking_and_communications.ch5;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.net.ServerSocket;
import java.net.Socket;

import book.networking_and_communications.shared.HttpTransactionHandler;

/**
 * Improved version of httpd server: Places main loop into a separate thread, so
 * it doesn't lock up the system
 */
public class HttpdAsync extends Thread {

	public final static int DEFAULT_PORT = 80;

	public boolean fDebugOn = true;

	protected int fPort;

	protected ServerSocket fMainListenSocket = null;
	protected boolean fContinueListening = true;
	protected Socket fClientSocket = null;
	protected DataInputStream fClientInputStream;
	protected DataOutputStream fClientOutputStream;
	protected HttpTransactionHandler fTransactionHandler;

	/**
	 * Instantiate and init
	 * 
	 * @param port
	 */
	public HttpdAsync(int port) {
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
		// create a new ServerSocket
		try {
			if (fDebugOn) {
				System.out.println("building fMainListenSocket...");
			}
			fMainListenSocket = new ServerSocket(fPort);
		} catch (Exception e) {
			System.err.println("build fMainListSocket threw: " + e);
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

						// we no longer need the client input stream
						fClientInputStream = null;

						// we no longer need the client output stream
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