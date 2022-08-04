package book.network_programming.ch8;

import java.io.IOException;
import java.net.Socket;
import java.net.UnknownHostException;

/**
 * Example 8-5. Find out which of the first 1024 ports seem to be hosting TCP
 * servers on a specified host
 */
public class LowPortScanner {
	public static void main(String[] args) {
		String host = args.length > 0 ? args[0] : "localhost";

		for (int i = 0; i < 1024; i++) {
			try {
				Socket s = new Socket(host, i);
				System.out.println("There is a server on port " + i + " of " + host);
				s.close();
			} catch (UnknownHostException ex) {
				System.err.println(ex);
			} catch (IOException ex) {
				// must not be a server on this port
			}
		}
	}
}
