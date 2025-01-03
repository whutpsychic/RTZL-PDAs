// ignore_for_file: file_names
import 'dart:io';
import 'package:flutter/material.dart';
import 'package:flutter/cupertino.dart';

GlobalKey<_ProgressIndicatorState> key = GlobalKey();

class ModalProgress {
  static void addstep(double x) {
    key.currentState?.addstep(x);
  }

  static void setstep(double x) {
    key.currentState?.setstep(x);
  }

  static Future show(BuildContext context, String content) async {
    if (Platform.isIOS) {
      // print('当前运行的平台是 iOS');
      showGeneralDialog(
        barrierColor: Colors.black.withOpacity(0.5),
        transitionBuilder: (context, a1, a2, widget) {
          return ScaleTransition(
            scale: Tween<double>(begin: 0.9, end: 1.0).animate(a1),
            child: FadeTransition(
              opacity: Tween<double>(begin: 0.5, end: 1.0).animate(a1),
              child: CupertinoAlertDialog(
                title: Text(content),
                content: SizedBox(
                  height: 60,
                  child: Center(
                    child: ProgressIndicatorComponent(key: key),
                  ),
                ),
              ),
            ),
          );
        },
        transitionDuration: const Duration(milliseconds: 150),
        barrierDismissible: false,
        barrierLabel: '',
        context: context,
        pageBuilder: (context, animation1, animation2) {
          return Container();
        },
      );
    }
    // 其余平台一律按 Android 处理
    else {
      // print('当前运行的平台是 Android');
      showDialog<String>(
        context: context,
        builder: (BuildContext context) => AlertDialog(
          title: Text(content),
          content: SizedBox(
            height: 60,
            child: Center(
              // child: LinearProgressIndicator(value: value),
              child: ProgressIndicatorComponent(key: key),
            ),
          ),
        ),
        barrierDismissible: false,
      );
      key.currentState?.clear();
    }
  }

  static void hide(BuildContext context) {
    Navigator.of(context).pop();
  }
}

class ProgressIndicatorComponent extends StatefulWidget {
  const ProgressIndicatorComponent({super.key});

  @override
  State<ProgressIndicatorComponent> createState() => _ProgressIndicatorState();
}

class _ProgressIndicatorState extends State<ProgressIndicatorComponent>
    with TickerProviderStateMixin {
  late AnimationController controller;
  double _percent = 0.0;

  @override
  void initState() {
    controller = AnimationController(
      /// [AnimationController]s can be created with `vsync: this` because of
      /// [TickerProviderStateMixin].
      vsync: this,
      duration: const Duration(seconds: 2),
    )..addListener(() {
        setState(() {});
      });
    super.initState();
  }

  @override
  void dispose() {
    controller.dispose();
    super.dispose();
  }

  void addstep(double x) {
    _percent += x;
    controller.animateTo(_percent);
  }

  void setstep(double x) {
    controller.animateTo(x);
  }

  void clear() {
    _percent = 0;
    controller.animateTo(_percent);
  }

  @override
  Widget build(BuildContext context) {
    return LinearProgressIndicator(
      value: controller.value,
    );
  }
}
