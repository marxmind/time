package com.marxmind.serial;

import java.io.InputStream;

import com.fazecast.jSerialComm.*;
import com.fazecast.jSerialComm.SerialPortEvent;

public class SerialCommunication {
	
	private static final String SCANNER_PORT="USB Serial Device (COM5)";
	static SerialPort serialPort;
	
	public static void main(String[] args) {
		/*
		 * SerialPort comPort = SerialPort.getCommPort(SCANNER_PORT);
		 * comPort.openPort(); MessageListener listener = new MessageListener();
		 * comPort.addDataListener(listener); try { Thread.sleep(5000); } catch
		 * (Exception e) { e.printStackTrace(); } comPort.removeDataListener();
		 * comPort.closePort();
		 */
		
		//listen();
	}
	
	private final class MessageListener implements SerialPortMessageListener
	{
	   @Override
	   public int getListeningEvents() { return SerialPort.LISTENING_EVENT_DATA_RECEIVED; }

	   @Override
	   public byte[] getMessageDelimiter() { return new byte[] { (byte)0x0B, (byte)0x65 }; }

	   @Override
	   public boolean delimiterIndicatesEndOfMessage() { return true; }

	   @Override
	   public void serialEvent(SerialPortEvent event)
	   {
	      byte[] delimitedMessage = event.getReceivedData();
	      System.out.println("Received the following delimited message: " + delimitedMessage);
	   }
	}
	
	static class SerialPortReader implements SerialPortEventListener{

		@Override
		public void serialEvent(SerialPortEvent serialPortEvent) {
			SerialPort comPort = SerialPort.getCommPort(SCANNER_PORT);
			comPort.openPort();
			comPort.addDataListener(new SerialPortDataListener() {
			   @Override
			   public int getListeningEvents() { return SerialPort.LISTENING_EVENT_DATA_AVAILABLE; }
			   @Override
			   public void serialEvent(SerialPortEvent event)
			   {
			      if (event.getEventType() != SerialPort.LISTENING_EVENT_DATA_AVAILABLE)
			         return;
			      byte[] newData = new byte[comPort.bytesAvailable()];
			      int numRead = comPort.readBytes(newData, newData.length);
			      System.out.println("Read " + numRead + " bytes.");
			   }
			});
		}
		
	}
	
	
	public static void getAllPorts() {
		for(SerialPort p : SerialPort.getCommPorts()) {
			System.out.println(p.getDescriptivePortName());
		}
	}
	
	public static void listen() {
		SerialPort comPort = SerialPort.getCommPort(SCANNER_PORT);
		   comPort.openPort();
		   PacketListener listener = new PacketListener();
		   comPort.addDataListener(listener);
		   try { 
			   Thread.sleep(5000); 
			   
			   System.out.println("listenning..." + listener.getPacketSize());
			   System.out.println("listenning event..." + listener.getListeningEvents());
		   } catch (Exception e) { e.printStackTrace(); }
		   comPort.removeDataListener();
		   comPort.closePort();
	}
	
}
