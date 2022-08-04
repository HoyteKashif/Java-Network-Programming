package book.networking_and_communications.ch3;

import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.net.URLConnection;
import java.util.Date;

import book.networking_and_communications.shared.ScheduledEvent;

/**
 * A class that takes a list of URLs and a list of ScheduledEvents and downloads
 * those URLs at those times
 */
public class SimpleWebDrone {

	// list of URLs to load
	protected URL[] fSourcesList;

	// list of times a which to load the URLs
	protected ScheduledEvent[] fEventList;

	protected boolean fContinueSlurp = true;
	protected long fLastCheckupTime = 0;

	/**
	 * Loop forever, downloading documents as appropriate
	 */
	public void run() {
		while (fContinueSlurp) {
			// check scheduled transactions
			checkForExpiredTimers();

			// sleep for a while
			try {
				// park here for 60 seconds...
				wait(60000);
			} catch (InterruptedException intEx) {
			}
		}
	}

	/**
	 * Build a SimpleWebDrone
	 * 
	 * @param sourceList A list of URLs to slurp in
	 * @param eventList  A list of times at which to slurp in the URLs
	 */
	public SimpleWebDrone(URL[] sourceList, ScheduledEvent[] eventList) {
		fSourcesList = sourceList;
		fEventList = eventList;
	}

	/**
	 * Run through the timer list... see if anything needs attention. This method
	 * iterates through the list of scheduled events, checks for any transactions
	 * that need to be executed, and launches a new URL handler to execute the
	 * transaction.
	 */
	public void checkForExpiredTimers() {

		long curTime = (new Date()).getTime();

		// run through the event list
		for (int evtIdx = 0; evtIdx < fEventList.length; evtIdx++) {
			ScheduledEvent curEvent = fEventList[evtIdx];
			long nextEventTime = curEvent.getNextOccurrenceAfterRaw(fLastCheckupTime);
			if (nextEventTime <= curTime) {
				// handle the transaction
				loadURL(fSourcesList[evtIdx], "file" + evtIdx + ".html");
			}
		}

		fLastCheckupTime = curTime;
	}

	/**
	 * The method that downloads the URL and stores it in a file.
	 * 
	 * @param theURL      The URL to load.
	 * @param outFileName The name of the file in which to store the content.
	 */
	public void loadURL(URL theURL, String outFileName) {

		URLConnection theURLConnection;

		InputStream tempInputStream;
		int curByte;

		try {

			// first, get a hold of a URLConnection for the given URL object
			theURLConnection = theURL.openConnection();

			// open the connection, get data.
			// calls connect() implicitly
			tempInputStream = theURLConnection.getInputStream();

			try {
				// open output file
				FileOutputStream outstream = new FileOutputStream(outFileName);

				int curBytesRead = 0;
				byte[] copyBlock = new byte[1024]; // for copying data

				try {
					while ((curBytesRead = tempInputStream.read(copyBlock, 0, 1024)) != -1) {
						outstream.write(copyBlock, 0, curBytesRead);
					}
					outstream.close();
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
