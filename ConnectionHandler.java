package p2pChat;

import java.io.IOException;
import java.net.Socket;
import java.net.SocketException;

public class ConnectionHandler implements Runnable {
	public void run() {
		while(!Peer.quit && !Peer.prevDone)
		{
			Socket s;
			try 
			{
				if ((s = Peer.ss.accept()) != null)
					Peer.socketQueue.add(s);
			} catch (SocketException e) {
				//do nothing. really! this will happen sometimes, and it's okay.
			} catch (IOException e) {
				// if there's an I/O related socket exception?
				System.out.println("Something unexpected went wrong. Please try" +
						"again.");
				System.exit(1);
			}
		}
	}
}
