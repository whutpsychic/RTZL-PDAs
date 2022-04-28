import 'package:connectivity/connectivity.dart';

// connectivity 检测主函数
Future<void> checkoutNetConnection() async {
  var connectivityResult = await (Connectivity().checkConnectivity());
  if (connectivityResult == ConnectivityResult.mobile) {
    print("4g/5g");
    // I am connected to a mobile network.
  } else if (connectivityResult == ConnectivityResult.wifi) {
    // I am connected to a wifi network.
    print("wifi");
  } else if (connectivityResult == ConnectivityResult.none) {
    print("no network");
    // 手动提示去设置里面授权网络
  }
}
