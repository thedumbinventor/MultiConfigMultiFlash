package com.example.multideviceconfiguration;

public interface usbDataReceieveListener<T> {
    default void onUsbReceived(T data){}
}
