package book.network_programming.ch5;

import java.io.BufferedInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.Reader;
import java.net.MalformedURLException;
import java.net.URL;

/*
 * Example 5-2. Download a web page
 */
public class SourceViewer {
	public static void main(String[] args) {

		args = new String[1];
		args[0] = "https://www.oreilly.com"; // http://www.oreilly.com not working

		if (args.length > 0) {
			InputStream in = null;
			try {
				// Open the URL for reading
				URL u = new URL(args[0]);
				in = u.openStream();
				// buffer the input to increase performance
				in = new BufferedInputStream(in);
				// chain the InputStream to a Reader
				Reader r = new InputStreamReader(in);
				int c;
				while ((c = r.read()) != -1) {
					System.out.print((char) c);
				}
			} catch (MalformedURLException e) {
				System.err.println(args[0] + " is not a parseable URL");
			} catch (IOException e) {
				System.err.println(e);
			} finally {
				if (in != null) {
					try {
						in.close();
					} catch (IOException e) {
						// ignore
					}
				}
			}
		}
	}
}
