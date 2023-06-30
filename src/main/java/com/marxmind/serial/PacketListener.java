package com.marxmind.serial;

import java.io.UnsupportedEncodingException;
import java.util.logging.Level;

import javax.swing.JDialog;
import javax.swing.JOptionPane;

import com.fazecast.jSerialComm.SerialPort;
import com.fazecast.jSerialComm.SerialPortEvent;
import com.fazecast.jSerialComm.SerialPortPacketListener;
import com.marxmind.utils.DateUtils;
import com.marxmind.utils.TimeUtils;

public class PacketListener implements SerialPortPacketListener {
	  
	@Override
	   public int getListeningEvents() { 
		return SerialPort.LISTENING_EVENT_DATA_RECEIVED;  
	   }

	   @Override
	   public int getPacketSize() { 
		   return SerialPortReader.PACKET_SIZE_IN_BYTES; 
	   }

	   @Override
	   public void serialEvent(SerialPortEvent event)
	   {
		   
		   byte[] newData = event.getReceivedData();
	        String str = new String(newData).split("\n", 2)[0].replaceAll("\\s+", "");
	        int byteSize = 0;
	        //System.out.println("STR: " + str);
	        try {
	            byteSize = str.getBytes("UTF-8").length;
	            String val = new String(newData);
	            System.out.println("reading: " + val);
	            message();
	        } catch (UnsupportedEncodingException ex) {
	            System.out.println("Log: " + PacketListener.class.getName() + " LEVEL: " + Level.SEVERE);
	        }
	        if (byteSize == SerialPortReader.PACKET_SIZE_IN_BYTES) {
	            System.out.println("Received data: " + str);
	            
	        }
		   
		   
		   
		   
	   }
	   
	   private void message() {
		   String timeNow = TimeUtils.getTime24FormatPlain();
		   JOptionPane jop = new JOptionPane();
		   jop.setMessageType(JOptionPane.PLAIN_MESSAGE);
		   jop.setMessage("You have successfully record your time: " + timeNow);
		   JDialog dialog = jop.createDialog(null, "Time Record");

		   // Set a 10 second timer
		   new Thread(new Runnable() {
		       @Override
		       public void run() {
		           try {
		               Thread.sleep(10000);
		           } catch (Exception e) {
		           }
		           dialog.dispose();
		       }

		   }).start();

		   dialog.setVisible(true);
	   }
	   
	
}
