package book.network_programming.ch4;

import java.net.InetAddress;
import java.net.NetworkInterface;
import java.net.SocketException;
import java.net.UnknownHostException;

public class FindLoopbackNetworkInterface {
	public static void main(String[] args) {
		try {
			InetAddress local = InetAddress.getByName("127.0.0.1");
			NetworkInterface ni = NetworkInterface.getByInetAddress(local);
			if (ni == null) {
				System.err.println("That's weird. No local loopback address.");
			} else {
				System.out.println(local);
				System.out.println(ni);
			}
		} catch (SocketException e) {
			System.err.println("Could not list network interfaces.");
			System.err.println(e);
		} catch (UnknownHostException e) {
			System.err.println("That's weird. Could not lookup 127.0.0.1");
			System.err.println(e);
		}
	}
}
