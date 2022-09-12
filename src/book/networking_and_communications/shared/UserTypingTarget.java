package book.networking_and_communications.shared;

/**
 * An interface used to pass back user input
 */
public interface UserTypingTarget {
	public void acceptUserCommand(String cmdString);
}
