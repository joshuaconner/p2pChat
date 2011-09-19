package p2pChat;

//import java.io.BufferedReader;
import java.io.IOException;


public class PrevInput implements Runnable {
	
	@Override
	public void run() {
	    try {
	    	String input;
	    	String lastInput = null;
	    	
	    	//main execution loop
	    	while (true)
	    	{
				if((input = Peer.prevIn.readLine()) != null)
				{
					if (input.startsWith(Peer.myIP))
					//if it's a chat I sent
					{ 
						System.out.println("(You) " + input);
					}
					else if (input.substring(5).startsWith(Peer.myIP) || 
							(input.startsWith("  ") && input.equals(lastInput)))
					/* it's my chat-join or chat-leave message that's come back
					 * around the loop OR it's a chat-leave message repeat
					 * ..then ignore it!
					 */
					{
						continue;
					}
					else
					//otherwise it's someone else's chat; add to queue
					{
						Peer.chatQueue.add(input);
						lastInput = input;
					}
				}
	    	}
		} catch (IOException e) {
			//I/O exceptions are expected as we reassign sockets.
			//so we ignore them unless we are quitting.
			if (Peer.debug)
				System.out.println(e.getClass().toString() + 
						" caught in PrevInput.");
			if(!Peer.getQuit()) {
				//if we are not quitting, wait until we are not holding (i.e. 
				//reconnect is done) to try to restart thread.
				while (Peer.getHold());
				
				run();
			}
		}
	}

}
