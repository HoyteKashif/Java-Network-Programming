package book.network_programming.ch7;

import java.io.BufferedInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.Reader;
import java.net.URL;
import java.net.URLConnection;
import java.util.Date;

/*
 * Example 7-13. Set ifModifiedSince to 24 hours prior to now.
 */
public class Last24 {
	public static void main(String[] args) {

		args = new String[1];
		args[0] = "http://elharo.com";

		// Initialize a Date object with the current date and time
		Date today = new Date();
		long millisecondPerDay = 24 * 60 * 60 * 1000;

		for (int i = 0; i < args.length; i++) {
			try {
				URL u = new URL(args[i]);
				URLConnection uc = u.openConnection();
				System.out.println("Original if modified since: " + new Date(uc.getIfModifiedSince()));
				uc.setIfModifiedSince((new Date(today.getTime() - millisecondPerDay)).getTime());
				System.out.println("Will retrieve file if it's modified since " + new Date(uc.getIfModifiedSince()));
				try (InputStream in = new BufferedInputStream(uc.getInputStream())) {
					Reader r = new InputStreamReader(in);
					int c;
					while ((c = r.read()) != -1) {
						System.out.print((char) c);
					}
					System.out.println();
				}
			} catch (IOException e) {
				System.err.println(e);
			}
		}
	}
}
