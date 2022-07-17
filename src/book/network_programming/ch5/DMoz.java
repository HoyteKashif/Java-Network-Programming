package book.network_programming.ch5;

import java.io.BufferedInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.MalformedURLException;
import java.net.URL;

/*
 * Example 5.10. Do an Open Directory search. Site is not reachable so
 *  used https://www.bing.com/search?q=java instead.
 */
public class DMoz {
	public static void main(String[] args) {

		args = new String[1];
		args[0] = "java";

		String target = "";
		for (int i = 0; i < args.length; i++) {
			target += args[i] + " ";
		}
		target = target.trim();

		QueryString query = new QueryString();
		query.add("q", target);

		try {
			URL u = new URL("https://www.bing.com/search?" + query);
			try (InputStream in = new BufferedInputStream(u.openStream())) {
				InputStreamReader theHTML = new InputStreamReader(in);
				int c;
				while ((c = theHTML.read()) != -1) {
					System.out.print((char) c);
				}
			}
		} catch (MalformedURLException e) {
			System.err.println(e);
		} catch (IOException e) {
			System.err.println(e);
		}

	}
}
