package p2pChat;

import java.io.IOException;

public class NextInput implements Runnable {

	@Override
	public void run() {
		String housekeeping;
		try {
			while((housekeeping = Peer.nextIn.readLine()) != null)
			{   
				//if (Peer.debug) System.out.println("Housekeeping received: " + housekeeping);
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
			    	if (!housekeeping.equals("80085")) {
			    		if (Peer.debug) System.out.println("New IP received: " + housekeeping);
			    			Peer.reconnectQueue.add(housekeeping);
			    	}
			    }
			}
		} catch (IOException e) {
			if(!Peer.quit) 
			{
				try {
					wait(1000);
				} catch (IllegalMonitorStateException e1) {
					System.out.println("IllegalMontiorStateException");
				} catch (InterruptedException e1) {
					// shouldn't happen
					e1.printStackTrace();
				}
				run();
			}
		}
	}
}
