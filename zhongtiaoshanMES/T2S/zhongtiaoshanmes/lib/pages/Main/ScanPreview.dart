// ignore_for_file: file_names
import 'dart:typed_data';
import 'package:flutter/material.dart';
import 'package:camera/camera.dart';
import 'package:flutter/services.dart';
import "../../main.dart";

const int frequency = 1000; // ms

class ScanPreview extends StatefulWidget {
  const ScanPreview({Key? key}) : super(key: key);

  @override
  _ScanPreviewState createState() => _ScanPreviewState();
}

class _ScanPreviewState extends State<ScanPreview> {
  late CameraController controller;
  static const scannerChannel = MethodChannel('rtzl.distinguish');
  // 图片采集样本
  Uint8List? src;

  //设置消息监听
  Future<void> nativeMessageListener() async {
    scannerChannel.setMethodCallHandler((resultCall) async {
      // 处理原生 Android iOS 发送过来的消息
      MethodCall call = resultCall;
      String method = call.method;
      var arguments = call.arguments;
      print(" ============ method ============== ");
      print(method);
      print(" ============ arguments ============== ");
      print(arguments);

      if (arguments is List && arguments.isNotEmpty) {
        // 结束摄像
        controller.dispose();
        // 退回并传输数据
        _onWillPop(arguments);
      } else {
        // loop();
      }
    });
  }

  void _callIO(String funcName, [var arg]) {
    scannerChannel.invokeMethod(funcName, arg);
  }

  void takePicture() {
    controller.takePicture().then((XFile value) async {
      Uint8List _bytes = await value.readAsBytes();
      _callIO("operate2", _bytes);
    });
  }

  void loop() {
    Future.delayed(const Duration(milliseconds: frequency), () {
      takePicture();
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
      setState(() {});
      // 设置自动聚焦
      controller.setFocusMode(FocusMode.auto);
      // 获取屏幕宽高
      double _w = MediaQuery.of(context).size.width;
      double _h = MediaQuery.of(context).size.height;
      _callIO("setWH", <double>[_w, _h]);

      // // 开始监听扫描画面
      // loop();
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
    double _w = MediaQuery.of(context).size.width;
    double _h = MediaQuery.of(context).size.height;
    if (!controller.value.isInitialized) {
      return Container();
    }
    return WillPopScope(
      child: SizedBox(
          width: _w,
          height: _h,
          child: Stack(
            children: [
              CameraPreview(controller),
              Positioned(
                bottom: 0,
                left: 0,
                child: Container(
                  width: _w,
                  height: 100,
                  alignment: Alignment.center,
                  child: GestureDetector(
                    onTap: takePicture,
                    child: Container(
                      width: 80,
                      height: 80,
                      decoration: const BoxDecoration(
                          color: Colors.white,
                          borderRadius: BorderRadius.all(Radius.circular(25))),
                    ),
                  ),
                ),
              )
            ],
          )),
      onWillPop: () async {
        return false;
      },
    );
  }

  _onWillPop(arguments) {
    Navigator.pop(context, arguments);
    return false;
  }
}
