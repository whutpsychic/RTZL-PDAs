package com.example.esc_printdemo.util;

import java.io.File;
import java.io.FileWriter;

public class PowerUtils {

    public static void powerOnOrOff(String id) {
        try {
            FileWriter localFileWriterOn = new FileWriter (new File ("/proc/gpiocontrol/set_sam"));
            localFileWriterOn.write(id);
            localFileWriterOn.close();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
