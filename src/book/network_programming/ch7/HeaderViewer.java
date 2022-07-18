package book.network_programming.ch7;

import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLConnection;
import java.util.Date;

/*
 * Example 7-4 return the header
 */
public class HeaderViewer {
	public static void main(String[] args) {

		/*
		 * Many servers don't bother to provide a Content-length header for text files.
		 * However, a Content-length header should always be sent for a binary file.
		 */

		args = new String[4];
		args[0] = "https://www.oreilly.com/";
		args[1] = "https://www.oreilly.com/favicon.ico";
		args[2] = "https://www.google.com/";
		args[3] = "https://www.google.com/favicon.ico";

		for (int i = 0; i < args.length; i++) {
			try {
				URL u = new URL(args[i]);
				URLConnection uc = u.openConnection();
				System.out.println("Content-type: " + uc.getContentType());
				if (uc.getContentEncoding() != null) {
					System.out.println("Content-encoding: " + uc.getContentEncoding());
				}
				if (uc.getDate() != 0) {
					System.out.println("Date: " + new Date(uc.getDate()));
				}
				if (uc.getLastModified() != 0) {
					System.out.println("Last modified: " + new Date(uc.getLastModified()));
				}
				if (uc.getExpiration() != 0) {
					System.out.println("Expiration date: " + new Date(uc.getExpiration()));
				}
				if (uc.getContentLength() != -1) {
					System.out.println("Content-length: " + uc.getContentLength());
				}
				if (uc.getContentLengthLong() != -1) {
					System.out.println("Content-length-long: " + uc.getContentLengthLong());
				}
			} catch (MalformedURLException e) {
				System.err.println(args[i] + " is not a URL I understand");
			} catch (IOException e) {
				System.err.println(e);
			}
			System.out.println();
		}
	}
}
