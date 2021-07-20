package com.lcserial.www;

import java.io.File;
import java.io.IOException;
import java.security.InvalidParameterException;

/**
 * Created by moxiaomo
 * on 2020/4/2
 */
public class SerialPortTool {

    public SerialPortFinder mSerialPortFinder = new SerialPortFinder ();
    private SerialPort mSerialPort = null;

    public SerialPortTool() {
    }

    public SerialPort getSerialPort(String device, int baudrate) throws SecurityException, IOException, InvalidParameterException, Exception {
        if (this.mSerialPort == null) {
            if (device.length() == 0 || baudrate == -1) {
                throw new InvalidParameterException();
            }

            this.mSerialPort = new SerialPort (new File (device), baudrate, 0);
        }

        return this.mSerialPort;
    }

    public void closeSerialPort() {
        if (this.mSerialPort != null) {
            this.mSerialPort.close();
            this.mSerialPort = null;
        }

    }
}
