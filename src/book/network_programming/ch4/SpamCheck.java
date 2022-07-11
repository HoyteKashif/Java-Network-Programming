package book.network_programming.ch4;

import java.net.InetAddress;
import java.net.UnknownHostException;

/*
 * Example 4-9. SpamCheck
 */
public class SpamCheck {
	public static final String BLACKHOLE = "spl.spamhaus.org";

	public static void main(String[] args) {

		if (args == null || args.length == 0) {
			args = new String[3];
			args[0] = "207.34.56.23";
			args[1] = "125.12.32.4";
			args[2] = "130.130.130.130";
		}

		for (String arg : args) {
			if (isSpammer(arg)) {
				System.out.println(arg + " is a known spammer.");
			} else {
				System.out.println(arg + " appears legitimate.");
			}
		}
	}

	private static boolean isSpammer(String arg) {
		try {
			InetAddress address = InetAddress.getByName(arg);
			byte[] quad = address.getAddress();
			String query = BLACKHOLE;
			for (byte octet : quad) {
				int unsigned = octet < 0 ? octet + 256 : octet;
				// attach to the front in reverse order
				query = unsigned + "." + query;
			}
			InetAddress.getByName(query);
			return true;
		} catch (UnknownHostException e) {
			return false;
		}
	}
}
