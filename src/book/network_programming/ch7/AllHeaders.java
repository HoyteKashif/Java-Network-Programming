package book.network_programming.ch7;

import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLConnection;

/*
 * Example 7-5. Print the entire HTTP header
 */
public class AllHeaders {
	public static void main(String[] args) {

		args = new String[1];
		args[0] = "https://www.oreilly.com/";

		for (int i = 0; i < args.length; i++) {
			try {
				URL u = new URL(args[i]);
				URLConnection uc = u.openConnection();

				/*
				 * The request method is header zero and has a null key. In HTTP, the starter
				 * line containing the request method and path is header field zero and the
				 * first actual header is one.
				 */
				System.out.println("Request Method: " + uc.getHeaderField(0));

				for (int j = 1;; j++) {
					String header = uc.getHeaderField(j);
					if (header == null) {
						break;
					}
					System.out.println(uc.getHeaderFieldKey(j) + ": " + header);
				}

			} catch (MalformedURLException e) {
				System.err.println(args[i] + " is not a URL I understand.");
			} catch (IOException e) {
				System.err.println(e);
			}
			System.out.println();
		}
	}
}
