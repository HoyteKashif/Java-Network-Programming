package book.networking_and_communications.ch5;

import java.io.IOException;

/*
 * Example 5-2 Implementation of a Finger Client. A class which extends SimpleSocketClient to implement a Finger Client
 */
public class FingerClient extends SimpleSocketClient {
	
	public static void main(String[] args) {
//		new FingerClient("finger.farm");
//		new FingerClient("graph.no");
		new FingerClient("coke@1.gp.cs.cmu.edu");
	}
	
	public static final int PORTNUM_FINGER = 79;

	// by default we just make a blanket finger request (no Specific username)
	protected String fUserName = "";

	/**
	 * This constructor takes two Strings
	 * 
	 * @param hostName The host on which to perform the finger
	 * @param userName The user on which to perform the finger
	 */
	public FingerClient(String hostName, String userName) {
		// give SimpleSocketClient the required connection info
		super(hostName, PORTNUM_FINGER);

		// tuck away the userName for our MHandleSession method
		fUserName = userName;

		// kickstart the SimpleSocketClient's thread
		this.start();
	}

	/**
	 * This constructor takes on string:
	 * 
	 * @param targetStr A string of the form "username@host"
	 */
	public FingerClient(String targetStr) {
		super();// call superclass constructor first

		// find the last @
		int atSignIdx = targetStr.lastIndexOf('@');

		// valid username
		if (atSignIdx > 0) {
			// get the username
			fUserName = targetStr.substring(0, atSignIdx);
			String hostName = targetStr.substring(atSignIdx + 1, targetStr.length());
			// set the host name in the superclass
			fHostName = hostName;
		} else {
			// just a hostname
			fUserName = ""; // empty user name
			// set the host name in the superclass
			fHostName = targetStr;
		}
		// set the port number in the superclass
		fPortNum = PORTNUM_FINGER;

		// kickstart the SimpleSocketClient's thread
		this.start();
	}

	/**
	 * Overrides SimpleSocketClient.MHandleSession Perform a Finger Session.
	 */
	public void MHandleSession() {
		String curLine = "Reading data...";

		try {
			// Start by sending the requested username
			// Send the requested username plus a terminating carriage return
			fRemoteOutputStream.writeBytes(fUserName + "\n");
		} catch (IOException writeEx) {
			if (fDebugOn) {
				System.err.println("write failed with: " + writeEx);
			}
		}

		do {
			// dump the current line to stdout
			System.out.println(curLine);

			try {
				// get the next line of input
				curLine = fRemoteInputStream.readLine();
			} catch (IOException readEx) {
				// we expect this throw when we reach EOF on the stream
				curLine = null;
			}
		} while (curLine != null);

		System.out.println("<EOF>");// dump a finished not to stdout
	}
}
