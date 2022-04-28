import 'package:flutter/material.dart';
// import 'package:flutter/cupertino.dart';
import 'package:flutter_easyloading/flutter_easyloading.dart';

class Spin {
  static void setLoadingConfig() {
    EasyLoading.instance
      // 停留时长
      ..displayDuration = const Duration(milliseconds: 1000)
      // loading 样式
      ..indicatorType = EasyLoadingIndicatorType.circle
      // loading 风格
      ..loadingStyle = EasyLoadingStyle.custom
      // 尺寸
      ..indicatorSize = 45.0
      ..radius = 10.0
      // ======== 自定义样式
      ..progressColor = Colors.yellow.withOpacity(0.7)
      ..backgroundColor = Colors.black
      ..indicatorColor = Colors.white
      ..textColor = Colors.white
      ..maskColor = Colors.black.withOpacity(0.5)
      // ======== 自定义样式
      // loading 的时候是否允许操作
      ..userInteractions = false
      ..dismissOnTap = false;
  }

  // 外露的方法
  // 初始化
  static dynamic init() {
    return EasyLoading.init();
  }

  // 显示加载中
  static void show({String? text}) async {
    await EasyLoading.show(
      status: text ?? "loading...",
      maskType: EasyLoadingMaskType.black,
    );
  }

  // 结束加载
  static void dismiss() {
    EasyLoading.dismiss();
  }

  // --------------------------------------------
  // 短提示
  static void toast(String str) {
    EasyLoading.showToast(
      str,
      duration: Duration(milliseconds: 1500),
      toastPosition: EasyLoadingToastPosition.bottom,
    );
  }
}
