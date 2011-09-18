package p2pChat;

import java.io.BufferedReader;
import java.io.IOException;


public class PrevInput implements Runnable {
	BufferedReader in;
	
	@Override
	public void run() {
		synchronized (Peer.monitor) {
		    	try {
		    		String input;
				while((input = Peer.prevIn.readLine()) != null)
				{
					if (!input.startsWith(Peer.myIP) || (!input.equals("80085")))
					{
						Peer.chatQueue.add(input);
					}
				}
			} catch (IOException e) {
				if(!Peer.quit) {
					try {
						wait();
					} catch (InterruptedException e1) {
						// shouldn't happen
						e1.printStackTrace();
					}
					run();
				}
			}
		}
	}

}
