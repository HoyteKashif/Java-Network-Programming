package book.networking_and_communications.ch4;

import java.net.URL;
import java.util.Date;

import book.networking_and_communications.shared.ScheduledEvent;

// Example 4.4 Placing WebDrones in a ThreadGroup
public class WebDroneGroup extends WebDrone {
	protected ThreadGroup fDroneGroup = null;

	/**
	 * stop all of the current URLDownloadSlaves...
	 */
	public void stopAllDownloads() {
		fDroneGroup.stop();// should kill all download slaves
	}

	/**
	 * pause all downloads
	 */
	public void pauseAllDownloads() {
		fDroneGroup.suspend();
	}

	/**
	 * resume all downloads
	 */
	public void resumeAllDownloads() {
		fDroneGroup.resume();
	}

	/**
	 * Build a WebDroneGroup
	 * 
	 * @param sourceList List of URLs to slurp in
	 * @param eventList  List of times at which to slurp in URLs
	 */
	public WebDroneGroup(URL[] sourceList, ScheduledEvent[] eventList) {
		super(sourceList, eventList);
		fDroneGroup = new ThreadGroup("DroneGruppe");
	}

	protected void checkForExpiredTimers() {
		long curTime = (new Date()).getTime();

		// run through the event list
		for (int evtIdx = 0; evtIdx < fEventList.length; evtIdx++) {
			ScheduledEvent curEvent = fEventList[evtIdx];
			long nextEventTime = curEvent.getNextOccurrenceAfterRaw(fLastCheckupTime);

			if (nextEventTime <= curTime) {
				// build a new URLDownloadSlave to handle the transaction
				URLDownloadSlave newSlave = new URLDownloadSlave(fSourcesList[evtIdx], "file" + evtIdx + ".html",
						fDroneGroup);
				// since the slave is ref'd by the group, it won't get gc'd
			}
		}
		fLastCheckupTime = curTime;
	}
}
