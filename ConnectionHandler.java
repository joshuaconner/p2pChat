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
				//do nothing. really!
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
	}
}
