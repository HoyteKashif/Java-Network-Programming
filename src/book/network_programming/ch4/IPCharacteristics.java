package book.network_programming.ch4;

import java.net.InetAddress;
import java.net.UnknownHostException;

// Example 4-6. Testing the characteristics of an IP Address
public class IPCharacteristics {
	public static void main(String[] args) {
		String[] a = new String[7];
		a[0] = "127.0.0.1";
		a[1] = "192.168.254.32";
		a[2] = "www.oreilly.com";
		a[3] = "224.0.2.1";
		a[4] = "FF01:0:0:0:0:0:0:1";
		a[5] = "FF05:0:0:0:0:0:0:101";
		a[6] = "0::1";

		for (String s : a) {
			try {
				characterize(InetAddress.getByName(s));
			} catch (UnknownHostException e) {
				System.err.println("Could not resolve " + s);
				e.printStackTrace();
			}
			System.out.println();
		}
	}

	public static void characterize(InetAddress ia) {
		if (ia.isAnyLocalAddress()) {
			System.out.println(ia + " is a wildcard address.");
		}

		if (ia.isLoopbackAddress()) {
			System.out.println(ia + " is loopback address.");
		}

		if (ia.isLinkLocalAddress()) {
			System.out.println(ia + " is a link-local address.");
		} else if (ia.isSiteLocalAddress()) {
			System.out.println(ia + " is a site-local address.");
		} else {
			System.out.println(ia + " is a global address.");
		}

		if (ia.isMulticastAddress()) {
			if (ia.isMCGlobal()) {
				System.out.println(ia + " is a global multicast address.");
			} else if (ia.isMCOrgLocal()) {
				System.out.println(ia + " is an organization wide multicast address.");
			} else if (ia.isMCSiteLocal()) {
				System.out.println(ia + " is a site wide multicast address.");
			} else if (ia.isMCLinkLocal()) {
				System.out.println(ia + " is a subnet wide multicast address.");
			} else if (ia.isMCNodeLocal()) {
				System.out.println(ia + " is a interface-local multicast address.");
			} else {
				System.out.println(ia + " is an unknown multicast address type.");
			}
		} else {
			System.out.println(ia + " is a unicast address.");
		}
	}
}
