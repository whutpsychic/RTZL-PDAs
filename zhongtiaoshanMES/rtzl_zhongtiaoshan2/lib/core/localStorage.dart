// 本地数据存储
// based on shared_preferences
import 'package:shared_preferences/shared_preferences.dart';
// ==========================================================================
// request_type          网络请求模式 uri || url_port
// http_type             网络请求协议 http || https
// url                   网络请求地址 xxx.xxx.xxx.xxx
// port                  网络请求端口 xxxxx
// uri                   网络请求直接地址 www.baidu.com
// ==========================================================================

class LocalStorage {
  // 保存数据
  static Future setValue(String key, String value) async {
    Future<SharedPreferences> _prefs = SharedPreferences.getInstance();
    final SharedPreferences prefs = await _prefs;
    return prefs.setString(key, value);
  }

  static Future getValue(String key) async {
    Future<SharedPreferences> _prefs = SharedPreferences.getInstance();
    final SharedPreferences prefs = await _prefs;
    return prefs.getString(key);
  }
}
