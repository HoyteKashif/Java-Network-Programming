package book.networking_and_communications.shared;

import java.awt.Event;
import java.awt.TextArea;

/**
 * A class that allows multiline input with carriage-return termination as well
 * as some convenient command keys.
 */
public class CmdTextArea extends TextArea {
	// the receiver for all the user's typing
	protected UserTypingTarget fTargetShell;

	/**
	 * @param targetShell The parent user interface.
	 * @param rows        The number of text rows to provide
	 * @param cols        The number of text columns to provide
	 */
	public CmdTextArea(UserTypingTarget targetShell, int rows, int cols) {
		super(rows, cols); // tell TextArea our size
		fTargetShell = targetShell;
	}

	/**
	 * This is where we do all of the interesting cmd-key filtering. Currently we
	 * suppose CR to terminate and clear the current input, Ctrl-U to erase the
	 * entire current input.
	 */
	public boolean handleEvent(Event evt) {
		// if the evt is a key press, filter it
		if (evt.id == Event.KEY_PRESS) {
			switch (evt.key) {
			case (10):// user hit return/enter

				// hand input to our target
				fTargetShell.acceptUserCommand(this.getText());
				// clear out the input field...
				this.setText("");
				break;

			case (21): // user hit Ctrl-U

				// clear out the input field...
				this.setText("");
				break;

			default:
				// we don't handle this particular key
				return super.handleEvent(evt);
			}
			return true;
		} else {
			return super.handleEvent(evt);
		}
	}
}
