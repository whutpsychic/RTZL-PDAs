import 'package:flutter/material.dart';
import 'package:flutter/services.dart';

void main() {
  runApp(MyApp());
}

class MyApp extends StatelessWidget {
  // This widget is the root of your application.
  @override
  Widget build(BuildContext context) {
    return MaterialApp(
      title: 'Flutter Demo',
      theme: ThemeData(
        // This is the theme of your application.
        //
        // Try running your application with "flutter run". You'll see the
        // application has a blue toolbar. Then, without quitting the app, try
        // changing the primarySwatch below to Colors.green and then invoke
        // "hot reload" (press "r" in the console where you ran "flutter run",
        // or simply save your changes to "hot reload" in a Flutter IDE).
        // Notice that the counter didn't reset back to zero; the application
        // is not restarted.
        primarySwatch: Colors.blue,
      ),
      home: MyHomePage(title: 'Flutter Demo Home Page'),
    );
  }
}

class MyHomePage extends StatefulWidget {
  MyHomePage({Key key, this.title}) : super(key: key);
  final String title;

  @override
  _MyHomePageState createState() => _MyHomePageState();
}

class _MyHomePageState extends State<MyHomePage> {
  static const batteryChannel =
      const MethodChannel('rtzl_nlscan.flutter.io/key');

  int _counter = 0;

  String _batteryLevel = 'Unknown battery level.';

  // static Future<dynamic> invokNative(String method, {Map arguments}) async {
  //   if (arguments == null) {
  //     return await batteryChannel.invokeMethod(method);
  //   } else {
  //     return await batteryChannel.invokeMethod(method, arguments);
  //   }
  // }

  //设置消息监听
  Future<void> nativeMessageListener() async {
    batteryChannel.setMethodCallHandler((resultCall) {
      // 处理原生 Android iOS 发送过来的消息
      MethodCall call = resultCall;
      String method = call.method;
      String arguments = call.arguments;
      print(method);
      print(arguments);
      return;
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

  void _incrementCounter() {
    setState(() {
      _counter++;
    });
  }

  @override
  Widget build(BuildContext context) {
    return Scaffold(
      appBar: AppBar(
        title: Text(widget.title),
      ),
      body: Center(
        child: Column(
          mainAxisAlignment: MainAxisAlignment.center,
          children: <Widget>[
            Text(_batteryLevel ?? "没有字符串"),
            Text(
              'You have pushed the button this many times:',
            ),
            Text(
              '$_counter',
              style: Theme.of(context).textTheme.headline4,
            ),
          ],
        ),
      ),
      floatingActionButton: FloatingActionButton(
        onPressed: _incrementCounter,
        tooltip: 'Increment',
        child: Icon(Icons.add),
      ),
    );
  }
}
