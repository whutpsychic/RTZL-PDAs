import 'package:flutter/material.dart';
import 'package:flutter/services.dart';
// ------------ core ------------
import './core/network_connectivity.dart';
// ------------ loading ------------
import "./UI/Spin/main.dart";
// ------------ pages ------------
import "./pages/Main/main.dart";
import "./pages/IpConfig/main.dart";

void main() {
  // 如果你想在 runApp() 之前调用一些代码，那么你就需要调这一行代码以开路
  WidgetsFlutterBinding.ensureInitialized();

  // ========= 初始化逻辑 =========
  Spin.setLoadingConfig();
  // ========= 初始化逻辑 =========

  // 强制竖屏
  SystemChrome.setPreferredOrientations(
      [DeviceOrientation.portraitUp, DeviceOrientation.portraitDown]);

  runApp(Main());
}

class Main extends StatelessWidget {
  @override
  Widget build(BuildContext context) {
    checkoutNetConnection();

    return MaterialApp(
      initialRoute: "/main",
      routes: {
        '/main': (context) => MainPage(),
        "/ip-config": (context) => IpConfig(),
      },
      builder: Spin.init(),
    );
  }
}
