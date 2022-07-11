package book.network_programming.ch4;

import java.net.InetAddress;
import java.net.UnknownHostException;

/*
 * Example 4-7. Are www.ibiblio.org and helios.ibiblio.org the same?
 */
public class IBiblioAliases {
	public static void main(String[] args) {
		try {
			InetAddress ibiblio = InetAddress.getByName("www.biblio.org");
			InetAddress helios = InetAddress.getByName("helios.biblio.org");
			if (ibiblio.equals(helios)) {
				System.out.println("www.ibiblio.org is the same as helios.ibiblio.org");
			} else {
				System.out.println("www.ibiblio.org is not the same as helios.ibiblio.org");
			}
		} catch (UnknownHostException e) {
			System.out.println("Host lookup failed.");
		}
	}
}
