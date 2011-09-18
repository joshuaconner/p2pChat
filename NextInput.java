package p2pChat;

import java.io.IOException;

public class NextInput implements Runnable {

	@Override
	public void run() {
		synchronized (Peer.monitor) {
			String housekeeping;
			try {
				while((housekeeping = Peer.nextIn.readLine()) != null)
				{   
					if (Peer.debug) System.out.println("Housekeeping received: " + housekeeping);
					if (housekeeping.startsWith("Hold"))
				    {
				    	Peer.hold = true;
				    }
				    else if (Peer.debug && housekeeping.equals("test"))
				    {
				    		System.out.println("Test housekeeping message received.");
				    } 
				    else
				    {
				    	if (Peer.debug) System.out.println("New IP received: " + housekeeping);
				    	Peer.reconnectQueue.add(housekeeping);	
				    }
				}
			} catch (IOException e) {
				if(!Peer.quit) 
				{
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
