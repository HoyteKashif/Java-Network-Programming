package book.networking_and_communications.shared;

import java.awt.BorderLayout;
import java.awt.Event;
import java.awt.Frame;
import java.io.IOException;
import java.io.InputStream;
import java.io.PipedInputStream;
import java.io.PipedOutputStream;
import java.io.PrintStream;

/**
 * A class for displaying a stdin and stdout. Useful when your platform doesn't
 * have stdin/stdout, or you want to launch multiple i/o windows.
 */
public class StdioShell extends Frame implements UserTypingTarget {

	private static final long serialVersionUID = 1L;
	public boolean fDebugOn = true; // toggles debugging
	public boolean fEchoOn = true; // toggles local echo of user input

	protected QuickTerm fDisplayArea;
	protected CmdTextArea fInputField;
	protected BorderLayout fUberLayoutMgr;

	protected PrintStream fOutputStream;
	protected PipedInputStream fResultStream;

	public StdioShell(String title, InputStream srcStream, int maxWidth, int maxHeight) {

		try {
			PipedOutputStream tempOutStream = new PipedOutputStream();
			fOutputStream = new PrintStream(tempOutStream);
			fResultStream = new PipedInputStream(tempOutStream);
		} catch (IOException ioEx) {
			System.err.println("StdioShell stream allocation failed: " + ioEx);
			return;
		}

		setTitle(title);

		fUberLayoutMgr = new BorderLayout(0, 0);
		this.setLayout(fUberLayoutMgr);

		fDisplayArea = new QuickTerm(24, 80, srcStream);
		add("North", fDisplayArea);

		fInputField = new CmdTextArea(this, 2, 80);
		add("South", fInputField);

	}

	/**
	 * 
	 * @param srcStream The Input Stream to use for display
	 */
	public void setDisplaySourceStream(InputStream srcStream) {
		fDisplayArea.setInputStream(srcStream);
	}

	/**
	 * Provide an InputStream that contains the user input...
	 * 
	 * @return
	 */
	public InputStream getInputStream() {
		return (InputStream) fResultStream;
	}

	/**
	 * This accepts a String from the user input field
	 * 
	 * @param cmdStr The user input String
	 */
	public void acceptUserCommand(String cmdStr) {
		if (fDebugOn) {
			System.out.println(cmdStr);
		}

		if (fOutputStream != null) {
			fOutputStream.println(cmdStr);
			if (fEchoOn) {
				fDisplayArea.append("--->" + cmdStr);
			}
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
}
