module com.example.multideviceconfiguration {
    requires javafx.controls;
    requires javafx.fxml;
    requires com.fazecast.jSerialComm;


    opens com.example.multideviceconfiguration to javafx.fxml;
    exports com.example.multideviceconfiguration;
}