package book.network_programming.ch4;

import java.net.InetAddress;
import java.util.concurrent.Callable;

/*
 * Example 4-11. LookupTask
 */
public class LookupTask implements Callable<String> {

	private String line;

	public LookupTask(String line) {
		this.line = line;
	}

	@Override
	public String call() throws Exception {
		try {
			// separate out the IP address
			int index = line.indexOf(' ');
			String address = line.substring(0, index);
			String theRest = line.substring(index);
			String hostname = InetAddress.getByName(address).getHostName();
			return hostname + " " + theRest;
		} catch (Exception e) {
			return line;
		}
	}

}
