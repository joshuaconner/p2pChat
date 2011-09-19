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
					if (!input.startsWith(Peer.myIP))
					{
						Peer.chatQueue.add(input);
					}
					else
					{
						System.out.println("(You) " + input);
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
