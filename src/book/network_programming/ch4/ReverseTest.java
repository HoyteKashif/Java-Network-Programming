package book.network_programming.ch4;

import java.net.InetAddress;
import java.net.UnknownHostException;

/*
 *  Example 4-3. Given the address, find the hostname
 */
public class ReverseTest {
	public static void main(String[] args) throws UnknownHostException {
		InetAddress ia = InetAddress.getByName("208.201.239.100");
		System.out.println(ia.getCanonicalHostName());
	}
}
