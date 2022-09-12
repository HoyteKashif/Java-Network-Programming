package book.networking_and_communications.ch6;

public class ICBRemoteInputMonitor extends Thread {

	RemoteInputMonitor monitor;

	ICBRemoteInputMonitor(RemoteInputMonitor monitor) {
		this.monitor = monitor;
	}

	@Override
	public void run() {
		while (true) {
			
		}
	}
}
