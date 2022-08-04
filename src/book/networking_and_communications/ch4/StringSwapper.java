package book.networking_and_communications.ch4;

// Example 4-6 Two Threads Use Wait and Notify to communicate
/**
 * An example of how wait/notify work
 */
public class StringSwapper extends Thread {
	public int fID = 0;
	protected String fPrivateString = null;
	protected StringSwapper fSwapTarget = null;

	public StringSwapper(String startString) {
		fPrivateString = startString;
	}

	public void setTarget(StringSwapper target) {
		fSwapTarget = target;
	}

	public synchronized void setString(String newString) {
		// set the string as soon as it's null
		while (fPrivateString != null) {
			try {
				wait();
			} catch (InterruptedException intEx) {
			}
		}
		fPrivateString = newString;
		notify();// let any monitors know we're finished setting
	}

	public synchronized String getString() {
		// get the string as soon as it's non-null
		while (fPrivateString == null) {
			try {
				wait();
			} catch (InterruptedException intEx) {
			}
		}

		String tempString = fPrivateString;
		fPrivateString = null;
		notify();// let any monitors know we're finished getting
		return tempString;
	}

	@Override
	public void run() {
		String holder;
		for (int count = 0; count < 100; count++) {
			holder = fSwapTarget.getString();
			System.out.println("object " + this.fID + " swaps \"" + this.fPrivateString + "\" for \"" + holder + "\"");
			this.setString(holder);
		}
	}

	static class StringSwapperTest {
		public static void main(String[] args) {
			StringSwapper a, b;
			a = new StringSwapper("cheezeball");
			b = new StringSwapper("cornpuff");
			a.fID = 1;
			b.fID = 2;
			a.setTarget(b);
			b.setTarget(a);
			a.start();
			b.start();
		}
	}
}
