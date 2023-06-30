package com.marxmind.serial;

import java.io.InputStream;

import com.fazecast.jSerialComm.SerialPort;
import com.fazecast.jSerialComm.SerialPortDataListener;

public class SerialPortReader {
	
	public static String devicePortName = "USB Serial Device (COM5)";
    public static SerialPort arduinoPort;
    public static InputStream arduinoStream = null;
	public static int PACKET_SIZE_IN_BYTES = 15;
	
	public static void main(String[] args) {
		 
	}

	public static void runSerialReader() {
		int len = SerialPort.getCommPorts().length;
        SerialPort serialPorts[] = new SerialPort[len];
        serialPorts = SerialPort.getCommPorts();
        
        if(len>0) {
        for (int i = 0; i < len; i++) {

            String portName = serialPorts[i].getDescriptivePortName();
            System.out.println(serialPorts[i].getSystemPortName() + ": " + portName + ": " + i);

            if (portName.contains(devicePortName)) {
                arduinoPort = serialPorts[i];
                arduinoPort.openPort();
                System.out.println("connected to: " + portName + "[" + i + "]");
                break;
            }
        }

        PacketListener listener = new PacketListener();
        arduinoPort.addDataListener(listener);
		
        
        }else {
        	System.out.println("No serial port found...");
        }
	}
	
}
