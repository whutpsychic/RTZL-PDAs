// ignore_for_file: file_names
import 'package:flutter/services.dart';

class ToastChannel {
  static const MethodChannel _fnChannel =
      MethodChannel('com.rtzl.zbc/action/toast'); // 方法通道名称

  //
  static Future<void> show(String content) async {
    _fnChannel.invokeMethod('toast', content);
  }
}
