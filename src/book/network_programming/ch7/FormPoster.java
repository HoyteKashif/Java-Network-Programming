package book.network_programming.ch7;

import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.io.Reader;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLConnection;

import book.network_programming.ch5.QueryString;

/*
 * Example 7-14. Posting a form
 */
public class FormPoster {

	private URL url;

	private QueryString query = new QueryString();

	public FormPoster(URL url) {
		if (!url.getProtocol().toLowerCase().startsWith("http")) {
			throw new IllegalArgumentException("Posting only works for http URLs");
		}
		this.url = url;
	}

	public void add(String name, String value) {
		query.add(name, value);
	}

	public URL getURL() {
		return this.url;
	}

	public InputStream post() throws IOException {
		URLConnection uc = url.openConnection();
		uc.setDoOutput(true);
		try (OutputStreamWriter out = new OutputStreamWriter(uc.getOutputStream(), "UTF-8")) {
			// The POST line, the Content-type header,
			// and the content-length headers are sent by the URLConnection.
			// We just need to send the data
			out.write(query.toString());
			out.write("\r\n");
			out.flush();
		}

		// return the response
		return uc.getInputStream();
	}

	public static void main(String[] args) {
		URL url;
		if (args.length > 0) {
			try {
				url = new URL(args[0]);
			} catch (MalformedURLException e) {
				System.err.println("Usage: java FormPoster url");
				return;
			}
		} else {
			try {
				url = new URL("http://www.cafeaulait.org/books/jnp4/postquery.phtml");
			} catch (MalformedURLException e) {
				System.err.println(e);
				return;
			}
		}

		FormPoster poster = new FormPoster(url);
		poster.add("name", "Elliotte Rusty Harold");
		poster.add("email", "elharo@ibiblio.org");

		try (InputStream in = poster.post()) {
			// Read the response
			Reader r = new InputStreamReader(in);
			int c;
			while ((c = r.read()) != -1) {
				System.out.print((char) c);
			}
			System.out.println();
		} catch (IOException e) {
			System.err.println(e);
		}
	}
}
