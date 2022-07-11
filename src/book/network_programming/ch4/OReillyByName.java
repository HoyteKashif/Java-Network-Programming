package book.network_programming.ch4;

import java.net.InetAddress;
import java.net.UnknownHostException;

public class OReillyByName {
	public static void main(String[] args) {
		try {
			InetAddress address;

			address = InetAddress.getByName("www.oreilly.com");
			System.out.println(address);

			address = InetAddress.getByName("173.223.184.139");
			System.out.println(address.getHostName());

			InetAddress[] addresses = InetAddress.getAllByName("www.oreilly.com");
			for (InetAddress addr : addresses) {
				System.out.println(addr);
			}
			
			InetAddress me = InetAddress.getLocalHost();
			System.out.println(me);
		} catch (UnknownHostException e) {
			e.printStackTrace();
			System.out.println("Could not find www.oreilly.com");
		}
	}
}
