package book.networking_and_communications.ch3;

import java.awt.Button;
import java.awt.Event;
import java.awt.FlowLayout;
import java.awt.Frame;
import java.awt.Panel;
import java.awt.TextField;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.MalformedURLException;
import java.net.URL;

/**
 * Example 3-1 (49-51) Downloading Web Content Given a URL String. A class to
 * demonstrate simple programmatic access of Web content using the URL class.
 */
public class URL_Loader extends Frame {
	private static final long serialVersionUID = 1L;

	public boolean fDebugOn = true; // toggles debugging

	protected TextField fInputField; // user input field
	protected URL fDefaultURL = null; // used as default URL

	/**
	 * Build the appropriate UI elements for punching in a URL.
	 */
	public URL_Loader() {
		// create the main window
		setTitle("Load a URL");
		Panel p = new Panel();
		p.setLayout(new FlowLayout());

		// create an input field for typing in the URL
		fInputField = new TextField(40);
		p.add(fInputField);

		// add a button so the user can tell us to go
		p.add(new Button("Go!"));

		add("North", p);

		// build the default base URL
		try {
			fDefaultURL = new URL("http://www.rawthought.com/");
		} catch (MalformedURLException e) {
			System.err.println("build of fDefaultURL failed: " + e);
		}
	}

	/**
	 * Handle the WINDOW_DESTROY event as an app closure.
	 */
	public boolean handleEvent(Event evt) {
		if (evt.id == Event.WINDOW_DESTROY) {
			System.exit(0);
		}
		return super.handleEvent(evt);
	}

	/**
	 * Handle the user clicking on the "Go!" button
	 */
	public boolean action(Event evt, Object arg) {
		if (arg.equals("Go!")) {
			// slurp the string from the input field
			String urlString = fInputField.getText();
			loadURL(urlString);
			return true; // we handled this event
		} else {
			// allow our superclass to handle the event
			return super.action(evt, arg);
		}
	}

	/**
	 * Load a document given a URL string
	 * 
	 * @param specStr A String containing the URL to load: i.e.
	 *                "http://www.rawthought.com/"
	 */
	public void loadURL(String specStr) {
		URL tempURL;// the URL to be loaded
		InputStream tempInputStream;// stream from which to read data
		int curByte; // used for copying from input stream to a file

		try {
			// create new URL using fDefaultURL as the base or default URL
			tempURL = new URL(fDefaultURL, specStr);

			if (fDebugOn) {
				System.out.println("protocol: " + tempURL.getProtocol());
				System.out.println("host: " + tempURL.getHost());
				System.out.println("port: " + tempURL.getPort());
				System.out.println("filename: " + tempURL.getFile());
			}

			try {
				if (fDebugOn) {
					System.out.println("Opening input stream...");
				}

				// open the connection , get InputStream from which to read content data
				tempInputStream = tempURL.openStream();

				// if we get to this point withoutan exception being thrown, then we've
				// connected to a valid Web server, requested a valid URL, and there's content
				// data waiting for us on the InputStream
				try {

					// use URL.hashCode() to generate a unique filename
					String newFilename = String.valueOf(tempURL.hashCode()) + ".htm";

					if (fDebugOn) {
						System.out.println("Opening output file: " + newFilename);
					}

					// open output file
					FileOutputStream outStream = new FileOutputStream(newFilename);

					if (fDebugOn) {
						System.out.println("Copying Data...");
					}

					try {
						while ((curByte = tempInputStream.read()) != -1) {
							// simple byte-for-byte copy... could be improved!
							outStream.write(curByte);
						}

						if (fDebugOn) {
							System.out.println("Done Downloading Content!");
						}

						// we're done writing to the local file, so close it
						outStream.close();

					} catch (IOException copyEx) {
						System.err.println("copyEx: " + copyEx);
					}
				} catch (Exception fileOpenEx) {
					System.err.println("fileOpenEx: " + fileOpenEx);
				}
			} catch (IOException retrieveEx) {
				System.err.println("retrieveEx: " + retrieveEx);
			}
		} catch (MalformedURLException murlEx) {
			System.err.println("new URL thre ex: " + murlEx);
		}
	} // loadURL
}// class URL_Loader
