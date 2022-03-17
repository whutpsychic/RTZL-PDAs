import 'dart:convert';

import 'package:flutter/material.dart';
import 'package:webview_flutter/webview_flutter.dart';
import '../../core/dimensions.dart';
import '../../core/localStorage.dart';
import '../../config/Arguments.dart';
// import "./ImageGallery.dart";
import "./ScanPreview.dart";

class MainPage extends StatefulWidget {
  const MainPage({Key? key}) : super(key: key);

  @override
  State<StatefulWidget> createState() => MainPageState();
}

class MainPageState extends State<MainPage> {
  WebViewController? _webViewController;

  // 打开摄像头识别
  _onStartSpot() {
    // 跳转到相机识别页去
    Navigator.of(context)
        .push(MaterialPageRoute(builder: (context) => const ScanPreview()))
        .then((result) {
      // 之后
      // print("-=-=-=-=-=-=-=-=-=-=-=- result -=-=-=-=-=-=-=-=-=-=-=");
      // print(result);
      List<List> arr = [];
      for (int i = 0; i < result.length; i++) {
        List<dynamic> _result = [];
        result[i].forEach((v) {
          _result.add('"$v"');
        });
        arr.add(_result);
      }
      // print(arr);
      _webViewController!.runJavascript('window.rtzlPDAResult = ${arr}');
      _webViewController!.runJavascript('onReceive()');
    });
  }

  _ipconfig() {
    Navigator.of(context).pushNamed("/ip-config");
  }

  // 网页加载完成后
  _loadUp() {
    // 先识别默认加载网络地址
    LocalStorage.getValue('request_type').then((_requestType) {
      if (_requestType == "uri") {
        LocalStorage.getValue('uri').then(
          (result) {
            _webViewController?.loadUrl(result);
          },
        );
      } else {
        Future f1 = LocalStorage.getValue('http_type');
        Future f2 = LocalStorage.getValue('url');
        Future f3 = LocalStorage.getValue('port');
        Future.wait([f1, f2, f3]).then((valueArr) {
          String result =
              '${valueArr[0] ?? Arguments.defaultHttpType}://${valueArr[1] ?? Arguments.defaultProxyUrl}:${valueArr[2] ?? Arguments.defaultProxyPort}';
          _webViewController?.loadUrl(result);
        });
      }
    });
  }

  @override
  Widget build(BuildContext context) {
    double _screenWidth = Dimensions.getScreenWidth(context);
    double _screenHeight = Dimensions.getScreenHeight(context);

    return SizedBox(
      width: _screenWidth,
      height: _screenHeight,
      child: WebView(
        // initialUrl: "https://www.baidu.com",
        initialUrl: "about:blank",
        javascriptMode: JavascriptMode.unrestricted,
        javascriptChannels: <JavascriptChannel>{
          _tracebackJsChannel(context),
        },
        onWebViewCreated: (WebViewController webViewController) {
          _webViewController = webViewController;
          _loadUp();
        },
      ),
    );
  }

  // 创建 JavascriptChannel
  // 预留的 traceback 通道
  JavascriptChannel _tracebackJsChannel(BuildContext context) =>
      JavascriptChannel(
          name: 'traceback',
          onMessageReceived: (JavascriptMessage msg) {
            String mainInfo = msg.message;
            // print(" ======================= ");
            // print(mainInfo);
            // print(" ======================= ");
            if (mainInfo == "componentDidMount") {
              // _webViewController.evaluateJavascript('');
            }
            // 地址设定
            else if (mainInfo == "ipconfig") {
              _ipconfig();
            }
            // 开启摄像头
            else if (mainInfo == "startSpot") {
              _onStartSpot();
            }
          });
}
