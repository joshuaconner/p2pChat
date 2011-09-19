package p2pChat;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintStream;
import java.net.Socket;
import java.net.SocketException;

public class PrevOutput implements Runnable {
    
	/* sendReconnect(Socket new Prev)
	 * 
	 * Tells your current prev node to change their next (i.e. you) to newPrev,
	 * then closes their socket. Reassigns prev = newPrev, so the order will
	 * now be prev-->newPrev-->you.
	 */
	protected static void sendReconnect(Socket newPrev)
    {
        try {
        	//sends first of two messages
    		Peer.prevOut.println("Hold");
    		
    		//debug code
    		if (Peer.debug) System.out.println("Hold sent");	
		    if (Peer.debug) System.out.println("IP " + 
		    		newPrev.getInetAddress().getHostAddress() + " sent");
		    
		    //sends second of two messages
		    Peer.prevOut.println(newPrev.getInetAddress().getHostAddress());
		    
		    //close old prev, assign prev = newPrev, reopen streams
		    Peer.prevOut.close();
		    Peer.prevIn.close();
		    Peer.prev.close();
		    Peer.prev = newPrev;
		    Peer.prevIn = new BufferedReader(new InputStreamReader(
		    		Peer.prev.getInputStream()));
		    Peer.prevOut = new PrintStream(
		    		Peer.prev.getOutputStream(), true);

		//SocketExceptions and I/O exceptions are expected as you close and
		//reopen sockets, so we ignore.
		} catch (SocketException e) {
			if(Peer.debug)
        		System.out.println(e.getClass().toString() + 
        				" caught in PrevOutput");
		} catch (IOException e) {
			if(Peer.debug)
        		System.out.println(e.getClass().toString() + 
        				" caught in PrevOutput");
		}
    }
	
	public void run() {
		if (Peer.debug) 
			System.out.println("PREV: " + Peer.prev.toString());
		
		while (!Peer.prevDone)
		{	
			if(!Peer.socketQueue.isEmpty())
			{
				sendReconnect(Peer.socketQueue.remove());
			}
			
			if (Peer.getQuit())
			{
				Peer.prevDone = true;
			}
		}
	}
}
