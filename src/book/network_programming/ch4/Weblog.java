package book.network_programming.ch4;

import java.io.BufferedReader;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.Reader;
import java.net.InetAddress;
import java.net.UnknownHostException;

public class Weblog {
	public static void main(String[] args) {
		try (FileInputStream fin = new FileInputStream(args[0])) {
			Reader in = new InputStreamReader(fin);
			BufferedReader bin = new BufferedReader(in);
			for (String entry = bin.readLine(); entry != null; entry = bin.readLine()) {
				// separate out the IP address
				int index = entry.indexOf(' ');
				String ip = entry.substring(0, index);
				String theRest = entry.substring(index);

				// Ask DNS for the hostname and print it out
				try {
					InetAddress address = InetAddress.getByName(ip);
					System.out.println(address.getHostName() + theRest);
				} catch (UnknownHostException e) {
					System.err.println(entry);
					System.err.println(e);
				}
			}
		} catch (IOException e) {
			System.out.println(e);
		}
	}
}
