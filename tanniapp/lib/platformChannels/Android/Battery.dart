// ignore_for_file: file_names
import 'package:flutter/services.dart';

class BatteryChannel {
  static const MethodChannel _fnChannel =
      MethodChannel('com.rtzl.zbc/get/battery'); // 方法通道名称

  // 异步任务，通过平台通道与特定平台进行通信，获取电量，这里的宿主平台是 Android
  static Future<String> getBatteryLevel() async {
    String batteryLevel;
    try {
      final int result = await _fnChannel.invokeMethod('getBatteryLevel');
      batteryLevel = '$result';
    } on PlatformException catch (e) {
      print(e);
      batteryLevel = "unknown";
    }

    return batteryLevel;
  }
}
