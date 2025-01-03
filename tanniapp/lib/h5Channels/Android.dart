// ignore_for_file: file_names, use_build_context_synchronously, no_leading_underscores_for_local_identifiers
import 'dart:io';
import 'package:flutter/material.dart';
import 'package:webview_flutter/webview_flutter.dart';

import '../appConfig.dart';
import '../platformChannels/main.dart';
import '../utils/main.dart';
import '../App.dart';

// 创建 JavascriptChannel
// 预留的 android 的服务调用通道
class AndroidChannel {
  final BuildContext context;
  AndroidChannel(this.context);

  static String name = '${Utils.getFnName()}android';

  onMessageReceived(JavaScriptMessage msg) async {
    if (Platform.isAndroid) {
      String mainInfo = msg.message;
      // print(" ======================= ");
      // print(mainInfo);
      // print(" ======================= ");
      // 无参数调用
      // 原生提示
      if (mainInfo == "battery") {
        String result = await BatteryChannel.getBatteryLevel();
        Utils.runChannelJs(
            globalWebViewController, "batteryInfoCallback($result)");
      }
      // 带参数调用
      else {
        List<String> infoArr = mainInfo.split(StaticConfig.argsSpliter);
        String _fnKey = infoArr[0];
        // 获取电量
        if (_fnKey == "toast") {
          ToastChannel.show(infoArr[1]);
        }
      }
    }
  }
}
