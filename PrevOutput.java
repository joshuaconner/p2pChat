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
		    Peer.prevOut.println(newPrev.getInetAddress().getHostAddress());
		    if (Peer.debug) System.out.println("IP " + 
		    		newPrev.getInetAddress().getHostAddress() + " sent");
		    //Thread.sleep(1000);
		    Peer.prevOut.close();
		    Peer.prevIn.close();
		    Peer.prev.close();
		    Peer.prev = newPrev;
		    Peer.prevIn = new BufferedReader(new InputStreamReader(
		    		Peer.prev.getInputStream()));
		    Peer.prevOut = new PrintStream(
		    		Peer.prev.getOutputStream(), true);
		    
		    /*
		    synchronized (Peer.prevMonitor) {
		    	Peer.prevMonitor.notify();
		    }
		    */
       // } catch (InterruptedException e) {
       // 	if(Peer.debug)
       //		System.out.println(e.getClass().toString() + " caught in PrevOutput");
		} catch (SocketException e) {
			if(Peer.debug)
        		System.out.println(e.getClass().toString() + " caught in PrevOutput");
		} catch (IOException e) {
			if(Peer.debug)
        		System.out.println(e.getClass().toString() + " caught in PrevOutput");
		}
    }
	
	public void run() {
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
