import 'package:webview_flutter/webview_flutter.dart';
import '../appConfig.dart';

class Utils {
  static String getFnName() {
    return StaticConfig.preName;
  }

  static void runChannelJs(
      WebViewController? webViewController, String codeStr) {
    webViewController?.runJavaScript("${getFnName()}$codeStr");
  }
}
