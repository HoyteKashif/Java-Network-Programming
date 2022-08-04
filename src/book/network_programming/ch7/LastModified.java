package book.network_programming.ch7;

import java.io.IOException;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.Date;

/*
 * Example 7-15. Get the time when a URL was last changed
 */
public class LastModified {
	public static void main(String[] args) {
		args = new String[1];
		args[0] = "http://www.ibiblio.org/xml/";
		for (int i = 0; i < args.length; i++) {
			try {
				URL u = new URL(args[i]);
				HttpURLConnection http = (HttpURLConnection) u.openConnection();
				http.setRequestMethod("HEAD");
				System.out.println(u + " was last modified at " + new Date(http.getLastModified()));
			} catch (MalformedURLException e) {
				System.err.println(args[i] + " is not a URL I understand");
			} catch (IOException e) {
				System.err.println(e);
			}
			System.out.println();
		}
	}
}
