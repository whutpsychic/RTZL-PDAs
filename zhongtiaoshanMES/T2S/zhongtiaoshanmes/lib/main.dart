import 'package:flutter/material.dart';
import 'package:flutter/services.dart';
import 'package:camera/camera.dart';
// ------------ core ------------
import './core/network_connectivity.dart';
// ------------ loading ------------
import "./UI/Spin/main.dart";
// ------------ pages ------------
// import "./pages/Test/main.dart";
// import "./pages/Test2/main.dart";
// import "./pages/Test3/main.dart";
import "./pages/Main/maintest.dart";
import "./pages/Main/main.dart";
import "./pages/Main/ScanPreview.dart";
// import "./pages/Main/main.dart";
import "./pages/IpConfig/main.dart";

late List<CameraDescription> cameras;

Future<void> main() async {
  // 链接照相机
  WidgetsFlutterBinding.ensureInitialized();
  cameras = await availableCameras();
  // 如果你想在 runApp() 之前调用一些代码，那么你就需要调这一行代码以开路
  WidgetsFlutterBinding.ensureInitialized();

  // ========= 初始化逻辑 =========
  Spin.setLoadingConfig();
  // ========= 初始化逻辑 =========

  // 强制竖屏
  SystemChrome.setPreferredOrientations(
      [DeviceOrientation.portraitUp, DeviceOrientation.portraitDown]);

  runApp(const Main());
}

class Main extends StatelessWidget {
  const Main({Key? key}) : super(key: key);

  @override
  Widget build(BuildContext context) {
    checkoutNetConnection();

    return MaterialApp(
      initialRoute: "/ip-config",
      routes: {
        // '/cam': (context) => CameraApp(), // 摄像头捕捉识别
        // '/test': (context) => const Tester(),  // 相册选图识别
        // '/test3': (context) => const Tester3(),  // 啥也不是
        // '/main': (context) => const MainTest(),
        '/main': (context) => const MainPage(),
        "/ip-config": (context) => IpConfig(),
      },
      builder: Spin.init(),
    );
  }
}
