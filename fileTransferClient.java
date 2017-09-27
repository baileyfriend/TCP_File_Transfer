/*****************************************
Madison Brooks and Bailey Freund
CIS 457-20
Lab Project 1  - Client
******************************************/

import java.io.*;
import java.net.*;
import java.nio.*;
import java.nio.channels.*;
import java.nio.file.Paths;


class fileTransferClient{

      public fileTransferClient(){}

      /*
        Checks if valid ip
        @return True if string is a valid ip, else false
      */
      public static boolean isValidIP(String ip){
      	try{
      	    if(ip == null || ip.isEmpty()){
      		return false;
      	    }

      	    String[] ipArr = ip.split("\\.");
      	    if( ipArr.length != 4 ){
      		return false;
      	    }

      	    for(String numStr : ipArr){
      		int num = Integer.parseInt(numStr);
      		if(num < 0 || num > 255){
      		    return false;
      		}
      	    }

      	    if(ip.endsWith(".")){
      		return false;
      	    }

      	    return true;

      	} catch(NumberFormatException e){
      	    return false; //means it wasn't a number
      	}
      }

      public static void main(String args[]){
      	fileTransferClient ftc = new fileTransferClient();
      	try{

      	    //get input
      	    Console cons = System.console();


      	    String ipStr = "127.0.0.1"; // Default ip address
      	    boolean valid = false;
      	    while(valid == false){
          		ipStr = cons.readLine("Enter target IP address: ");
              valid = ftc.isValidIP(ipStr.trim());
          		if(!valid){
          		    System.out.println("IP address " + ipStr + " is not valid.");
          		    continue;
          		} else{
          		    valid = true;
          		}
      	    }


      	    String portStr = cons.readLine("Enter target port number: ");
	/*Validating the port number*/
                  Boolean portNotValid = false; //checking to see if the port is valid
		      int portInt=9876; //declaring portInt so the code works
		      if(portStr.matches("^[0-9]*$")){
		            portInt = Integer.parseInt(portStr);
		            if(portInt<1024 || 49151<portInt){
					portNotValid = true;
				}
		        }else{
		        	portNotValid = true;
		        }
			while(portNotValid){
				System.out.println("INVALID PORT NUMBER!");
				portStr = cons.readLine("Enter port number to listen on: ");
				if(portStr.matches("^[0-9]*$")){
		        	portInt = Integer.parseInt(portStr);
		        	if(1024<portInt && portInt<49151){
						portNotValid = false;
				}
		       	}else{
		        		portNotValid = true;
		        	}
			}
			System.out.println("Now using port number "+portInt); //end of checking port number
      while(true){
        
          String exitStr = cons.readLine("Would you like to transfer new file or exit? (type: newfile or exit): ");
          if(exitStr.trim().equals("exit") ){
              System.exit(0);
          } else if(!exitStr.equals("newfile") ){
              System.out.println("You typed invalid input: " + exitStr + " - continuing on to connect to server");
          }


          		InetSocketAddress insa = new InetSocketAddress(ipStr, portInt);
          		SocketChannel sc = SocketChannel.open();
          		sc.connect(insa);
          		//@TODO:while loop starts here (below is just rought 'outline' code)
		// Boolean exit = false;
		//while(false) ----- do the reading in of a file knowing when to stop reading bt having the size
          		String fname = cons.readLine("Enter file to transfer: ");
          		ByteBuffer buf = ByteBuffer.wrap(fname.getBytes()); // must send all messages as ByteBuffer
          		sc.write(buf); // write to the buffer the file name
          		//New Code
          		//TODO: This size reading is not working correctly with the server
          		//For the looping, Should we read in the size of all the files together then work with that as a whole, or one at a time?

          		// ByteBuffer sizebuffer = ByteBuffer.allocate(100); // should be 16?
          		// sc.read(sizebuffer); //reading the soccet channel
          		// long fileSizeLeastSig =  sizebuffer.getLong();
          		// long fileSizeMostSig =  sizebuffer.getLong();
          		// System.out.println(fileSizeLeastSig);
          		// System.out.println(fileSizeMostSig);

          		//loop here - loop until end of file size

              // String outfname = Paths.get("").toString() + fname;
              // System.out.println(outfname);
          		File newfile = new File(fname); //make an empty file with that file name
          		ByteBuffer buffer = ByteBuffer.allocate(100000);
          		int bytesRead = sc.read(buffer);
          		FileOutputStream outstream =new FileOutputStream(newfile);
          		FileChannel fc = outstream.getChannel();
          		while(bytesRead != -1){
          		    buffer.flip();
          		    fc.write(buffer);
          		    buffer.compact();
          		    bytesRead = sc.read(buffer);
          		}
          		String message2 = new String(buffer.array());
          		System.out.println(message2);


          		sc.read(buf); //reading the soccet channel for an error message or a good message
          		String message = new String(buf.array());
          		System.out.println(message);
		//should not close the soccet channel until we know the client doesn't want anymore files
          		sc.close();
            }
      	}catch(IOException e){
      	    System.out.println("Got an exception: " + e);
      	}
          }

  }
