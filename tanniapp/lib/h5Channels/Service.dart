// ignore_for_file: file_names, use_build_context_synchronously
import 'dart:io';
import 'package:flutter/material.dart';
import 'package:flutter/services.dart';
import 'package:webview_flutter/webview_flutter.dart';
import 'package:permission_handler/permission_handler.dart';

import '../appConfig.dart';
import '../service/main.dart';
import '../utils/main.dart';
import '../main.dart';
import '../App.dart';

// 创建 JavascriptChannel
// 预留的调用服务通道（直接发起动作）
class ServiceChannal {
  final BuildContext context;
  ServiceChannal(this.context);

  static String name = '${Utils.getFnName()}call';

  onMessageReceived(JavaScriptMessage msg) async {
    String mainInfo = msg.message;
    // print(" ======================= ");
    // print(mainInfo);
    // print(" ======================= ");
    // =================== 无参数调用 ===================
    // 后退
    if (mainInfo == "backup" || mainInfo == "done") {
      Navigator.of(context).pop();
    }
    // 打开 App 设置（权限专用）
    else if (mainInfo == "openAppSettings") {
      bool result = await openAppSettings();
      if (!result) {
        ModalTips.show(context, "警告", "无法从您的设备直接打开系统设置，请前往系统设置为应用设定权限。");
      }
    }
    // 去往服务器 IP 设置界面
    else if (mainInfo == "ipConfig") {
      appPageKey.currentState?.ipConfig();
    }
    // 获取当前设备的 safeHeight [top, bottom]
    else if (mainInfo == "getSafeHeight") {
      double top = MediaQuery.of(context).padding.top;
      double bottom = MediaQuery.of(context).padding.bottom;
      List<double> res = [top, bottom];
      Utils.runChannelJs(
          globalWebViewController, "getSafeHeightCallback($res)");
    }
    // 设置顶部条为深色风格
    else if (mainInfo == "setTopbarStyleToDark") {
      if (Platform.isAndroid) {
        SystemChrome.setSystemUIOverlayStyle(const SystemUiOverlayStyle(
          statusBarColor: Colors.black, // 设置为黑色
          systemNavigationBarColor: Colors.black, // 设置为黑色
        ));
      } else if (Platform.isIOS) {
        SystemChrome.setSystemUIOverlayStyle(SystemUiOverlayStyle.dark);
      }
    }
    // 设置顶部条为浅色风格
    else if (mainInfo == "setTopbarStyleToLight") {
      if (Platform.isAndroid) {
        SystemChrome.setSystemUIOverlayStyle(const SystemUiOverlayStyle(
          statusBarColor: Colors.transparent, // 设置为透明
          systemNavigationBarColor: Colors.transparent, // 设置为透明
        ));
      } else if (Platform.isIOS) {
        SystemChrome.setSystemUIOverlayStyle(SystemUiOverlayStyle.light);
      }
    }
    // 去拍照并返回照片（base64）
    else if (mainInfo == "takePhoto") {
      appPageKey.currentState?.takePhoto();
    }
    // 颤动(按键手触觉)
    else if (mainInfo == 'heavyImpact') {
      HapticFeedback.heavyImpact();
    }
    // 播放提示音
    else if (mainInfo == 'voice') {
    }
    // =================== 带参数调用 ===================
    else {
      List<String> infoArr = mainInfo.split(StaticConfig.argsSpliter);
      String fnKey = infoArr[0];
      // 短提示
      if (fnKey == "toast") {
        Toast.show(context, infoArr[1]);
      }
      // 模态提示
      else if (fnKey == "modalTips") {
        String? res = await ModalTips.show(context, infoArr[1], infoArr[2]);
        Utils.runChannelJs(
            globalWebViewController, "modalTipsCallback('$res')");
      }
      // 模态确认询问
      else if (fnKey == "modalConfirm") {
        String? res = await ModalConfirm.show(context, infoArr[1], infoArr[2]);
        Utils.runChannelJs(
            globalWebViewController, "modalConfirmCallback('$res')");
      }
      // 展示加载中
      else if (fnKey == "modalLoading") {
        ModalLoading.show(context, infoArr[1]);
      }
      // 展示模态进度条
      else if (fnKey == "modalProgress") {
        ModalProgress.show(context, infoArr[1]);
      }
      // 模态进度条值
      else if (fnKey == "modalProgressAdd") {
        ModalProgress.addstep(double.parse(infoArr[1]));
      }
      // 模态进度条值
      else if (fnKey == "modalProgressSet") {
        ModalProgress.setstep(double.parse(infoArr[1]));
      }
      // 拨打电话
      else if (fnKey == "phonecall") {
        PhoneCall.dial(context, infoArr[1]);
      }
      // 在浏览器打开某网址
      else if (fnKey == "launchInExplorer") {
        LaunchInExplorer.at(context, infoArr[1], false);
      }
      // 在 url_launcher 内嵌浏览器打开某网址
      else if (fnKey == "launchInnerExplorer") {
        LaunchInExplorer.at(context, infoArr[1], true);
      }
      // 写入本地缓存数据
      else if (fnKey == "recordLocal") {
        LocalStorage.setValue(infoArr[1], infoArr[2]);
      }
      // 读取本地缓存数据
      else if (fnKey == "readLocal") {
        String? result = await LocalStorage.getValue(infoArr[1]);
        Utils.runChannelJs(
            globalWebViewController, "readLocalCallback('$result')");
      }
    }
  }
}
