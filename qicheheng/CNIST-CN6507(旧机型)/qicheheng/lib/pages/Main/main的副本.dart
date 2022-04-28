import 'package:flutter/material.dart';
import 'package:flutter/services.dart';
import 'package:webview_flutter/webview_flutter.dart';
import '../../core/dimensions.dart';
import '../../core/localStorage.dart';
import '../../config/Arguments.dart';

class MainPage extends StatefulWidget {
  @override
  State<StatefulWidget> createState() => MainPageState();
}

class MainPageState extends State<MainPage> {
  WebViewController? _webViewController;
  static const BroadcastChannel =
      const MethodChannel('rtzl_cnscan.flutter.io/key');

  //设置消息监听
  Future<void> nativeMessageListener() async {
    BroadcastChannel.setMethodCallHandler((resultCall) {
      // 处理原生 Android iOS 发送过来的消息
      MethodCall call = resultCall;
      String method = call.method;
      String arguments = call.arguments;
      print(method);
      print(arguments);

      _webViewController?.evaluateJavascript('test($arguments)');

      return Future.delayed(Duration(seconds: 1), () {});
    });
  }

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
          print(valueArr);
          String result =
              '${valueArr[0] ?? Arguments.defaultHttpType}://${valueArr[1] ?? Arguments.defaultProxyUrl}:${valueArr[2] ?? Arguments.defaultProxyPort}';
          _webViewController?.loadUrl(result);
        });
      }
    });
  }

  @override
  void initState() {
    super.initState();
    nativeMessageListener();
  }

  @override
  void dispose() {
    //Dispose broadcast
    super.dispose();
  }

  @override
  Widget build(BuildContext context) {
    double _screenWidth = Dimensions.getScreenWidth(context);
    double _screenHeight = Dimensions.getScreenHeight(context);

    return Container(
      width: _screenWidth,
      height: _screenHeight,
      child: WebView(
        // initialUrl: "https://www.baidu.com",
        initialUrl: "about:blank",
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
            print(" ======================= ");
            print(mainInfo);
            print(" ======================= ");
            if (mainInfo == "componentDidMount") {
              // _webViewController.evaluateJavascript('');
            } else if (mainInfo == "onTestClick") {
              print(" ======================= ");
              print("hey!hey!hey!hey!hey!hey!");
              print(" ======================= ");
            }
          });
}
