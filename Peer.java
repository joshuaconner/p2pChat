package p2pChat;

import java.io.*;
import java.net.*;
import java.util.concurrent.LinkedBlockingQueue;

public class Peer {
	public static final boolean debug = true;
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
    protected static Object monitor;
//    protected static Object prevMonitor;
	/*
    Peer()
    {
    	quit = false;
    	prevDone = false;
    	socketQueue = new LinkedBlockingQueue<Socket>();
    	chatQueue = new LinkedBlockingQueue<String>();
    	hold = false;
    	try {
			myIP = InetAddress.getLocalHost().getHostAddress();
		} catch (UnknownHostException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
    }
    */
    

	/**
	 * @param args
	 * @throws Exception
	 */
	public static void main(String[] args) throws Exception {
//		nextMonitor = new Object();
		monitor = new Object();
		
    		try {
			myIP = InetAddress.getLocalHost().getHostAddress();
		} catch (UnknownHostException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		ss = new ServerSocket(serverPort);

		if (args.length  == 0) {
			next = new Socket(myIP, serverPort);
		} else if (args.length  == 1) {
			next = new Socket(InetAddress.getByName(args[0]), serverPort);
		} else {
			System.out.println("Usage: peer [ip address]");
			System.exit(0);
		}
		
		prev = ss.accept();
		nextIn = new BufferedReader(new InputStreamReader(next.getInputStream()));
		nextOut = new PrintStream(next.getOutputStream(), true);
		prevIn = new BufferedReader(new InputStreamReader(prev.getInputStream()));
		prevOut = new PrintStream(prev.getOutputStream(), true);
		
		
		nextInput = new Thread(new NextInput());
		nextInput.start();
		nextOutput = new Thread(new NextOutput());
		nextOutput.start();
		prevOutput = new Thread(new PrevOutput());
		prevOutput.start();
		connectionHandler = new Thread(new ConnectionHandler());
		connectionHandler.start();
		prevInput = new Thread(new PrevInput());
		prevInput.start();
		
		BufferedReader stdIn = new BufferedReader(new InputStreamReader(System.in));
		String userInput;
		try {
			mainloop:while (!quit) {
				if ((userInput = stdIn.readLine()) != null) {
				    if (userInput.toLowerCase().equals("quit"))
				    {
					    	quit = true;
					    	break mainloop;
				    }
				    else if (Peer.debug && userInput.toLowerCase().equals("debug"))
				    {
				    		System.out.println("PREV: " + prev.toString());
				    		System.out.println("NEXT: " + next.toString());
				    		System.out.println("SocketQueue empty? " + socketQueue.isEmpty());
				    		System.out.println("ChatQueue empty? " + chatQueue.isEmpty());
				    		System.out.println("reconnectQueue empty? " + reconnectQueue.isEmpty());
				    		System.out.println("Hold = " + hold + ", prevDone = " + prevDone + ", quit = " + quit);
				    }
				    else if (Peer.debug && userInput.toLowerCase().equals("test"))
				    {
				    		Peer.prevOut.println("test");
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
		
		while(!socketQueue.isEmpty())
		{
		    Socket s = socketQueue.remove();
		    PrevOutput.sendReconnect(s);
		}
		
        try {
    		prevOut.println("Hold");
		    Thread.sleep(2000);
		} catch (InterruptedException e) {
		    // TODO Auto-generated catch block
		    e.printStackTrace();
		}
	    
	    while(!chatQueue.isEmpty())
	    {
	    	nextOut.println(chatQueue.remove());
	    }
	    
	    prevOut.println(next.getInetAddress().getHostAddress());

	    prevOut.close();
	    prevIn.close();
	    prev.close();

	    nextOut.close();
	    nextIn.close();
	    next.close();  
	    
	    ss.close();
	}
}