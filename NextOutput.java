package p2pChat;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintStream;
import java.net.Socket;
import java.net.SocketException;

public class NextOutput implements Runnable {
	
	protected static void doReconnect(String IP)
	{
		if (Peer.debug)
			System.out.println("NextOutput attempting to connect to " + IP);
	    try {
		    Peer.nextOut.close();
		    Peer.nextIn.close();
		    Peer.next.close();
			
		    Peer.next = new Socket(IP, Peer.serverPort);
			Peer.nextIn = new BufferedReader(new InputStreamReader(Peer.next.getInputStream()));
			Peer.nextOut = new PrintStream(Peer.next.getOutputStream(), true);
			
			/*if (Peer.next.getInetAddress().getHostAddress().equals(IP))
			{
				Peer.reconnectQueue.remove();*/
			Peer.setHold(false);
				/*
				synchronized (Peer.nextMonitor) {
					Peer.nextMonitor.notify();
				}
				*/
			//}
	    } catch (SocketException e) {
			//do nothing. really!
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	
	public void run() {
		System.out.println("NEXT: " + Peer.next.toString());
	    while (!Peer.getQuit())
	    {
			if (!Peer.reconnectQueue.isEmpty()) {
				doReconnect(Peer.reconnectQueue.remove());
			}
			
			if (!Peer.chatQueue.isEmpty() && !Peer.hold)
			{
				String message = Peer.chatQueue.remove();
				if (!message.startsWith(Peer.myIP)) System.out.println(message);
			    Peer.nextOut.println(message);
			}
	    }
	}
}
