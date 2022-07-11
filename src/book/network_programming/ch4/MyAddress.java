package book.network_programming.ch4;

import java.net.InetAddress;
import java.net.UnknownHostException;

public class MyAddress {
	public static void main(String[] args) {

		// Example 4-2. Find the address of the local machine
		try {
			InetAddress address = InetAddress.getLocalHost();
			System.out.println(address);

		} catch (UnknownHostException e) {
			e.printStackTrace();
			System.out.println("Could not find this computer's address");
		}

		// Example 4-4. Find the IP address of the local machine
		try {
			InetAddress me = InetAddress.getLocalHost();
			String dottedQuad = me.getHostAddress();
			System.out.println("My address is " + dottedQuad);
		} catch (UnknownHostException e) {
			e.printStackTrace();
			System.out.println("I'm sorry. I don't know my own address.");
		}
	}
}
