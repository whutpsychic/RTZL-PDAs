import 'package:flutter/material.dart';
import 'package:flutter/services.dart';

const String CHANNEL_STRING = "flutter.urovo/functions";

void main() {
  runApp(const MyApp());
}

class MyApp extends StatelessWidget {
  const MyApp({Key? key}) : super(key: key);

  @override
  Widget build(BuildContext context) {
    return MaterialApp(
      title: 'Flutter Demo',
      theme: ThemeData(primarySwatch: Colors.blue),
      home: const MyHomePage(title: 'Flutter Demo Home Page'),
    );
  }
}

class MyHomePage extends StatefulWidget {
  const MyHomePage({Key? key, required this.title}) : super(key: key);

  final String title;

  @override
  State<MyHomePage> createState() => _MyHomePageState();
}

class _MyHomePageState extends State<MyHomePage> {
  static const platform = MethodChannel(CHANNEL_STRING);

  // 获取扫描到的码字方法(flutter主动)
  static const String getCodeStr = "getScanCode";

  // 获取扫描到的码字方法(android主动)
  // static const String getCodeListener = "receiveScanCode";
  static const BroadcastChannel = MethodChannel(CHANNEL_STRING);

  // =========== state ===========
  String _code = "";
  // =========== state ===========

  //设置消息监听
  Future<void> nativeMessageListener() async {
    BroadcastChannel.setMethodCallHandler((resultCall) async {
      // 处理原生 Android iOS 发送过来的消息
      MethodCall call = resultCall;
      String method = call.method;
      String arguments = call.arguments;

      print(" ------------------- flutter duan ------------------- ");

      setState(() {
        _code = call.arguments;
      });
    });
  }

  Future<Null> _getScanCode() async {
    String res = "";
    try {
      final String result = await platform.invokeMethod(getCodeStr);
      res = "扫描结果为:$result";
    } catch (e) {
      print(" ==================== error ==================== ");
      print(e);
      print(" ==================== error ==================== ");
    }

    setState(() {
      _code = res;
    });
  }

  @override
  void initState() {
    super.initState();

    nativeMessageListener();
  }

  @override
  Widget build(BuildContext context) {
    return Scaffold(
      appBar: AppBar(title: Text(widget.title)),
      body: Center(
        child: Column(
          mainAxisAlignment: MainAxisAlignment.center,
          children: <Widget>[
            Text(_code),
          ],
        ),
      ),
      // This trailing comma makes auto-formatting nicer for build methods.
    );
  }
}
