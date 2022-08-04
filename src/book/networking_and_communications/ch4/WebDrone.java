package book.networking_and_communications.ch4;

import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.net.URLConnection;
import java.util.Date;

import book.networking_and_communications.shared.ScheduledEvent;

// Example 4-3 Multithreaded WebDrone Downloads Several Web Pages Simultaneously
/**
 * A Class that takes a list of URLs and a list of ScheduledEvents and downloads
 * those URLs at those times. A new URLDownloadSlave is ceated every time one of
 * the ScheduledEvents is reached.
 */
public class WebDrone extends Thread {
	public URL[] fSourcesList; // list of sources to load
	protected ScheduledEvent[] fEventList; // list of times at which to load

	protected boolean fContinuesSlurp = true;
	protected long fLastCheckupTime = 0;

	/**
	 * Loop forever, downloading documents as appropriate
	 */
	public void run() {
		while (fContinuesSlurp) {
			checkForExpiredTimers();
			try {
				sleep(60000);
			} catch (InterruptedException intEx) {
			}
		}
	}

	/**
	 * Build a WebDrone
	 * 
	 * @param sourcesList List of URLs to slurp in
	 * @param eventList   List of times at which to slurp in the URLs
	 */
	public WebDrone(URL[] sourcesList, ScheduledEvent[] eventList) {
		fSourcesList = sourcesList;
		fEventList = eventList;
	}

	/**
	 * Run through the time list... see if anyting needs attention
	 */
	protected void checkForExpiredTimers() {
		long curTime = (new Date()).getTime();

		// run through the vent list
		for (int evtIdx = 0; evtIdx < fEventList.length; evtIdx++) {
			ScheduledEvent curEvent = fEventList[evtIdx];
			long nextEventTime = curEvent.getNextOccurrenceAfterRaw(fLastCheckupTime);

			if (nextEventTime <= curTime) {
				handleDocumentTransaction(fSourcesList[evtIdx], "file" + evtIdx + ".html");
			}
		}

		fLastCheckupTime = curTime;
	}

	/**
	 * Handle the web transaction
	 * 
	 * @param targetURL
	 * @param fileName
	 */
	public void handleDocumentTransaction(URL targetURL, String fileName) {
		URLDownloadSlave newSlave = new URLDownloadSlave(targetURL, fileName);
		// we should really keep a list of slaves so they don't get gc'd too soon
	}// handleDocumentTransaction

	static class URLDownloadSlave extends Thread {

		URL fTargetURL = null;
		String fTargetFilename = null;

		public URLDownloadSlave(URL theURL, String filename, ThreadGroup parentGroup) {
			// This does not work without the missing thread name, so
			// use the URL HashCode which is unique, just don't create multiple threads for
			// the same file
			super(parentGroup, String.valueOf(theURL.hashCode()));
			fTargetURL = theURL;
			fTargetFilename = filename;
			this.start();
		}

		public URLDownloadSlave(URL theURL, String filename) {
			fTargetURL = theURL;
			fTargetFilename = filename;
			this.start();
		}

		/**
		 * our asynchronous run method
		 */
		public void run() {
			loadURL(fTargetURL, fTargetFilename);
		}

		/**
		 * the main method which downloads the URL and stroes it in a file.
		 * 
		 * @param theURL      the URL to load
		 * @param outFileName The name of the file in which to store the content.
		 */
		public static void loadURL(URL theURL, String outFileName) {
			URLConnection theURLConnection;
			InputStream tempInputStream;
			int curByte;

			try {
				// first, get a URLConnection for the given URL object
				theURLConnection = theURL.openConnection();

				// open the connect , get data.
				// calls connect() implicitly
				tempInputStream = theURLConnection.getInputStream();

				try {
					// open output file
					FileOutputStream outstream = new FileOutputStream(outFileName);

					int curBytesRead = 0;
					byte[] copyBlock = new byte[1024];

					try {
						while ((curBytesRead = tempInputStream.read(null, 0, 1024)) != -1) {
							outstream.write(copyBlock, 0, curBytesRead);
						}
						outstream.close();// be nice
					} catch (IOException copyEx) {
						System.err.println("copyEx: " + copyEx);
					}
				} catch (Exception fileOpenEx) {
					System.err.println("fileOpenEx: " + fileOpenEx);
				}

			} catch (IOException retrieveEx) {
				System.err.println("retrieve ex: " + retrieveEx);
			}
		}
	}
}
