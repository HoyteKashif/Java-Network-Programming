package book.network_programming.ch8;

import java.io.IOException;
import java.net.Socket;
import java.net.SocketException;
import java.net.UnknownHostException;

/**
 * Example 8-6. Get a socket's information
 */
public class SocketInfo {
	public static void main(String[] args) {
		
		args = new String[4];
		args[0] = "www.oreilly.com";
		args[1] = "www.oreilly.com";
		args[2] = "www.elharo.com";
		args[3] = "login.ibiblio.org";
		
		for (String host : args) {
			try {
				Socket theSocket = new Socket(host, 80);
				System.out.println("Connected to " + theSocket.getInetAddress() 
					+ " on port " + theSocket.getPort() + " from port " 
					+ theSocket.getLocalPort() + " of " 
					+ theSocket.getLocalAddress());
				theSocket.close();
			} catch (UnknownHostException ex) {
				System.err.println("I can't find " + host);
			} catch (SocketException ex) {
				System.err.println("Could not connect to " + host);
			} catch (IOException ex) {
				System.err.println(ex);
			}
		}
	}
}
