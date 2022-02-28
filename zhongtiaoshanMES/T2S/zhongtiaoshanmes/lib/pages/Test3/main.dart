import 'dart:typed_data';

import 'package:flutter/material.dart';

class Tester3 extends StatefulWidget {
  const Tester3({Key? key}) : super(key: key);

  @override
  _CameraAppState createState() => _CameraAppState();
}

class _CameraAppState extends State<Tester3> {
  @override
  void initState() {
    super.initState();

    var args = ModalRoute.of(context)!.settings.arguments;
  }

  @override
  void dispose() {
    super.dispose();
  }

  @override
  Widget build(BuildContext context) {
    return Container();
  }
}
