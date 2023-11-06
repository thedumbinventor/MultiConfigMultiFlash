package com.example.multideviceconfiguration;

import com.fazecast.jSerialComm.SerialPort;

import java.io.InputStream;
import java.io.OutputStream;
import java.util.ArrayList;

public class SerialPortExample {
    public static void main(String[] args) {
        SerialPort[] portList = SerialPort.getCommPorts();
        ArrayList<String> spandanList
                = new ArrayList<>();
        int DeviceCount=0; // Set the desired DeviceCount
        for (SerialPort port : portList) {
            if (port.getPortDescription().toLowerCase().contains("spandan") || port.getPortDescription().toLowerCase().contains("sunfox")) {
                spandanList.add(port.getSystemPortName());
                DeviceCount++;
            }
        }

        int baudRate = 9600;
        int StartBIT = 8;
        int StopBIT = 1;
        int ParityBIT = 0;


        SerialPort[]  serialPorts = new SerialPort[DeviceCount];
        InputStream[] inputStreams = new InputStream[DeviceCount];
        OutputStream[] outputStreams = new OutputStream[DeviceCount];


        for (int i = 0; i < DeviceCount; i++) {
            SerialPort serialPort = SerialPort.getCommPort(spandanList.get(i)); // Replace with your actual port name
            serialPort.setComPortParameters(baudRate, StartBIT, StopBIT, ParityBIT);
            serialPort.setComPortTimeouts(SerialPort.TIMEOUT_READ_BLOCKING, 2000, 0);

            if (serialPort.openPort()) {
                serialPorts[i] = serialPort;
                inputStreams[i] = serialPort.getInputStream();
                outputStreams[i] = serialPort.getOutputStream();
                System.out.println("Port " + (i + 1) + " opened.");
            } else {
                System.err.println("Failed to open Port " + (i + 1));
            }
        }

        for (int i = 0; i < DeviceCount; i++) {
            if (serialPorts[i] != null) {
                serialPorts[i].closePort();
                System.out.println("Port " + (i + 1) + " closed.");
            }
        }
    }
}
