/*
 * COMP9331 Lab2 - HTTP, TCP and UDP 
 * Q5
 * Author: Changfeng Li (z5137858)
 */


import java.io.*;
import java.net.*;
import java.util.*;

/*
 * Server to process ping requests over UDP. 
 * The server sits in an infinite loop listening for incoming UDP packets. 
 * When a packet comes in, the server simply sends the encapsulated data back to the client.
 */

public class PingClient
{
   private static final int SET_TIME_OUT = 1000; // ms

   public static void main(String[] args) throws Exception
   {
      // Get command line argument.
      if (args.length != 2)  {
         System.out.println("Required arguments: host port");
         return;
      }
      String host = args[0];
      InetAddress hostAddr = InetAddress.getByName(host);
      int clientPortNum = Integer.parseInt(args[1]);

      // This datagram socket is used for sending UDP packets
      // through the port specified on the command line.
      // If use StreamSocket ==> TCP!!!
      DatagramSocket socket = new DatagramSocket();

      // Requirement: The client should send 10 pings to the server
      // initialize the status var
      
      int respRecvN = 0;
      int respLostN = 0;
      long minRTT = 0;
      long maxRTT = 0;
      long avgRTT = 0;

      // loop\
      int j;
      for (j = 0; j < 10; j++) {

         Date date = new Date();
         long sendTime = date.getTime();

         // sending
         // prescribed format
         String pingInfo = "PING";
         String cntInfo = Integer.toString(j) + " ";
         String dateInfo = Long.toString(sendTime);
         String crlfInfo = "\r\n";
         String sendMsg =  pingInfo + cntInfo + dateInfo + crlfInfo;

         byte[] buff = sendMsg.getBytes();

         DatagramPacket pack = new DatagramPacket(buff, buff.length, hostAddr, clientPortNum);
         DatagramPacket resp = new DatagramPacket(new byte[1024], 1024);
         
         socket.send(pack);
         socket.setSoTimeout(SET_TIME_OUT);
         
         try {
            //Try to receive response from server
            socket.receive(resp);
            
            long recvTime = System.currentTimeMillis();
            long roundTripTime = recvTime - sendTime;
            
            System.out.println("ping to " + hostAddr + ", seq = " + Integer.toString(j+1) + ", rtt = " + Long.toString(roundTripTime));
            respRecvN++;

            if (j == 0) {
               minRTT = roundTripTime;
               maxRTT = roundTripTime;
            }
              
            // Compute minimum rtt and maximum rtt.
            if (maxRTT < roundTripTime) maxRTT = roundTripTime;
            else if (minRTT > roundTripTime) minRTT = roundTripTime;
            // Compute average rtt.
            avgRTT += roundTripTime;
            
         } catch(IOException el) {
            //System.out.println("Time_out reached!");
            System.out.println("ping to " + hostAddr + ", seq = " + Integer.toString(j+1) + ", time out!");               
            respLostN++;   
            
         }

      }
      
      if (respRecvN == 0) {
	     avgRTT = 0;
      }
      else if (respRecvN != 0) {
	     avgRTT = avgRTT / respRecvN;
      }

      System.out.println("\r1. => Statistics for the host " + hostAddr + ":");
      System.out.println("    Nb of Packets recv : " + Integer.toString(respRecvN));
      System.out.println("    Nb of Packets lost : " + Integer.toString(respLostN));
      System.out.println("\r2. => Statistics for Estimated round trip time(ms):");
      System.out.println("    min rtt : " + Long.toString(minRTT));
      System.out.println("    max rtt : " + Long.toString(maxRTT));
      System.out.println("    avg rtt : " + Long.toString(avgRTT));

   }
}
