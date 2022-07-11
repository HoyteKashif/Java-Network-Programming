package book.network_programming.ch4;

import java.net.InetAddress;
import java.net.UnknownHostException;

public class AddressTests {
	public static void main(String[] args) throws UnknownHostException {
		InetAddress localhost = InetAddress.getLocalHost();
		System.out.println(localhost);
		System.out.println("IPv" + getVersion(localhost) + " address");
	}

	public static int getVersion(InetAddress ia) {
		byte[] address = ia.getAddress();
		if (address.length == 4)
			return 4;
		else if (address.length == 6)
			return 6;
		else
			return -1;
	}

}
