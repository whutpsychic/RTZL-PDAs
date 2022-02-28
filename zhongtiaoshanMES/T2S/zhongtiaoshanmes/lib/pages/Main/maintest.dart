import 'package:flutter/material.dart';
import '../../core/dimensions.dart';
// import "./ImageGallery.dart";
import "./ScanPreview.dart";

class MainTest extends StatefulWidget {
  const MainTest({Key? key}) : super(key: key);
  @override
  State<StatefulWidget> createState() => MainPageState();
}

class MainPageState extends State<MainTest> {
  @override
  void initState() {
    super.initState();
  }

  @override
  void dispose() {
    super.dispose();
  }

  // 打开摄像头识别
  _onStartSpot() {
    // 跳转到相机识别页去
    Navigator.of(context)
        .push(MaterialPageRoute(builder: (context) => const ScanPreview()))
        .then((result) {
      // 之后
      print("-=-=-=-=-=-=-=-=-=-=-=- result -=-=-=-=-=-=-=-=-=-=-=");
      print(result);
    });
  }

  @override
  Widget build(BuildContext context) {
    double _screenWidth = Dimensions.getScreenWidth(context);
    double _screenHeight = Dimensions.getScreenHeight(context);

    return SizedBox(
        width: _screenWidth,
        height: _screenHeight,
        child: Column(
          mainAxisAlignment: MainAxisAlignment.center,
          crossAxisAlignment: CrossAxisAlignment.center,
          children: [
            GestureDetector(
              onTap: _onStartSpot,
              child: Container(
                width: 300,
                height: 80,
                color: Colors.red,
              ),
            ),
          ],
        ));
  }
}
