package book.network_programming.ch4;

import java.net.InetAddress;
import java.net.NetworkInterface;
import java.net.SocketException;
import java.util.Enumeration;

public class Eth0InternetAddressLister {
	public static void main(String[] args) throws SocketException {
		NetworkInterface eth0 = NetworkInterface.getByName("eth0");
		Enumeration<InetAddress> addresses = eth0.getInetAddresses();
		while (addresses.hasMoreElements()) {
			System.out.println(addresses.nextElement());
		}
	}
}
