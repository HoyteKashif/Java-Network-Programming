package book.networking_and_communications.shared;

import java.awt.Font;
import java.awt.TextArea;
import java.io.DataInputStream;
import java.io.InputStream;

/**
 * A class that displays text input in a scrolling view.
 */
public class QuickTerm extends TextArea implements Runnable {
	public boolean fDebugOn = false;

	// the maximum number of screens of characters to buffer
	public int fMaxNumScreens = 10;

	// the size of the scrollback buffer
	public int fMaxCharCount;

	// the number of characters currently buffered
	protected int fTotalCharCount = 0;

	// the number of columns of characters to display
	protected int fNumCols = 80;

	// the stream from which to read input data to be displayed
	protected DataInputStream fInputStream;

	// the thread that drives our Runnable interface
	protected Thread fTicklerThread;

	// flag that tells us to stop displaying
	protected boolean fContinueDisplay = true;

	/**
	 * 
	 * @param rows      The number of rows to display
	 * @param cols      The number of columns to display
	 * @param srcStream The InputStream from which to read the text data to be
	 *                  displayed
	 */
	public QuickTerm(int rows, int cols, InputStream srcStream) {
		super("Welcome!\n", rows, cols);

		fNumCols = cols;

		// set up a monospace font
		super.setFont(new Font("Courier", Font.PLAIN, 6));

		// calculate the size of our character buffer
		fMaxCharCount = rows * (cols * fMaxNumScreens);

		setInputStream(srcStream);

		// turn off editing so the user can't type over the display
		this.setEditable(false);

		// kickstart our thread
		fTicklerThread = new Thread(this);
		fTicklerThread.start();
	}

	/**
	 * Set the Input Stream for the terminal to read from
	 * 
	 * @param srcStream
	 */
	public void setInputStream(InputStream srcStream) {
		if (srcStream != null) {
			try {
				fInputStream = new DataInputStream(srcStream);
			} catch (Exception constructEx) {
				System.err.println("constructEx: " + constructEx);
				return;
			}
		} else {
			fInputStream = null;
		}
	}

	/**
	 * Take a String and blast it onto the display
	 * 
	 * @param str The String to append to the display
	 */
	@Override
	public void appendText(String str) {
		if (fDebugOn) {
			System.out.println("appendText: " + str);
		}

		String newStr = "";
		int newStrLen = str.length();

		// Are we appending more than one line?
		int numLines = (newStrLen / fNumCols) + (((newStrLen % fNumCols) > 0) ? 1 : 0);

		if (numLines > 1) {
			// perform necessary wrapping
			int offset = 0;
			String tempStr;
			for (int idx = 0; idx < numLines; idx++) {
				int newOffset = fNumCols * (idx + 1) - 1;
				if (newOffset > (newStrLen - 1)) {
					newOffset = (newStrLen - 1);
				}
				tempStr = str.substring(offset, newOffset);
				newStr += "\n" + tempStr;
				offset = newOffset;
			}
			newStrLen = newStr.length();
		} else {
			// if just one line, then prepend "\n" and display
			newStr = "\n" + str;
			newStrLen += 1;
		}

		// if we've exceeded our scrollback buffer size...
		if ((newStrLen + fTotalCharCount) > fMaxCharCount) {
			// remove as many characters from the top as we're about to append to the bottom
			super.replaceText("", 0, newStrLen);
			fTotalCharCount -= newStrLen;
		}

		// now use TextArea's method to place text in the display
		super.appendText(newStr);
		fTotalCharCount += newStrLen;
	}

	/**
	 * Method that continuously updates the screen display
	 */
	@Override
	public void run() {
		while (fContinueDisplay) {
			try {
				if (fInputStream != null) {
					String tmpStr = null;

					// read all available lines
					// this blocks waiting for a whole line
					while ((tmpStr = fInputStream.readLine()) != null) {
						this.appendText(tmpStr);
					}
				}

				// after reading all we can, give up time to other tasks
				try {
					fTicklerThread.sleep(50);
				} catch (InterruptedException iEx) {
					// do nothing! continue while loop
				}
			} catch (Exception runEx) {
				System.err.println("runEx: " + runEx);
				fContinueDisplay = false;
			}
		}
	}
}
