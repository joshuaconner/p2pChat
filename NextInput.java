package p2pChat;

import java.io.IOException;

public class NextInput implements Runnable {
	
	boolean reconnectDone;

	@SuppressWarnings("unused")
	@Override
	public void run() {
		String housekeeping;
		try {
			while (!Peer.getQuit()) {
				if((housekeeping = Peer.nextIn.readLine()) != null)
				{   
					if (Peer.debug) System.out.println("Housekeeping received: " + housekeeping);
					if (housekeeping.toLowerCase().equals("hold"))
				    {
				    	Peer.setHold(true);
				    	reconnectDone = false;
				    	while(Peer.getHold() ) {
				    		try {
				    			String reconnectIP;
				    			if((reconnectIP = Peer.nextIn.readLine()) != null) {
				    				//if (!reconnectIP.equals("hold"))
				    					Peer.reconnectQueue.add(reconnectIP);
				    					reconnectDone = true;
				    			}
				    		} catch (IOException e) {
				    			//do nothing
				    		}
				    	}
				    }
				    else if (Peer.debug && housekeeping.equals("test"))
				    {
				    		System.out.println("Test housekeeping message received.");
				    } 
				}
			}
		} catch (IOException e) {
			if(Peer.debug) {
				System.out.println(e.getClass().toString() + " caught in NextInput.java");
				//e.printStackTrace();
			}
			if(!Peer.getQuit()) 
			{
				run();
			}
		}
	}

}
