import 'package:flutter/material.dart';
import 'package:webview_flutter/webview_flutter.dart';
import '../../core/dimensions.dart';
import '../../core/localStorage.dart';
import '../../config/Arguments.dart';

const String filePath = 'assets/h5/chart/index.html';

class MainPage extends StatefulWidget {
  @override
  State<StatefulWidget> createState() => MainPageState();
}

class MainPageState extends State<MainPage> {
  WebViewController _webViewController;

  _loadUp() {
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

    return Container(
      width: _screenWidth,
      height: _screenHeight,
      child: WebView(
        initialUrl: "https://www.baidu.com",
        javascriptMode: JavascriptMode.unrestricted,
        javascriptChannels: <JavascriptChannel>[
          _tracebackJsChannel(context),
        ].toSet(),
        onWebViewCreated: (WebViewController webViewController) {
          _webViewController = webViewController;
          _loadUp();
        },
      ),
    );
  }

  // 创建 JavascriptChannel
  JavascriptChannel _tracebackJsChannel(BuildContext context) =>
      JavascriptChannel(
          name: 'traceback',
          onMessageReceived: (JavascriptMessage msg) {
            // print("get message from JS, message is: ${msg.message}");
            String mainInfo = msg.message;
            if (mainInfo == "componentDidMount") {
              // _webViewController.evaluateJavascript('');
            } else if (mainInfo == "onTestClick") {
              print("hey!hey!hey!hey!hey!hey!");
            }
          });
}
