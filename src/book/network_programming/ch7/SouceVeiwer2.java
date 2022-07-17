package book.network_programming.ch7;

import java.io.BufferedInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.Reader;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLConnection;

/*
 * Example 7-1. Download a web page with a URLConnection
 */
public class SouceVeiwer2 {
	public static void main(String[] args) {
		if (args.length > 0) {
			try {
				// Open the URLConnection for reading
				URL u = new URL(args[0]);
				URLConnection uc = u.openConnection();
				try (InputStream raw = uc.getInputStream()) {
					InputStream buffer = new BufferedInputStream(raw);
					// chain the InputStream to a reader
					Reader reader = new InputStreamReader(buffer);
					int c;
					while ((c = reader.read()) != -1) {
						System.out.print((char) c);
					}
				}
			} catch (MalformedURLException e) {
				System.err.println(args[0] + " is not a parseable URL");
			} catch (IOException e) {
				System.err.println(e);
			}
		}
	}
}
