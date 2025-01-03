// =======================================
// flutter-app 核心模块 - 基于 shared_preferences.dart 的本地数据存储
// 开发者：zbc
// 创建时间：2021-??-??
// 上次维护时间：2023-05-17
// =======================================
// ignore_for_file: file_names, no_leading_underscores_for_local_identifiers
import 'dart:convert';
import 'package:shared_preferences/shared_preferences.dart';
// ==========================================================================
// 字典查询：
// serverUrl ------------------------------ web 主页服务器地址指针
// ==========================================================================

class LocalStorage {
  // 保存数据(键值型，直接保存为字符串型数据)
  static Future setValue(String key, String value) async {
    Future<SharedPreferences> _prefs = SharedPreferences.getInstance();
    final SharedPreferences prefs = await _prefs;
    return prefs.setString(key, value);
  }

  // 清除数据(简单地直接将该键删除)
  static Future clearValue(String key) async {
    Future<SharedPreferences> _prefs = SharedPreferences.getInstance();
    final SharedPreferences prefs = await _prefs;
    prefs.remove(key);
  }

  // 获取数据(键值型，直接根据键名获取字符串型数据)
  static Future getValue(String key) async {
    Future<SharedPreferences> _prefs = SharedPreferences.getInstance();
    final SharedPreferences prefs = await _prefs;
    return prefs.getString(key);
  }

  // -------------------------------------
  // 完全保存数据(数组型，直接保存为数组型数据)
  static Future setArrValue(String key, List value) async {
    Future<SharedPreferences> _prefs = SharedPreferences.getInstance();
    final SharedPreferences prefs = await _prefs;
    return prefs.setString(key, jsonEncode(value));
  }

  // 新增保存数据(数组型，添加新值加以保存)
  static Future addArrValue(String key, String value) async {
    Future<SharedPreferences> _prefs = SharedPreferences.getInstance();
    final SharedPreferences prefs = await _prefs;
    String? _currentResultStr = prefs.getString(key);
    List _result;
    if (_currentResultStr != null) {
      _result = jsonDecode(_currentResultStr);
    } else {
      _result = [];
    }
    _result.add(value);
    String result = jsonEncode(_result);
    return prefs.setString(key, result);
  }

  // 剔除保存数据(数组型，剔除掉已保存的数组中的某个确定元素)
  static Future removeArrValue(String key, String value) async {
    Future<SharedPreferences> _prefs = SharedPreferences.getInstance();
    final SharedPreferences prefs = await _prefs;
    String? _currentResultStr = prefs.getString(key);
    List _result;
    if (_currentResultStr != null) {
      _result = jsonDecode(_currentResultStr);
    } else {
      _result = [];
    }
    _result.remove(value);
    String result = jsonEncode(_result);
    return prefs.setString(key, result);
  }

  // 剔除保存数据(数组型，根据索引剔除掉已保存的数组中的某个元素)
  static Future removeArrValueIndex(String key, int i) async {
    Future<SharedPreferences> _prefs = SharedPreferences.getInstance();
    final SharedPreferences prefs = await _prefs;
    String? _currentResultStr = prefs.getString(key);
    List _result;
    if (_currentResultStr != null) {
      _result = jsonDecode(_currentResultStr);
    } else {
      _result = [];
    }
    _result.removeAt(i);
    String result = jsonEncode(_result);
    return prefs.setString(key, result);
  }

  // 获取数据(数组型，直接根据键名获取数组型数据)
  static Future getArrValue(String key) async {
    Future<SharedPreferences> _prefs = SharedPreferences.getInstance();
    final SharedPreferences prefs = await _prefs;
    String? _currentResultStr = prefs.getString(key);
    List _result;
    if (_currentResultStr != null) {
      _result = jsonDecode(_currentResultStr);
    } else {
      _result = [];
    }
    return _result;
  }
}
