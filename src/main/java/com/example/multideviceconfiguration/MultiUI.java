package com.example.multideviceconfiguration;

import com.fazecast.jSerialComm.SerialPort;
import javafx.animation.KeyFrame;
import javafx.animation.Timeline;
import javafx.application.Platform;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.geometry.Pos;
import javafx.scene.control.Label;
import javafx.scene.control.Menu;
import javafx.scene.control.MenuItem;
import javafx.scene.control.ProgressBar;
import javafx.scene.layout.Background;
import javafx.scene.layout.BackgroundFill;
import javafx.scene.paint.Color;
import javafx.scene.text.Text;
import javafx.util.Duration;

import java.io.*;
import java.math.BigInteger;
import java.net.URL;
import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.*;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class MultiUI implements Initializable {
    @FXML
    private Menu Connect;
    @FXML
    private MenuItem flashLegacy;

    @FXML
    private MenuItem flashNeo;

    @FXML
    private MenuItem flashPro;
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


    @FXML
    void StartConfiguration(ActionEvent event) {
        updateBar.setProgress(0.0);


    }

    int deviceFlag1 = 0, deviceFlag2 = 0, deviceFlag3 = 0, deviceFlag4 = 0, deviceFlag5 = 0;

    @FXML
    void StopConfiguration(ActionEvent event) {
            updateBar.setProgress(0.0); // Reset the progress bar
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
        } catch (Exception e) {

        }
        configureDevices();
    }

    @FXML
    void EjectDevices(ActionEvent event) throws IOException, InterruptedException {
        try {


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
                        try {
                            outputStreams[i].write("\u001B".getBytes());
                            Thread.sleep(5);
                            portList[i].closePort();
                            System.out.println("Port " + (i) + " closed.");
                        } catch (Exception e) {
                        }

                    }
                }
            }
        } catch (Exception e) {

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
    ///////////////////////////////////////////////////////////CONFIGURATION OF THE DEVICES//////////////////////////////////////////
    String tempString = "";
    String mid = "";
    int confClick = 0;
    List<String> upcomingConfigurationDevicesList = new ArrayList<>(List.of("SPPR-DN01-23111TEST1","SPPR.DN01.23111TEST2","SPPR.DN01.2311169971","SPPR.DN01.2311169972"));

    void configureDevices(){
        Thread deviceConfigureThread = new Thread(() -> {
            try {
                Thread.sleep(200);
            } catch (InterruptedException e) {
                throw new RuntimeException(e);
            }
            for (int i = 0; i < k; i++) {
                try {
                    String did = "SET_DID"+upcomingConfigurationDevicesList.get(i);
                    OutputStream outputStream1 = outputStreams[i];
                    InputStream inputStream1 = inputStreams[i];
                    outputStream1.write("ADMIN_SUNFOX".getBytes());
                    Thread.sleep(10);
                    outputStream1.write(did.getBytes());
                    int finalI = i;
                    Platform.runLater(()->{
                        device1Info.setText("DID----->" + upcomingConfigurationDevicesList.get(finalI)+"\n");
                    });
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
                    Platform.runLater(()->{
                        device1Info.setText("MID----->" + mid+"\n");
                    });
                    outputStream1.write("GET_INF".getBytes());
                    Thread.sleep(200);
                    String generatedHashValue = generateHash(asciiToHex(upcomingConfigurationDevicesList.get(i)), (mid));
                    outputStream1.write(("SET_HAS" + generatedHashValue).getBytes());
                    Thread.sleep(200);
                    Platform.runLater(()->{
                        device1Info.setText("HAS----->" + generatedHashValue+"\n");
                    });
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

    private static String generateHash(String deviceId, String microControllerId) throws NoSuchAlgorithmException {
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
//////////////////////////////////////////////////////////////FLASHING SCENARIO//////////////////////////////////////////////////////////
    String fname="";
    static String progress1="";
    static int p=0;

    @FXML
    void flashLegacy(ActionEvent event) {
        fname="LG";
            flash(fname,"flash");

    }

    @FXML
    void flashNeo(ActionEvent event) {
        fname="neo";
        flash(fname,"flash");
    }

    @FXML
    void flashPro(ActionEvent event) {
        fname="pro";
        flash(fname,"flash");
    }

    @FXML
    void memoryErase(ActionEvent event) {
        flash("LG","erase");

    }

    public  void flash(String fname, String operation ) {
        String stLinkCliPath = "STM32 ST-LINK Utility\\ST-LINK Utility\\ST-LINK_CLI.exe";
        String binaryFilePath="";
        if(fname.contains("neo")) binaryFilePath = "C:\\Users\\techn\\Downloads\\MultiDeviceConfiguration\\SpandanNeo Firmware\\Spandan_neo_v001.00.hex";
        if(fname.contains("LG")) binaryFilePath = "C:\\Users\\techn\\Downloads\\MultiDeviceConfiguration\\SpandanLegacy Firmware\\Spandan_V0.9.hex";
        if(fname.contains("pro")) binaryFilePath = "C:\\Users\\techn\\Downloads\\MultiDeviceConfiguration\\SpandanPro Firmware\\Spandan_Pro_Mux_V0.3.hex";

        String workingDirectory = "STM32 ST-LINK Utility\\ST-LINK Utility";
        // Flash command
        List<String> flashCommand = new ArrayList<>();
        flashCommand.add(stLinkCliPath);
        flashCommand.add("-P");
        flashCommand.add("\"" + binaryFilePath + "\"");
        flashCommand.add("0x08000000");
        flashCommand.add("-V");
        // Erase command
        List<String> eraseCommand = new ArrayList<>();
        eraseCommand.add(stLinkCliPath);
        eraseCommand.add("-ME");
        // Option Bytes command
        List<String> optionBytesCommand = new ArrayList<>();
        optionBytesCommand.add(stLinkCliPath);
        optionBytesCommand.add("-OB");
        optionBytesCommand.add("RDP=0");
        optionBytesCommand.add("USER=0x08000000");
        try {
            // Flash the binary
            if(operation.equalsIgnoreCase("flash"))
                executeCommand(flashCommand, workingDirectory);

            if(operation.equalsIgnoreCase("erase")){
                executeCommand(optionBytesCommand, workingDirectory);
                executeCommand(eraseCommand, workingDirectory);
            }

        } catch (IOException | InterruptedException e) {
            e.printStackTrace();
        }
    }
    private  void printProgress(String line) throws InterruptedException {
        String progressRegex = "\\b(\\d{1,3}%)\\b";
        Pattern pattern = Pattern.compile(progressRegex);
        Matcher matcher = pattern.matcher(line);

        while (matcher.find()) {
             progress1 = matcher.group(1);
             p=Integer.parseInt(progress1.substring(0,progress1.lastIndexOf('%')));
            System.out.println("Progress: " + progress1);
            Platform.runLater(()-> updateBar.setProgress(p));


        }
    }
    private  void executeCommand(List<String> command, String workingDirectory) throws IOException, InterruptedException {
        ProcessBuilder processBuilder = new ProcessBuilder(command);
        processBuilder.directory(new java.io.File(workingDirectory));

        Process process = processBuilder.start();

        try (BufferedReader reader = new BufferedReader(new InputStreamReader(process.getInputStream()))){
            String line;
            while ((line = reader.readLine()) != null) {
                printProgress(line);

            }
        }
        int exitCode = process.waitFor();
        if (exitCode == 0) {
            System.out.println("Command successful!");
        } else {
            System.out.println("Command failed. Exit code: " + exitCode);
        }
    }




}



