package com.example.multideviceconfiguration;

import com.fazecast.jSerialComm.SerialPort;
import javafx.animation.KeyFrame;
import javafx.animation.Timeline;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.geometry.Pos;
import javafx.scene.control.Label;
import javafx.scene.control.Menu;
import javafx.scene.control.ProgressBar;
import javafx.scene.layout.Background;
import javafx.scene.layout.BackgroundFill;
import javafx.scene.paint.Color;
import javafx.scene.text.Text;
import javafx.util.Duration;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.math.BigInteger;
import java.net.URL;
import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.*;

public class MultiUI implements Initializable {
    @FXML
    private Menu Connect;

    @FXML
    private Label Device1;

    @FXML
    private Label Device2;

    @FXML
    private Label Device3;

    @FXML
    private Label Device4;

    @FXML
    private Label Device5;

    @FXML
    private Text device1Info;

    @FXML
    private Text device2Info;

    @FXML
    private Text device3Info;

    @FXML
    private Text device4Info;

    @FXML
    private Text device5Info;
    @FXML
    private ProgressBar updateBar;

    SerialPort[] portList;
    ArrayList<String> spandanList
            = new ArrayList<>();
    Timeline timeline;

    @FXML
    void StartConfiguration(ActionEvent event) {
        timeline = new Timeline(
                new KeyFrame(Duration.ZERO, e -> updateBar.setProgress(0.0)),
                new KeyFrame(Duration.seconds(1), e -> updateBar.setProgress(0.2)),
                new KeyFrame(Duration.seconds(2), e -> updateBar.setProgress(0.4)),
                new KeyFrame(Duration.seconds(3), e -> updateBar.setProgress(0.6)),
                new KeyFrame(Duration.seconds(4), e -> updateBar.setProgress(0.8)),
                new KeyFrame(Duration.seconds(5), e -> updateBar.setProgress(1.0))

        );


        timeline.play();
    }

    int deviceFlag1 = 0, deviceFlag2 = 0, deviceFlag3 = 0, deviceFlag4 = 0, deviceFlag5 = 0;

    @FXML
    void StopConfiguration(ActionEvent event) {
        if (timeline != null) {
            updateBar.setProgress(0.0); // Reset the progress bar
            timeline.stop();
        }
    }

    @FXML
    void ConnectDevices(ActionEvent event) throws InterruptedException, IOException, NoSuchAlgorithmException {
        BackgroundFill backgroundFill = new BackgroundFill(Color.GREEN, null, null);
        Background background = new Background(backgroundFill);
        Thread.sleep(2);
        try {
            if (!portList[0].isOpen()) {
                if (portList[0].openPort()) {
                    inputStreams[0] = portList[0].getInputStream();
                    outputStreams[0] = portList[0].getOutputStream();
                    Device1.setBackground(background);
                    Device1.setText("  Device 1");
                    Device1.setAlignment(Pos.CENTER);

                }
            }
            if (portList[1].openPort()) {
                inputStreams[1] = portList[1].getInputStream();
                outputStreams[1] = portList[1].getOutputStream();
                Device2.setBackground(background);
                Device2.setText("  Device 2");
                Device2.setAlignment(Pos.CENTER);

            }
            if (portList[2].openPort()) {
                inputStreams[2] = portList[2].getInputStream();
                outputStreams[2] = portList[2].getOutputStream();
                Device3.setBackground(background);
                Device3.setText("  Device 3");
                Device3.setAlignment(Pos.CENTER);

            }
            if (portList[3].openPort()) {
                inputStreams[3] = portList[3].getInputStream();
                outputStreams[3] = portList[3].getOutputStream();
                Device4.setBackground(background);
                Device4.setText("  Device 4");
                Device4.setAlignment(Pos.CENTER);

            }
            if (portList[4].openPort()) {
                inputStreams[4] = portList[4].getInputStream();
                outputStreams[4] = portList[4].getOutputStream();
                Device5.setBackground(background);
                Device5.setText("  Device 5");
                Device5.setAlignment(Pos.CENTER);

            }
        } catch (ArrayIndexOutOfBoundsException ignored) {

        }
        configureDevices();
    }

    @FXML
    void EjectDevices(ActionEvent event) throws IOException, InterruptedException {
        BackgroundFill backgroundFill = new BackgroundFill(Color.RED, null, null);
        Background background = new Background(backgroundFill);

        List<Label> devices = Arrays.asList(Device1, Device2, Device3, Device4, Device5);
        String[] texts = {"Device 1", "Device 2", "Device 3", "Device 4", "Device 5"};

        for (int i = 0; i < devices.size(); i++) {
            Label device = devices.get(i);
            device.setBackground(background);
            device.setText("  " + texts[i]);
            device.setAlignment(Pos.CENTER);
        }
        device1Info.setText("Waiting to be configured...");
        device2Info.setText("Waiting to be configured...");
        device3Info.setText("Waiting to be configured...");
        device4Info.setText("Waiting to be configured...");
        device5Info.setText("Waiting to be configured...");


        for (int i = 0; i < portList.length; i++) {
            if (spandanList.get(i) != null) {
                if (portList[i].isOpen()) {
                    outputStreams[i].write("\u001B".getBytes());
                    Thread.sleep(5);
                    portList[i].closePort();
                    System.out.println("Port " + (i) + " closed.");
                }
            }
        }
    }

    int click = 1;
    InputStream[] inputStreams;
    OutputStream[] outputStreams;
    int k = 0;

    @FXML
    void LoadDevices(ActionEvent event) throws InterruptedException {
        try {
            if (click > 1) {
                portList = null;
                spandanList = null;
                click = 0;
            }
            portList = SerialPort.getCommPorts();
            int DeviceCount = 0;
            for (SerialPort port : portList) {
                if (port.getPortDescription().toLowerCase().contains("spandan") || port.getPortDescription().toLowerCase().contains("sunfox")) {
                    spandanList.add(port.getSystemPortName());
                    DeviceCount++;
                }
            }

            int baudRate = 115200;
            int StartBIT = 8;
            int StopBIT = 1;
            int ParityBIT = 0;
            SerialPort[] serialPorts = new SerialPort[DeviceCount];
            inputStreams = new InputStream[DeviceCount];
            outputStreams = new OutputStream[DeviceCount];
            k = DeviceCount;

            for (int i = 0; i < DeviceCount; i++) {
                SerialPort serialPort = SerialPort.getCommPort(spandanList.get(i));
                serialPort.setComPortParameters(baudRate, StartBIT, StopBIT, ParityBIT);
                serialPort.setComPortTimeouts(SerialPort.TIMEOUT_READ_BLOCKING, 2000, 0);
                Thread.sleep(2);
                serialPorts[i] = serialPort;
                System.out.println(spandanList.get(i));
                inputStreams[i] = serialPort.getInputStream();
                outputStreams[i] = serialPort.getOutputStream();
            }
        } catch (NullPointerException ignored) {
        }
    }

    //    String deviceID=MainUiSpandanNeo.deviceID;
    void microControllerIDFetcher() throws IOException, InterruptedException {
        String GET_DID = "";
        HashMap<String, String> microcontrollerToDevice = new HashMap<>();
        for (int i = 0; i < spandanList.size(); i++) {
            Thread.sleep(10);
            if (portList[i].bytesAvailable() > 0) {
                byte[] arr2 = new byte[portList[i].bytesAvailable()];

            }
            String microcontrollerID = "";
            String deviceID = "";
            for (String s : microcontrollerToDevice.keySet()) {
                microcontrollerID = s;
                deviceID = microcontrollerToDevice.get(microcontrollerID);

            }
//            System.out.println("Microcontroller ID: " + microcontrollerID + " => Device ID: " + deviceID);


        }
    }

    String tempString = "";
    String mid = "";
    int confClick = 0;

    void configureDevices() throws IOException, InterruptedException, NoSuchAlgorithmException {
        microControllerIDFetcher();
        Thread deviceConfigureThread = new Thread(() -> {
            try {
                Thread.sleep(200); // Initial sleep for 2 seconds
            } catch (InterruptedException e) {
                throw new RuntimeException(e);
            }
            ArrayList<String> result = new ArrayList<>();
            for (int i = 0; i < k; i++) {
                try {

                    String did = "SET_DIDSPNE.DN01.23111699XX";
                    OutputStream outputStream1 = outputStreams[i];
                    InputStream inputStream1 = inputStreams[i];
                    outputStream1.write("ADMIN_SUNFOX".getBytes());
                    Thread.sleep(10);
                    outputStream1.write(did.getBytes());
                    Thread.sleep(10);
                    outputStream1.write("GET_DID".getBytes());
                    Thread.sleep(200);
                    outputStream1.write("GET_MID".getBytes());
                    Thread.sleep(200);
                    while (true) {
                        byte[] arr = new byte[portList[i].bytesAvailable()];
                        if (portList[i].bytesAvailable() > 0) {
                            inputStream1.read(arr);
                            mid = new String(arr).substring(34);
                            break;
                        }

                    }
                    outputStream1.write("GET_INF".getBytes());
                    Thread.sleep(200);
                    String generatedHashValue = generateHash(asciiToHex(did), (mid));
                    outputStream1.write(("SET_HAS" + generatedHashValue).getBytes());
                    Thread.sleep(200);
                    outputStream1.write(("GET_HAS" + generatedHashValue).getBytes());
                    Thread.sleep(100);

                    while (true) {
                        if (portList[i].bytesAvailable() > 0) {
                            try {
                                byte[] arr2 = new byte[portList[i].bytesAvailable()];
                                inputStream1.read(arr2);
                                tempString = new String(arr2);
                                System.out.println(tempString);
                                break;

                            } catch (Exception ignored) {
                            }
                        }
                    }
                } catch (IOException | InterruptedException | NoSuchAlgorithmException e) {
                    e.printStackTrace();
                }
                try {
                    Thread.sleep(10);
                } catch (InterruptedException e) {
                    throw new RuntimeException(e);
                }
            }
        });
        deviceConfigureThread.start();

        if (confClick > 0) {
            deviceConfigureThread.interrupt();
            confClick = 0;
        }


    }

    private static String asciiToHex(String asciiStr) {
        char[] chars = asciiStr.toCharArray();
        StringBuilder hex = new StringBuilder();
        for (char ch : chars) {
            hex.append(Integer.toHexString(ch));
        }
        return hex.toString();
    }

    private String generateHash(String deviceId, String microControllerId) throws NoSuchAlgorithmException {
        String hashInput = deviceId + microControllerId;
        MessageDigest md = MessageDigest.getInstance("SHA-256");
        byte[] hash = md.digest(hashInput.getBytes(StandardCharsets.UTF_8));
        BigInteger number = new BigInteger(1, hash);
        StringBuilder hexString = new StringBuilder(number.toString(16));

        while (hexString.length() < 64) {
            hexString.insert(0, '0');
        }
        return hexString.toString();
    }

    @Override
    public void initialize(URL location, ResourceBundle resources) {

    }

}



