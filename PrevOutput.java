package p2pChat;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintStream;
import java.net.Socket;
import java.net.SocketException;

public class PrevOutput implements Runnable {
    protected static void sendReconnect(Socket newPrev)
    {
        try {
    			Peer.prevOut.println("Hold");
    			if (Peer.debug) System.out.println("Hold sent");
		    Thread.sleep(2000);
		    Peer.prevOut.println(newPrev.getInetAddress().getHostAddress());
		    if (Peer.debug) System.out.println("IP " + 
		    		newPrev.getInetAddress().getHostAddress() + " sent");
		    Peer.prevOut.close();
		    Peer.prevIn.close();
		    Peer.prev.close();
		    Peer.prev = newPrev;
		    Peer.prevIn = new BufferedReader(new InputStreamReader(
		    		Peer.prev.getInputStream()));
		    Peer.prevOut = new PrintStream(
		    		Peer.prev.getOutputStream(), true);
		    /*
		    synchronized (Peer.nextMonitor) {
		    		Peer.prevInput.notify();
		    }
		    */
        } catch (InterruptedException e) {
        		//shouldn't happen
		} catch (SocketException e) {
			//do nothing. really!
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
    }
	
	public void run() {
		System.out.println("PREV: " + Peer.prev.toString());
		while (!Peer.prevDone)
		{	
			Peer.prevOut.println("80085");
			try {
				wait(100);
			} catch (InterruptedException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			if(!Peer.socketQueue.isEmpty())
			{
				sendReconnect(Peer.socketQueue.remove());
			}
			
			if (Peer.quit)
			{
				Peer.prevDone = true;
			}
		}
	}
}
