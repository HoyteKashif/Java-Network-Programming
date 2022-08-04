package book.networking_and_communications.ch5;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.net.Socket;

import book.networking_and_communications.shared.HttpTransactionHandler;

/**
 * A class that manages a single HTTP connection We've migrated all the
 * connection-oriented stuff from HttpdAsync into this class.
 */
public class HttpConnectionMgr extends Thread {

	public boolean fDebugOn = true;

	protected Socket fClientSocket = null;
	protected DataInputStream fClientInputStream;
	protected DataOutputStream fClientOutputStream;
	protected HttpTransactionHandler fTransactionHandler;

	public HttpConnectionMgr(Socket clientSocket) {
		fClientSocket = clientSocket;
		this.start();
	}

	@Override
	public void run() {
		try {
			if (fDebugOn) {
				System.out.println("building iostreams...");
			}
			fClientInputStream = new DataInputStream(fClientSocket.getInputStream());
			fClientOutputStream = new DataOutputStream(fClientSocket.getOutputStream());

			if ((fClientInputStream != null) && (fClientOutputStream != null)) {
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
		} catch (IOException ioEx) {
			System.err.println("HttpConnectionMgr run ioEx: " + ioEx);
		}
	}
}
