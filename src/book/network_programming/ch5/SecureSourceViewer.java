package book.network_programming.ch5;

import java.io.BufferedInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.Reader;
import java.net.Authenticator;
import java.net.MalformedURLException;
import java.net.URL;

/*
 * Example 5-12. A program to download password-protected web pages
 */
public class SecureSourceViewer {
	public static void main(String[] args) {
		Authenticator.setDefault(new DialogAuthenticator());

		for (int i = 0; i < args.length; i++) {
			try {
				// Open the URL for reading
				URL u = new URL(args[i]);
				try (InputStream in = new BufferedInputStream(u.openStream())) {
					// chain the InputStream to a Reader
					Reader r = new InputStreamReader(in);
					int c;
					while ((c = r.read()) != -1) {
						System.out.print((char) c);
					}
				}
			} catch (MalformedURLException e) {
				System.err.println(args[i] + " is not a parseable URL");
			} catch (IOException e) {
				System.err.println(e);
			}

			// print a blank line to separate pages
		}

		// Since we used the AWT, we have to explicitly exit.
		System.exit(0);
	}
}
