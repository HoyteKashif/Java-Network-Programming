package book.network_programming.ch7;

import java.io.IOException;
import java.net.URL;
import java.net.URLConnection;

/*
 * Example 7-12. Print the URL of a URLConnection to http://www.oreilly.com/
 */
public class URLPrinter {
	public static void main(String[] args) {
		try {
			URL u = new URL("https://www.oreilly.com/");
			URLConnection uc = u.openConnection();
			System.out.println(uc.getURL());
		} catch (IOException e) {
			System.err.println(e);
		}
	}
}
