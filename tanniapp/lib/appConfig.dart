// ignore_for_file: file_names
// ================ 控制阀文件 ================
// ==========================================
import './service/LocalStorage.dart';

enum AppMode { prodution, dev }

class Configure {
  // 调试模式
  // 会打开一些本不可被修改的设置途径
  static const bool debugging = false;

  // =================== dev 本地开发模式 ===================
  // 可调节的设置应该全开
  // 连接到本地开发环境
  // ✔ 本地地址
  // =================== production 产品模式 ===================
  // 与任何可调节的设置断绝
  // 连接到正式环境
  // ✔ 云上正式地址
  static const AppMode appMode = AppMode.dev;
}

// 最终控制结果
class AppConfig {
  // h5 最终地址
  static Future<String> getH5url() async {
    String? url = await LocalStorage.getValue("serverUrl");
    if (url != null) {
      return url;
    }

    switch (Configure.appMode) {
      case AppMode.prodution:
        return StaticConfig.productionH5url;
      case AppMode.dev:
        return StaticConfig.devH5url;
    }
  }

  // 默认的 h5 地址
  static String h5url = Configure.appMode == AppMode.prodution
      ? StaticConfig.productionH5url
      : StaticConfig.devH5url;
}

// 静态配置存储区
class StaticConfig {
  // h5 调试时的本地运行地址
  static const String devH5url = "http://192.168.1.71:8082/mobile/";
  // h5 产品运行时的地址
  static const String productionH5url = "http://10.18.47.16/mobile";

  // 复杂化命名前缀
  static const String preName = "zflutter";

  // 与 flutter-core 通讯时的参数分隔符
  static const String argsSpliter = "|_|";
}
