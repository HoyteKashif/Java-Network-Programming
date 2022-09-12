package book.networking_and_communications.ch6;

import java.io.DataInputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.io.PipedInputStream;
import java.io.PipedOutputStream;
import java.io.PrintStream;
import java.net.Socket;
import java.util.Date;
import java.util.Random;
import java.util.StringTokenizer;

import book.networking_and_communications.shared.StdioShell;

public class ICBProtocolManager implements UserInputMonitor, RemoteInputMonitor {

	// display constants in pixels
	static final int MAX_DISPLAY_WIDTH = 740;
	static final int MAX_DISPLAY_HEIGHT = 500;

	// ICB connection defaults
	protected int fRemoteHostPort = 7326;
//	protected String fRemoteHostName = "icb.evolve.com"; // default server
	protected String fRemoteHostName = "127.0.0.1"; // default server

	// ICB Packet types
	static final char M_LOGIN = 'a'; // login packet
	static final char M_LOGINOK = 'a'; // login response packet
	static final char M_OPEN = 'b'; // open message to group
	static final char M_PERSONAL = 'c'; // personal message
	static final char M_STATUS = 'd'; // status update message
	static final char M_ERROR = 'e'; // error message
	static final char M_IMPORTANT = 'f'; // special important announcement
	static final char M_EXIT = 'g'; // tell other side to exit
	static final char M_COMMAND = 'h'; // send a command from user
	static final char M_CMDOUT = 'i'; // output from a command
	static final char M_PROTOVERS = 'j'; // protocol version information
	static final char M_BEEP = 'k'; // beeps
	static final char M_PING = 'l'; // ping packet
	static final char M_PONG = 'm'; // response for ping packet

	// ICB protocol info
	static final int ICB_PROTOCOL_LEVEL = 1; // ICB protocol level known
	static final char ICB_NULL = '\001'; // field divider used in packets

	static final int MAX_NICKLEN = 12; // chars in a username
	static final int MAX_PASSWDLEN = 12; // chars in a user password
	static final int MAX_GROUPLEN = 8; // chars in a group name
	static final int MAX_INPUTSTR = (250 - MAX_NICKLEN - MAX_NICKLEN - 6);
	static final int MAX_FIELDS = 20; // fields in a packet

	// display formatting constants
	static final String LBRACKET = "[=";
	static final String RBRACKET = "=]";
	static final String LANGLE = "<";
	static final String RANGLE = ">";

	// Default login information
	protected String fUserID = "testuser"; // default username (for username@hostname)
	protected String fNickName = "remus"; // default nickname
	protected String fGroup = "core"; // default group to log into
	protected String fPassword = "b0gu$"; // to authenticate this testuser

	// debugging
	public int fDebugLevel = 1;
	protected static final PrintStream fDebugOut = System.out;

	public boolean fConnected = false; // state flag

	// used to pull data from the server
	protected byte[] fRawInputPacket;

	protected Socket fRemoteHostSocket = null;
	protected DataInputStream fRemoteHostInputStream = null;
	protected OutputStream fRemoteHostOutputStream = null;
	protected StdioShell fStdioShell = null;// interface to the user

	// used for reading commands from the user
	protected DataInputStream fInternalCmdInputStream;

	// used for sending data to the user display
	protected PrintStream fInternalDisplayOutputStream;

	// used to read user input
	protected PipedInputStream fUserDisplayStream;

	// used to monitor input from the remote host
	protected ICBRemoteInputMonitor fRemoteInputMonitor = null;

	// used to monitor input from the user
	protected ICBUserInputMonitor fUserInputMonitor = null;

	// used for displaying time
	Date fCurrentDate;
	
	public static void main(String[] args) {
		new ICBProtocolManager();
	}

	public ICBProtocolManager() {
		// we add some random info to the nickname to make it less likely that novice's
		// nicknames will collide
		Random tempRand = new Random();

		fNickName += tempRand.nextLong();
		if (fNickName.length() > 12) {
			fNickName = fNickName.substring(0, 11); // truncate
		}
		tempRand = null;

		try {
			// allocate a pipe from protocol manager to UI the protocol manager can write to
			// screen with this
			PipedOutputStream displayOutputStream = new PipedOutputStream();
			fUserDisplayStream = new PipedInputStream(displayOutputStream);
			fInternalDisplayOutputStream = new PrintStream(displayOutputStream);
		} catch (IOException ioEx) {
			System.err.println("allocating streams failed: " + ioEx);
			return;
		}

		// now create the GUI
		fStdioShell = new StdioShell("WickedFastICB 1.0", fUserDisplayStream, MAX_DISPLAY_WIDTH, MAX_DISPLAY_HEIGHT);
		fInternalCmdInputStream = new DataInputStream(fStdioShell.getInputStream());

		fStdioShell.resize(MAX_DISPLAY_WIDTH, MAX_DISPLAY_HEIGHT);
		fStdioShell.show();

		// preallocate our input buffer
		// (so we're not reallocating it on every read)
		fRawInputPacket = new byte[258];

		// get our login time (today's Date)
		fCurrentDate = new Date();

		// try to open the connection
		try {
			openConnection();
		} catch (Exception openEx) {
			System.err.println("openConnection failed with ex: " + openEx);
			return;
		}

		// build and start the monitors...
		fRemoteInputMonitor = new ICBRemoteInputMonitor(this);
		fUserInputMonitor = new ICBUserInputMonitor(this);
		fUserInputMonitor.start();
		fRemoteInputMonitor.start();
	}

	protected void openConnection() throws Exception {
		// Connect a new socket to the ICB server...
		fRemoteHostSocket = new Socket(fRemoteHostName, fRemoteHostPort);

		// Attach streams to the Socket
		fRemoteHostInputStream = new DataInputStream(fRemoteHostSocket.getInputStream());
		fRemoteHostOutputStream = fRemoteHostSocket.getOutputStream();

		if (fDebugLevel > 0) {
			fDebugOut.println("openConnection OK!");
		}
	}

	protected void closeConnection() {
		fRemoteHostInputStream = null;
		fRemoteHostOutputStream = null;

		if (fRemoteHostSocket != null) {
			try {
				fRemoteHostSocket.close();
				if (fDebugLevel > 0) {
					fDebugOut.println("fRemoteHostSocket closed.");
				}
			} catch (IOException ioEx) {
				fDebugOut.println("socket.close() threw: " + ioEx);
			}
		}

		fRemoteHostSocket = null;
	}

	/**
	 * What follows are methods for dealing with data arriving from the remote host.
	 */

	/**
	 * Read input from the remote host this method will be called periodically by a
	 * driving thread
	 */
	public void readRemoteInput() {
		String[] packet;

		// read until we run out of incoming packets...
		while ((packet = readPacket()) != null) {
			handlePacket(packet);
		}
	}

	/**
	 * Gets a complete packet from the socket and splits it into multiple fields,
	 * delimited by SOH (start-of-header, or ^A). The packet type goes into
	 * packet[0], the rest of the packet in packet[1]...
	 * 
	 * @return String[] A packet split into multiple fields
	 */
	protected synchronized String[] readPacket() {
		int packetLength = 0;
		int i = 0;
		int numTokens = 0;
		int bytesRead = 0;

		fRawInputPacket[0] = 0; // clear the length?

		try {
			// read in packet length and packet type first
			bytesRead = fRemoteHostInputStream.read(fRawInputPacket, 0, 2);

			if (fDebugLevel > 1) {
				fDebugOut.println("bytesRead: " + bytesRead);
			}

			if (bytesRead <= 0) {
				return null;
			}

			packetLength = fRawInputPacket[0];
			if (packetLength < 0) {
				// overall data length > 255, streamed over several packets. adjust length to be
				// *just* for this packet
				packetLength = packetLength + 256;
			}

			if (fDebugLevel > 1) {
				fDebugOut.println("packetLength = " + packetLength);
			}

			// push the packet type into fRawInputPacket[0]
			fRawInputPacket[0] = fRawInputPacket[1];
			if (fDebugLevel > 1) {
				fDebugOut.println("packet type = '" + (char) fRawInputPacket[0] + "' (" + fRawInputPacket[0] + ")");
			}

			// stuff in a fake delimiter for StringTokenizer
			fRawInputPacket[1] = '\0';

			fRemoteHostInputStream.read(fRawInputPacket, 2, packetLength - 1);

			if (fDebugLevel > 2) {
				fDebugOut.println("rawPacket = '" + new String(fRawInputPacket, 0).substring(2, packetLength) + "'");
			}
		} catch (IOException e) {
			System.err.println("readPacket ex: " + e);
		}

		// Create a new StringTokenizer to break the raw packet into a series of
		// delimited field Strings
		StringTokenizer strtok = new StringTokenizer(new String(fRawInputPacket, 0, 0, packetLength), ICB_NULL + "\0");

		// slurp all of the field tokens into the packet array
		numTokens = strtok.countTokens();
		String[] fieldList = new String[numTokens];

		for (i = 0; i < numTokens; i++) {
			fieldList[i] = (String) strtok.nextElement();
		}

		return fieldList;
	}

	/**
	 * Process an incoming packet
	 * 
	 * @param packet
	 */
	public void handlePacket(String[] packet) {
		if (packet.length == 0) {
			if (fDebugLevel > 0) {
				System.err.println("handlePacket err: empty packet");
			}
		}

		// Process incoming packet
		switch (packet[0].charAt(0)) {
		case M_PING:
			handlePacket_Ping(packet);
			break;

		case M_BEEP:
			handlePacket_Beep(packet);
			break;

		case M_PROTOVERS:
			handlePacket_ProtoVersion(packet);
			break;

		case M_LOGINOK:
			handlePacket_LoginOkMsg(packet);
			break;

		case M_OPEN:
			handlePacket_OpenMsg(packet);
			break;

		case M_STATUS:
			handlePacket_StatusMsg(packet);
			break;

		case M_ERROR:
			handlePacket_ErrorMsg(packet);
			break;

		case M_IMPORTANT:
			handlePacket_ImportantMsg(packet);
			break;

		case M_EXIT:
			handlePacket_Exit(packet);
			break;

		case M_CMDOUT:
			handlePacket_CmdOutMsg(packet);
			break;

		case M_PERSONAL:
			handlePacket_PersonalMsg(packet);
			break;

		case ICB_PROTOCOL_LEVEL:
			// ignore
			break;

		default:
			fDebugOut.println("Unknown packet type: " + packet[0].charAt(0));
			return;
		}
	}

	/**
	 * handle an important message packet
	 * 
	 * @param packet The message packet received.
	 */
	public void handlePacket_ImportantMsg(String[] packet) {
		if (fDebugLevel > 1) {
			fDebugOut.println("handlePacket_ImportantMsg");
		}

		if (packet.length != 3) {
			fDebugOut.println("ImportantMsg length error");
		} else {
			display(LBRACKET + "*" + packet[1] + "*" + RBRACKET + " " + packet[2]);
		}
	}

	/**
	 * Process and open message sent to the entire current group. (M_OPEN)
	 * 
	 * @param packet The message packet received.
	 */
	public void handlePacket_OpenMsg(String[] packet) {
		if (fDebugLevel > 1) {
			fDebugOut.println("handlePacket_OpenMsg");
		}

		if (packet.length != 3) {
			fDebugOut.println("OpenMsg length error");
		} else {
			display(LANGLE + "*" + packet[1] + "*" + RANGLE + " " + packet[2]);
		}
	}

	/**
	 * Process a private message sent just to me. (M_PERSONAL)
	 * 
	 * @param packet The message packet received.
	 */
	public void handlePacket_PersonalMsg(String[] packet) {
		if (fDebugLevel > 1) {
			fDebugOut.println("handlePacket_PersonalMsg");
		}

		if (packet.length == 2) {
			display(LANGLE + "*" + packet[1] + "*" + RANGLE + " ");
		} else if (packet.length == 3) {
			display(LANGLE + "*" + packet[1] + "*" + RANGLE + " " + packet[2]);
		} else {
			fDebugOut.println("PersonalMsg length error");
		}
	}

	/**
	 * handle a BEEP packet
	 * 
	 * @param packet The message packet received.
	 */
	public void handlePacket_Beep(String[] packet) {
		if (fDebugLevel > 1) {
			fDebugOut.println("handlePacket_Beep()");
		}

		if (packet.length != 2) {
			fDebugOut.println("Beep length error");
			return;
		}

		// we can't beep easily, so we just tell the user
		display(LBRACKET + "Beep!" + RBRACKET + " " + packet[1] + " sent you a beep");
	}

	/**
	 * handle a command output (result) packet
	 * 
	 * @param packet The message packet received.
	 */
	public void handlePacket_CmdOutMsg(String[] packet) {
		if (fDebugLevel > 1) {
			fDebugOut.println("handlePacket_CmdOutMsg(" + packet.length + ") " + packet[1]);
		}

		if (packet.length < 2) {
			System.err.println("CmdOutMsg length err");
			return;
		}

		// Header for a group listing
		if (packet[1].equals("gh")) {
			return;
		}
		// Who group listing - is this ever sent?
		else if (packet[1].equals("wg")) {
			if (fDebugLevel > 0) {
				fDebugOut.println("wg command output received");
			}

			if (packet.length < 3) {
				fDebugOut.println("CmdOutMsg Who Group listing length err");
			} else if (packet.length == 3) {
				display("Group: " + packet[2]);
			} else {
				display("Group: " + packet[2] + " " + packet[3]);
			}

			return;
		}
		// Header for a who listing
		else if (packet[1].equals("wh")) {
			// see below to line up header and text
			display("  Nickname Idle Sign-On UserID");
			return;
		}
		// Who listing
		else if (packet[1].equals("wl")) {
			int idletime, idleh, idlem, idles;
			StringBuffer s = new StringBuffer(128);

			if (packet.length != 10) {
				fDebugOut.println("CmdMsgOut Who Listing length err");
				return;
			}

			// " Nickname Idle Sign-On UserID
			// " Munge 1:15:00 Jul19 08:46 vapid@rawthought.com (nr)"
			// 23 4 6 7 8 9
			s.append(" ");

			// prepend asterisk to group moderator's name
			if (packet[2].charAt(0) == 'm') {
				s.append('*');
			} else {
				s.append(' ');
			}

			// nickname
			// pad nickname out to 15 spaces
			int nickLen = packet[3].length();
			if (nickLen < 15) {
				for (int catCount = 0; catCount < (15 - nickLen); catCount++) {
					packet[3] += " "; // append a space
				}
			}

			s.append(packet[3]); // append the actual username

			// idle time
			idletime = Integer.parseInt(packet[4]);
			idleh = idletime / 3600;
			idlem = idletime / 60 % 60;
			idles = idletime % 60;
			if (idleh > 0) {
				s.append(idleh);
				s.append(':');
				if (idlem < 10) {
					s.append('0');
				}
			} else {
				s.append(" ");
				if (idlem < 10) {
					s.append(' ');
				}
			}

			s.append(idlem);
			s.append(":");
			if (idles < 10) {
				s.append('0');
			}

			s.append(idles);
			s.append(" ");

			// packet[5] = response time, obsolete

			// login time
			s.append(dateToICBTimeStr(new Date(Long.parseLong(packet[6]) * 1000L)));

			s.append(" ");

			s.append(packet[7]);
			s.append('@');
			s.append(packet[8]);
			s.append(" ");

			// (nr) flag for not beign registered
			s.append(packet[9]);

			display(s.toString());
		} else if (packet[1].equals("c")) {
			if (fDebugLevel > 0) {
				fDebugOut.println("c command output received");
			}

			display("% " + '/' + packet[2]);
			return;
		}
		// Generic command output
		else if (packet[1].equals("co")) {
			if (packet.length == 3) {
				// "Group: foo (pv1) Mod: (None) Topic: bar"
				// becomes "foo (pv1) Mod: (None) Topic: bar" and
				// user header is attached here instead of in "wh"
				if (packet[2].startsWith("Group: ")) {
					display(packet[2].substring(7));
					display(" Nickname idle Sign-On UserID");
				} else {
					display(packet[2]);
				}
			} else {
				display(""); // blank line
			}

			return;
		} else {
			fDebugOut.println("CmdOutMsg Unkown Command Type = " + packet[1]);
			return;
		}
	}

	/**
	 * We received an error message from the ICB server. (M_ERROR: error message)
	 * 
	 * @param packet The packet received.
	 */
	public void handlePacket_ErrorMsg(String[] packet) {
		if (fDebugLevel > 1) {
			fDebugOut.println("handlePacket_ErrorMsg()");
		}

		if (packet.length != 2) {
			fDebugOut.println("ErrorMsg length err");
			return;
		}

		display(LBRACKET + "Error" + RBRACKET + " " + packet[1]);

	}

	/**
	 * We received a packet from the ICB server telling us to exit (M_EXIT: exit
	 * message)
	 * 
	 * @param packet The packet received
	 */
	public void handlePacket_Exit(String[] packet) {
		if (fDebugLevel > 1) {
			fDebugOut.println("handlePacket_ExitMsg()");
		}

		if (packet.length != 1) {
			fDebugOut.println("Exit msg length err");
		} else {
			shutdown();
		}
	}

	/**
	 * The ICB server sent us a message letting us know we logged in correctly.
	 * (M_LOGINOK: login packet receipt)
	 * 
	 * @param packet The packet received.
	 */
	public void handlePacket_LoginOkMsg(String[] packet) {
		if (fDebugLevel > 1) {
			fDebugOut.println("handlePacket_LoginOkMsg()");
		}

		fConnected = true;
	}

	/**
	 * We received a ping message from the ICB server. (M_PING) Respond with a pong
	 * message.
	 * 
	 * @param packet The packet received.
	 */
	public void handlePacket_Ping(String[] packet) {
		if (fDebugLevel > 1) {
			fDebugOut.println("handlePacket_Ping()");
		}

		if (packet.length != 1) {
			fDebugOut.println("Ping msg length err");
		} else {
			sendPacket("" + M_PONG);
		}
	}

	/**
	 * Process a packet containing ICB protocol version info.
	 * 
	 * @param packet The packet received.
	 */
	public void handlePacket_ProtoVersion(String[] packet) {
		int proto_level = 0;

		if (fDebugLevel > 1) {
			fDebugOut.println("handlePacket_Proto()");
		}

		if (packet.length != 4) {
			fDebugOut.println("ProtoVersion msg length err");
			return;
		}

		try {
			proto_level = Integer.parseInt(packet[1]);
		} catch (NumberFormatException e) {
			fDebugOut.println("handlePacket_ProtoVersion ex: " + e);
		}

		if (proto_level != ICB_PROTOCOL_LEVEL) {
			fDebugOut.println("Bogus Protocol Level: " + proto_level);
		} else {
			sendLogin();
		}

		return;
	}

	/**
	 * Process a system status message sent by the server.
	 * 
	 * @param packet The packet received.
	 */
	public void handlePacket_StatusMsg(String[] packet) {
		if (fDebugLevel > 1) {
			fDebugOut.println("handlePacket_StatusMsg()");
		}

		if (packet.length != 3) {
			fDebugOut.println("StatusMsg length err");
		} else {
			// two status messages we filter for:
			// 3: M_STATUS:Drop:Your connection will be dropped in 4 minutes due to idle
			// timeout.
			// 3: M_STATUS:Drop:Your connection has been idled out.
			if (packet[1].equals("Drop")) {
				if (packet[2].equals("Your connection has been idled out.")) {
					shutdown();
				} else {
					// ping the server to reset the idle?
					sendServerCommand("ping", "server");
				}
			} else {
				display(LBRACKET + packet[1] + RBRACKET + " " + packet[2]);
			}
		}

		return;
	}

	/**
	 * What follows are methods for dealing with user input.
	 */

	/**
	 * Read some user input
	 */
	public void readUserInput() {
		String userCmdStr = null;
		try {
			userCmdStr = fInternalCmdInputStream.readLine();
		} catch (IOException ioEx) {
			System.err.println("readLine failed with: " + ioEx);
		}

		if (userCmdStr != null) {
			handleUserInput(userCmdStr);
		}
	}

	/**
	 * Handle input from the user.
	 * 
	 * @param inputStr
	 */
	public void handleUserInput(String inputStr) {
		if (inputStr.length() > MAX_INPUTSTR) {
			// input string is too long... truncate it!
			inputStr = inputStr.substring(0, (MAX_INPUTSTR - 1));
		}

		if (inputStr.startsWith("/")) {
			// user has issued a command -- handle it!
			sendCommand(inputStr);
		} else {
			// send input it the current group
			sendOpenMsg(inputStr);
		}
	}

	/**
	 * Process a user-supplied /command line and send it off.
	 * 
	 * @param text The command line to be processed.
	 */
	public void sendCommand(String text) {
		String command = "";
		String args = "";

		if (fDebugLevel > 1) {
			fDebugOut.println("sendCommand('" + text + "')");
		}

		if (fConnected) {
			int space = -1;

			// [0] = '/', [1..space-1] = command, [space+1..] = args
			space = text.indexOf(' ');
			if (space == -1) {
				command = text.substring(1).toLowerCase();
			} else {
				command = text.substring(1, space).toLowerCase();
				args = text.substring(space + 1);
			}

			if (fDebugLevel > 0) {
				fDebugOut.println("Sending command: '" + command + ":" + args + "'");
			}

			// Test for user command or send it on to the server
			handleUserCommand(command, args);
		} else {
			fDebugOut.println("send cmd err: No connection.");
		}
	}

	/**
	 * Deal with a command issued by the user. Tries to deal with commands locally
	 * first, then passes on to the server. Note that command is always lowercase,
	 * argsis mixed-case.
	 * 
	 * @param command A string which contains a command.
	 * @param args    Parameters for the command.
	 */
	public void handleUserCommand(String command, String args) {
		if (fDebugLevel > 1) {
			fDebugOut.println("handleUserCommand('" + command + "', '" + args + "')");
		}

		// we currently only support one local ICB command:
		// the "quit" command ("/q" or "/quit")
		// everything else is forwarded to the server...

		if (command.equals("q") || command.equals("quit")) {
			shutdown();
		} else {
			// pass it on to the server
			if (fDebugLevel > 1) {
				fDebugOut.println("An actual icb command!");
			}
			sendServerCommand(command, args);
		}
	}

	/**
	 * Send an ICB Packet out on the remote server output stream. ICB packets are
	 * comprised of:
	 * <ol>
	 * <li>A Single length byte.</li>
	 * <li>Length number of data bytes.</li>
	 * </ol>
	 * 
	 * @param dataStr A string to be sent.
	 */
	protected void sendPacket(String dataStr) {
		if (fDebugLevel > 1) {
			fDebugOut.println("icbSendPacket('" + dataStr + "')");
		}

		int dataLen = dataStr.length();
		// convert to array of bytes..add space for null and length bytes
		byte[] packetBytes = new byte[dataLen + 2];
		packetBytes[0] = (byte) (dataLen + 1); // the length byte
		dataStr.getBytes(0, dataLen, packetBytes, 1);
		packetBytes[dataLen + 1] = 0;// stuff in null
		try {
			fRemoteHostOutputStream.write(packetBytes);
			if (fDebugLevel > 1) {
				fDebugOut.println("wrote OK");
			}
		} catch (IOException ioEx) {
			System.err.println("sendPacket failed: " + ioEx);
			fConnected = false;
		}
	}

	/**
	 * Send an open message to the entire current group.
	 * 
	 * @param msg The message to send.
	 */
	public void sendOpenMsg(String msg) {
		if (fDebugLevel > 1) {
			fDebugOut.println("sendOpenMsg('" + msg + "')");
		}

		if (!fConnected) {
			fDebugOut.println("Error: No connection.");
		} else {
			sendPacket(M_OPEN + msg);
		}
	}

	/**
	 * Send a command directly to the ICB server
	 * 
	 * @param command A command to send.
	 * @param args
	 */
	public void sendServerCommand(String command, String args) {
		sendPacket(M_COMMAND + command + ICB_NULL + args);
	}

	public void sendLogin() {
		if (fDebugLevel > 1) {
			fDebugOut.println("Error: Already connected.");
		} else {
			sendPacket(M_LOGIN + fUserID + ICB_NULL + fNickName + ICB_NULL + fGroup + ICB_NULL + "login" + ICB_NULL
					+ fPassword);
		}
	}

	/**
	 * What follows is utility stuff for ICB
	 */

	// set up the month name table ahead of time

	static String[] fMonthNameTable = { "Jan", "Feb", "Mar", "Apr", "May", "Jun", "Jul", "Aug", "Sep", "Oct", "Nov",
			"Dec" };

	/**
	 * Generate an appropriate timestamp string given a Date.
	 * 
	 * @param when Date to get time for...
	 * @return String The given time in the form "Jan01 00:00".."Dec25 23:59"
	 */
	public String dateToICBTimeStr(Date when) {
		Date now = new Date();
		int date, hour, min;
		StringBuffer s = new StringBuffer(11);

		if (fDebugLevel > 1) {
			fDebugOut.println("dateToICBTimeStr(" + when + ")");
		}

		date = when.getDate();
		hour = when.getHours();
		min = when.getMinutes();

		// if date is same as fCurrentDate, don't display month/date
		if (fCurrentDate.getDate() == date) {
			s.append("		");
		} else {
			s.append(fMonthNameTable[when.getMonth()]);
			if (date < 10) {
				s.append('0');
			}
			s.append(date);
		}
		s.append('	');

		if (hour < 10) {
			s.append('0');
		}
		s.append(hour);

		s.append(':');

		if (min < 10) {
			s.append('0');
		}
		s.append(min);

		return s.toString();
	}

	/**
	 * Display a string to the user
	 * 
	 * @param msg
	 */
	public void display(String msg) {
		if (fInternalDisplayOutputStream != null) {
			fInternalDisplayOutputStream.println(msg);
		}
	}

	/**
	 * Close down and clean up
	 */
	public void shutdown() {
		if (fDebugLevel > 0) {
			fDebugOut.println("shudown...");
			closeConnection(); // try to shut down our connection...
			System.exit(0); // close down the app
		}
	}
}
