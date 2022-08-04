package book.networking_and_communications.ch3;

import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLConnection;

/*
 * Example 3-2 An Application for Demonstrating the URLConnection States.
 * 
 */
public class URLConnectionTestSnippet {

	/**
	 * Code snippet from URLConnectionTest.java A method that loads the given URL
	 * and demonstrates the various states of a URLConnection.
	 * 
	 * @param specStr The URL to be loaded.
	 */
	public void loadURl(String specStr) {

		URL tempURL;
		URLConnection theURLConnection;

		InputStream tempInputStream;
		int curByte;

		try {
			tempURL = new URL(specStr);
			System.out.println("protocol: " + tempURL.getProtocol());
			System.out.println("host: " + tempURL.getHost());
			System.out.println("port: " + tempURL.getPort());
			System.out.println("filename: " + tempURL.getFile());

			try {
				// first, get a URLConnection for the given URL object
				System.out.println("openConnection...");
				theURLConnection = tempURL.openConnection();

				// Check stuff that doesn't require that an actual connection be opened
				System.out.println("getUseCaches: " + theURLConnection.getUseCaches());
				System.out.println("getDefaultUseCaches: " + theURLConnection.getDefaultUseCaches());
				System.out.println(
						"getDefaultAllowUserInteraction: " + theURLConnection.getDefaultAllowUserInteraction());
				System.out.println("getAllowUserInteraction: " + theURLConnection.getAllowUserInteraction());
				System.out.println("ifModifiedSince: " + theURLConnection.getIfModifiedSince());
				System.out.println("doInput: " + theURLConnection.getDoInput());
				System.out.println("doOutput: " + theURLConnection.getDoOutput());

				// Now, go ahead and connect
				System.out.println("getInputStream...");
				// open the connection, get data.
				// calls connect implicitly
				tempInputStream = theURLConnection.getInputStream();

				// Check stuff that requires a connection be open
				System.out.println("getContentEncoding: " + theURLConnection.getContentEncoding());
				System.out.println("getContentLength: " + theURLConnection.getContentLength());
				System.out.println("getContentType: " + theURLConnection.getContentType());
				System.out.println("getLastModified: " + theURLConnection.getLastModified());
				System.out.println("getDate: " + theURLConnection.getDate());
				System.out.println("getExpiration: " + theURLConnection.getExpiration());

				// note that at this point it's too late to call things like setDoOutput. If you
				// do, it throws a java.lang.IllegalAccessError: Already connected.

				try {
					// create a unique output filename
					String newFileName = String.valueOf(tempURL.hashCode()) + ".htm";
					System.out.println("Opening outstream..." + newFileName);

					// open output file
					FileOutputStream outstream = new FileOutputStream(newFileName);

					System.out.println("Copying Data...");
					try {
						while ((curByte = tempInputStream.read()) != -1) {
							// simple byte for byte copy
							outstream.write(curByte);
						}
						System.out.println("Done!");
						outstream.close();// be nice
					} catch (IOException copyEx) {
						System.err.println("copyEx: " + copyEx);
					}
				} catch (Exception fileOpenEx) {
					System.err.println("fileOpenEx: " + fileOpenEx);
				}
			} catch (IOException contentEx) {
				System.err.println("retrieve ex: " + contentEx);
			}

		} catch (MalformedURLException murlEx) {
			System.err.println("loadURL threw ex: " + murlEx);
		}
	}// loadURL
}
