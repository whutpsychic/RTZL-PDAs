import 'dart:typed_data';

import 'package:flutter/material.dart';
import 'package:camera/camera.dart';
import 'package:flutter/services.dart';
import "../../main.dart";

const int frequency = 1000; // ms

class CameraApp extends StatefulWidget {
  const CameraApp({Key? key}) : super(key: key);

  @override
  _CameraAppState createState() => _CameraAppState();
}

class _CameraAppState extends State<CameraApp> {
  late CameraController controller;
  static const scannerChannel = MethodChannel('rtzl.distinguish');
  // 已成功识别出文字
  bool success = false;
  // 图片采集样本
  Uint8List? src;

  //设置消息监听
  Future<void> nativeMessageListener() async {
    scannerChannel.setMethodCallHandler((resultCall) async {
      // 处理原生 Android iOS 发送过来的消息
      MethodCall call = resultCall;
      String method = call.method;
      List arguments = call.arguments;
      print(method);
      print(" ========================== ");
      print(arguments);
      print(" ========================== ");
    });
  }

  void _callIO(String funcName, [var arg]) {
    scannerChannel.invokeMethod(funcName, arg);
  }

  void loop() {
    Future.delayed(const Duration(milliseconds: frequency), () {
      controller.takePicture().then((XFile value) async {
        Uint8List _bytes = await value.readAsBytes();
        _callIO("operate2", _bytes);
        setState(() {
          src = _bytes;
        });
      });
    });
  }

  @override
  void initState() {
    super.initState();
    nativeMessageListener();
    _callIO("deviceInit");
    controller = CameraController(cameras[0], ResolutionPreset.max);
    controller.initialize().then((_) {
      if (!mounted) {
        return;
      }
      // 设置自动聚焦
      controller.setFocusMode(FocusMode.auto);
      // 开始监听扫描画面
      loop();
    });
  }

  @override
  void dispose() {
    print(" --------------========== hey dispose --------------========== ");
    controller.dispose();
    super.dispose();
  }

  @override
  Widget build(BuildContext context) {
    if (!controller.value.isInitialized) {
      return Container();
    }
    return Container(
      child: src != null ? Image.memory(src!) : CameraPreview(controller),
    );
  }
}
