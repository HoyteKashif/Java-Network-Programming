package book.networking_and_communications.shared;

import java.util.Date;
import java.util.StringTokenizer;

/**
 * Utility Class Used in the Examples (pages 305-308). Class that allows events
 * to be scheduled. Encapsulates all of the information necessary to schedule an
 * event to occur on a given date and time.
 */
public class ScheduledEvent {
	public boolean fDebugOn = true;

	// all times listed are in milliseconds
	public static long TIME_INTERVAL_MINUTE = 60 * 1000;
	public static long TIME_INTERVAL_HALF_HOUR = 30 * TIME_INTERVAL_MINUTE;
	public static long TIME_INTERVAL_HOUR = 60 * TIME_INTERVAL_MINUTE;
	public static long TIME_INTERVAL_DAY = 24 * TIME_INTERVAL_HOUR;
	public static long TIME_INTERVAL_WEEK = 7 * TIME_INTERVAL_DAY;

	protected long fStartTime; // when does this event first occur?
	protected long fEndTime; // when does this event last occur?
	protected long fTimeIncrement; // event interval (how often)

	/**
	 * Create a new ScheduledEvent based on a starting and ending date, and a time
	 * increment
	 */
	public ScheduledEvent(Date startTime, Date endTime, long timeIncrement) {
		fStartTime = startTime.getTime();
		fEndTime = endTime.getTime();
		fTimeIncrement = timeIncrement;

		if (fTimeIncrement <= 0) {
			fTimeIncrement = TIME_INTERVAL_DAY; // one day default
		}
	}

	/**
	 * Read in the flattened info and inflate self based on it. This is handy for
	 * debugging, and it also allows you to store a ScheduledEvent in a text file
	 */
	public ScheduledEvent(String srcStr) {
		try {
			StringTokenizer theTokenizer = new StringTokenizer(srcStr, "|\n");

			String tokStr = theTokenizer.nextToken();
			fStartTime = Date.parse(tokStr);

			tokStr = theTokenizer.nextToken();
			fEndTime = Date.parse(tokStr);

			tokStr = theTokenizer.nextToken();
			fTimeIncrement = Long.parseLong(tokStr);
		} catch (Exception instEx) {
			System.err.println("parsing srcStr threw: " + instEx);
		}
	}

	/**
	 * Dump a String representation of this object
	 */
	@Override
	public String toString() {
		String startDateStr = (new Date(fStartTime)).toString() + "|";
		String endDateStr = (new Date(fEndTime)).toString() + "|";
		String timeIntervalStr = Long.toString(fTimeIncrement) + "\n";
		String retVal = startDateStr + endDateStr + timeIntervalStr;

		return retVal;
	}

	/**
	 * Get the next occurrence of this event, in raw (long) date format
	 * 
	 * @return
	 */
	public long getNextOccurrenceRaw() {
		Date currentTime = new Date(); // slurps in the system clock time
		return getNextOccurrenceAfterRaw(currentTime.getTime());
	}

	/**
	 * Get the next occurrence of this event, after the given date, in raw (long)
	 * date format
	 * 
	 * @param afterTime
	 * @return
	 */
	public long getNextOccurrenceAfterRaw(long afterTime) {

		// we know that the max delay is fTimeIncrement, min is zero
		long testTime = fStartTime;

		// startin at fStartTime,
		// keep adding the interval time until
		// we find an event that occurs at or after afterTime
		while (testTime < afterTime) {
			testTime += fTimeIncrement;
		}

		if (fDebugOn) {
			System.out.println("testTime: " + testTime);
		}

		return testTime;
	}

	/**
	 * Get the next occurrence of this event, after the current time, in Date format
	 */
	public Date getNextOccurrence() {
		return (new Date(getNextOccurrenceRaw()));
	}

	public Date getNextOccurrenceAfter(Date givenTime) {
		long rawGivenTime = givenTime.getTime();
		if (fDebugOn) {
			System.out.println("rawGivenTime: " + rawGivenTime);
		}
		return (new Date(getNextOccurrenceAfterRaw(rawGivenTime)));
	}
}
