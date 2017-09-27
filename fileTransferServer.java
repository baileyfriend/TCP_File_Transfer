/*****************************************
Madison Brooks and Bailey Freund
CIS 457-20
Lab Project 1 - Server
******************************************/

import java.io.*;
import java.net.*;
import java.nio.*;
import java.nio.channels.*;
import java.io.FileOutputStream;
import java.io.File;


class fileTransferServer{
    public fileTransferServer(){}

    public static void main(String args[]){
      fileTransferServer fts = new fileTransferServer();
	     try{
            Console cons = System.console();
            String portStr = cons.readLine("Enter port number to listen on: ");
            int portInt = Integer.parseInt(portStr);
	    if(1024<portInt && portInt<49151){ //error checking to make sure port number is valid
		//@TODO: make it so the user has an opportunity to choose a new port number with while loop
            	ServerSocketChannel c = ServerSocketChannel.open();	//Open ServerSocketChannel
            	c.bind(new InetSocketAddress(portInt));
            	while(true){

		    SocketChannel sc = c.accept(); // get new channel for each new channel that connects to our server
		    //TODO: there is a problem with the line below but it is the same code that was used in lab???
		    TcpServerThread t = fts.new TcpServerThread(sc);
		    //Thread t = new Thread(sc);
		    t.start();
		    /*All code moved into the server thread class*/
		}
	    }else{
		System.out.println("Port number is invalid.");
	    }
	}catch(IOException e){
            System.out.println("Got an IO exception");
        }
    }



    class TcpServerThread extends Thread{
	SocketChannel sc;
	TcpServerThread(SocketChannel channel){
	    sc = channel;
	}
	public void run(){ //acts as the main method for the new thread
	    try{
		System.out.println("A client has connected");
		ByteBuffer buffer = ByteBuffer.allocate(4096);
		//TODO: read more than one name of a file
		//will need loops on a bunch of things like sending the size and contents of a file
		sc.read(buffer);
		String fileStr = new String(buffer.array());
		fileStr = fileStr.trim(); // have to trim leading and trailing spaces due to making string from oversized buffer
		File f = new File(fileStr);
		String filename = f.getName();
		boolean fileExists = f.exists();
		if(fileExists){
		    System.out.println("File exists");
		    //NEW CODE
		    //Send the file size so the user knows when to stop reading
		    Long fileSize = f.length();
		    ByteBuffer sizeB = ByteBuffer.allocate(100);
		    sc.write(sizeB);


		    //ByteBuffer nameB = ByteBuffer.wrap(fileStr.getBytes());// not used?

		    FileInputStream instream = new FileInputStream(f);
		    FileChannel fc = instream.getChannel();
		    ByteBuffer buf = ByteBuffer.allocate(100000);
		    int bytesread = fc.read(buf);

		    while(bytesread != -1){
			buf.flip();
			sc.write(buf);
			buf.compact();
			bytesread = fc.read(buf);
		    }
		} else{	//If the file doesn't exist, send a message to the client
		    String errormessage	= "Failed: Filename was invalid";
		    ByteBuffer errbuf =	ByteBuffer.wrap(errormessage.getBytes());
		    sc.write(errbuf);
		}
		sc.close();

	    }catch(IOException e){
		System.out.println("Got an Exception!");
	    }

	}
    }
}
