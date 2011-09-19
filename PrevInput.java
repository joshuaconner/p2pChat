package p2pChat;

//import java.io.BufferedReader;
import java.io.IOException;


public class PrevInput implements Runnable {
	//BufferedReader in;
	
	@Override
	public void run() {
	    try {
	    	String input;
	    	while (true)
	    	{
				if((input = Peer.prevIn.readLine()) != null)
				{
					
					if (input.startsWith(Peer.myIP))
					//if it's a chat I sent
					{ 
						System.out.println("(You) " + input);
					}
					/*else if (!input.substring(5).startsWith(Peer.myIP))
					//it's my chat-join or chat-leave message that's come back
					//around the loop, so ignore it
					{
						continue;
					}*/
					else
					//otherwise it's someone else's chat; add to queue
					{
						Peer.chatQueue.add(input);
					}
				}
	    	}
		} catch (IOException e) {
			if (Peer.debug)
				System.out.println(e.getClass().toString() + " caught in PrevInput.");
			if(!Peer.getQuit()) {
				while (Peer.getHold());
				run();
			}
		}
	}

}
