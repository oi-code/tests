package jepamt;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.io.PrintWriter;
import java.net.Socket;

public class Six_3_client {

    private Socket socket;
    private BufferedReader in;
    private PrintWriter out;
    private BufferedReader key;
    private String outMsgm, inMsg;

    public static void main(String... args) {
	try {
	    Six_3_client s = new Six_3_client();
	    System.out.println("Client started");
	    s.init();	    
	} catch (Exception e) {
	    System.out.println("cliend exception in main");
	    e.printStackTrace();
	}
    }

    private void init() throws Exception {
	socket = new Socket("localhost", 9764);
	in = new BufferedReader(new InputStreamReader(socket.getInputStream()));
	out = new PrintWriter(new BufferedWriter(new OutputStreamWriter(socket.getOutputStream())), true);
	key = new BufferedReader(new InputStreamReader(System.in));
	Thread t=new Thread(() -> {
	    
	    while (!Thread.currentThread().isInterrupted()) {
		try {
		    inMsg = in.readLine();
		    System.out.printf("%s\n", inMsg);
		} catch (IOException e) {
		    System.out.println("cliend exception in init");
		    e.printStackTrace();
		}
	    }
	});
	t.setDaemon(true);
	t.start();
	while (true) {
	    outMsgm = key.readLine();
	    if (outMsgm.equals("stop")) {		
		break;
	    }
	    out.println(outMsgm);	    
	}
	t.interrupt();
	out.close();
	in.close();
	socket.close();
    }
}
