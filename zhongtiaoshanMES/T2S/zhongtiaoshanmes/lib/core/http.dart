import 'dart:convert';
import 'package:http/http.dart' as http;

// 默认为请求10秒后超时
final Duration timeout = Duration(seconds: 10);

// 超时函数本体
Future tf = Future.delayed(timeout, () {
  String res = {"errcode": "1", "errmsg": "timeout"}.toString();
  return http.Response(res, 502);
});

class Http {
  // 一般请求
  static Future post(String url, {Object? body}) async {
    var uri = Uri.parse(url);

    Future f1 = http.post(
      uri,
      headers: {
        "Accept": "application/json",
        "content-type": "application/json"
      },
      body: body,
    );

    var result = await Future.any([f1, tf]).then((res) {
      return res;
    });
    print(' -------------------- http -------------------- ');
    print(result);
    print(' -------------------- http -------------------- ');
    return result;
  }
}

class HttpTool {
  static Map arranger(String x) {
    try {
      var result = jsonDecode(x);
      return result;
    } catch (err) {
      x = x.replaceAll('{', '{"');
      x = x.replaceAll(': ', '": "');
      x = x.replaceAll(', ', '", "');
      x = x.replaceAll('}', '"}');
      return jsonDecode(x);
    }
  }
}
