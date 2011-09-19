package p2pChat;

import java.io.*;
import java.net.*;
import java.util.concurrent.LinkedBlockingQueue;

public class Peer {
	public static final boolean debug = false;
	protected static boolean quit = false;
	protected static boolean prevDone = false;
	protected static boolean hold = false;
	protected static LinkedBlockingQueue<Socket> socketQueue = 
		new LinkedBlockingQueue<Socket>();
	protected static LinkedBlockingQueue<String> chatQueue = 
		new LinkedBlockingQueue<String>();
	protected static LinkedBlockingQueue<String> reconnectQueue = 
		new LinkedBlockingQueue<String>();
	protected static Socket prev;
	protected static BufferedReader prevIn;
	protected static PrintStream prevOut;
	protected static Socket next;
	protected static BufferedReader nextIn;
	protected static PrintStream nextOut;
	protected static String myIP;
	protected static int serverPort = 4242;
	protected static ServerSocket ss;
	protected static Thread nextInput;
	protected static Thread nextOutput;
	protected static Thread prevInput;
	protected static Thread prevOutput;
	protected static Thread connectionHandler;
	
	protected static synchronized void setHold(Boolean value) {
		hold = value; 
	}
	
	protected static synchronized boolean getHold() {
		return hold;
	}
	
	protected static synchronized void setQuit(Boolean value) {
		quit = value; 
	}
	
	protected static synchronized boolean getQuit() {
		return quit;
	}

	/**
	 * @param args
	 * @throws Exception
	 */
	@SuppressWarnings("unused")
	public static void main(String[] args) throws Exception {
		System.out.println("***********************************************");
		System.out.println("*            Welcome to P2P Chat!             *");
		System.out.println("*                                             *");
		System.out.println("* by Jacob Williams, Michael McCormick, Chad  *");
		System.out.println("*         Ellsworth and Joshua Conner         *");
		System.out.println("*                                             *");
		System.out.println("*       CS 499/565: Distributed Systems       *");
		System.out.println("*                 Fall 2011                   *");
		System.out.println("***********************************************\n");
		
		BufferedReader stdIn = new BufferedReader(new InputStreamReader(System.in));
		
    	try {
			myIP = InetAddress.getLocalHost().getHostAddress();
		} catch (UnknownHostException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		ss = new ServerSocket(serverPort);
		
		
		if (args.length  == 0) {
			System.out.print("Enter the IP address to connect to, or press " +
					"<Enter> to start a new chat node: ");
			String input = null; 
			if ((input = stdIn.readLine()) == null) 
			{
				next = new Socket(myIP, serverPort);
			}
			else
			{
				next = new Socket(input, serverPort);
			}
		} else if (args.length  == 1) {
			next = new Socket(InetAddress.getByName(args[0]), serverPort);
		} else {
			System.out.println("Usage: peer [ipAddress]");
			System.exit(0);
		}
		
		prev = ss.accept();
		nextIn = new BufferedReader(new InputStreamReader(next.getInputStream()));
		nextOut = new PrintStream(next.getOutputStream(), true);
		prevIn = new BufferedReader(new InputStreamReader(prev.getInputStream()));
		prevOut = new PrintStream(prev.getOutputStream(), true);
		
		
		nextInput = new Thread(new NextInput(), "NextIn");
		nextInput.start();
		nextOutput = new Thread(new NextOutput(), "NextOut");
		nextOutput.start();
		prevOutput = new Thread(new PrevOutput(), "PrevOut");
		prevOutput.start();
		connectionHandler = new Thread(new ConnectionHandler(), "ConnHand");
		connectionHandler.start();
		prevInput = new Thread(new PrevInput(), "PrevIn");
		prevInput.start();
		
		String userInput;
		try {

			mainloop:while (!getQuit()) {
				if ((userInput = stdIn.readLine()) != null) {
				    if (userInput.toLowerCase().equals("quit"))
				    {
					    	setQuit(true);
					    	break mainloop;
				    }
				    else if (Peer.debug && userInput.toLowerCase().equals("debug"))
				    {
				    		System.out.println("PREV: " + prev.toString());
				    		System.out.println("NEXT: " + next.toString());
				    		System.out.println("SocketQueue empty? " + socketQueue.isEmpty());
				    		System.out.println("ChatQueue empty? " + chatQueue.isEmpty());
				    		System.out.println("reconnectQueue empty? " + reconnectQueue.isEmpty());
				    		System.out.println("Hold = " + getHold() + ", prevDone = " + prevDone + ", quit = " + getQuit());
				    }
				    else if (Peer.debug && userInput.toLowerCase().startsWith("housekeeping"))
				    {
				    	if(Peer.debug)
				    	System.out.println("Sending housekeeping: " + userInput.substring(13));	
				    	Peer.prevOut.println(userInput.substring(13));
				    }
				    else
				    {
				        Peer.chatQueue.add(Peer.myIP + ": " + userInput);
				    }
				}
			}
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		//i.e. if you're not the only node left, do the following
		if(!next.getInetAddress().getHostAddress().equals(myIP) && 
				!prev.getInetAddress().getHostAddress().equals(myIP))
		{
			while(!socketQueue.isEmpty())
			{
			    Socket s = socketQueue.remove();
			    PrevOutput.sendReconnect(s);
			}
	    	prevOut.println("Hold");
		    
		    while(!chatQueue.isEmpty())
		    {
		    	nextOut.println(chatQueue.remove());
		    }
		    
		    prevOut.println(next.getInetAddress().getHostAddress());
		    //Thread.sleep(1000);
		}
		
	    prevOut.close();
	    prevIn.close();
	    prev.close();

	    nextOut.close();
	    nextIn.close();
	    next.close();  
	    
	    ss.close();
	}
}