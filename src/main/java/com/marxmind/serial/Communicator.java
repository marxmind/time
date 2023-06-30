package com.marxmind.serial;

import com.fazecast.jSerialComm.SerialPort;
import com.fazecast.jSerialComm.SerialPortDataListener;
import com.fazecast.jSerialComm.SerialPortEvent;

public class Communicator implements SerialPortDataListener{
	public static String devicePortName = "USB Serial Device (COM5)";
	public static void main(String[] args) {
	
		SerialPort comPort = SerialPort.getCommPort(devicePortName);
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

	@Override
	public int getListeningEvents() {
		// TODO Auto-generated method stub
		return 0;
	}

	@Override
	public void serialEvent(SerialPortEvent arg0) {
		// TODO Auto-generated method stub
		
	}
}

