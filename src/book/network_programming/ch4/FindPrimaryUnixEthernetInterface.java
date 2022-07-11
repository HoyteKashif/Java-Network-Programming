package book.network_programming.ch4;

import java.net.NetworkInterface;
import java.net.SocketException;

public class FindPrimaryUnixEthernetInterface {
	public static void main(String[] args) {
		try {
			NetworkInterface ni = NetworkInterface.getByName("eth0");
			if (ni == null) {
				System.err.println("No such interface: eth0");
			} else {
				System.out.println(ni);
			}
		} catch (SocketException e) {
			System.err.println("Could not list sockets");
			System.err.println(e);
		}
	}
}
